package de.adorsys.multibanking.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.adorsys.docusafe.business.types.complex.DSDocument;
import org.adorsys.docusafe.business.types.complex.DocumentFQN;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;

import de.adorsys.multibanking.domain.AccountSynchPref;
import de.adorsys.multibanking.domain.AccountSynchResult;
import de.adorsys.multibanking.domain.BankAccessData;
import de.adorsys.multibanking.domain.BankAccessEntity;
import de.adorsys.multibanking.domain.BankAccountData;
import de.adorsys.multibanking.domain.BankAccountEntity;
import de.adorsys.multibanking.domain.BankEntity;
import de.adorsys.multibanking.domain.BookingEntity;
import de.adorsys.multibanking.domain.BookingFile;
import de.adorsys.multibanking.domain.UserData;
import de.adorsys.multibanking.exception.ResourceNotFoundException;
import de.adorsys.multibanking.service.base.BaseUserIdService;
import de.adorsys.multibanking.service.helper.BookingHelper;
import de.adorsys.multibanking.utils.FQNUtils;
import domain.BankAccount;
import domain.BankApi;
import domain.BankApiUser;
import domain.Booking;
import domain.LoadBookingsResponse;
import exception.InvalidPinException;
import spi.OnlineBankingService;
import utils.Utils;

/**
 * 
 * @author fpo 2018-03-17 12:16
 *
 */
@Service
public class BookingService extends BaseUserIdService {
	
	@Autowired
    private UserDataService uds;
	

    @Autowired
    private BankAccessService bankAccessService;
    @Autowired
    private BankAccountService bankAccountService;
    @Autowired
    private AnalyticsService analyticsService;
    @Autowired
    private BankService bankService;
    @Autowired
    private UserService userService;
    @Autowired
    private AccountSynchPrefService accountSynchService;
    @Autowired
    private OnlineBankingServiceProducer bankingServiceProducer;
    @Autowired
    private StandingOrderService standingOrderService;
    
    /**
     * Read and returns the booking file for a given period. Single bookings are not deserialized in
     * the memory of this JVM.
     * 
     * @param accessId
     * @param accountId
     * @param period
     * @return
     */
    public DSDocument getBookings(String accessId, String accountId, String period) {
    	AccountSynchResult accountSynchResult = accountSynchService.loadAccountSynchResult(accessId, accountId);
    	if(accountSynchResult.getBookingFileExts().contains(period))
    		throw new ResourceNotFoundException(Booking.class, 
    				FQNUtils.bookingFQN(accessId,accountId,period).getValue());
        return loadDocument(FQNUtils.bookingFQN(accessId,accountId,period));
    }

    public List<BookingEntity> listBookings(String accessId, String accountId, String period) {
    	AccountSynchResult accountSynchResult = accountSynchService.loadAccountSynchResult(accessId, accountId);
    	if(accountSynchResult.getBookingFileExts().contains(period))
    		throw new ResourceNotFoundException(Booking.class, 
    				FQNUtils.bookingFQN(accessId,accountId,period).getValue());
    	return load(FQNUtils.bookingFQN(accessId,accountId,period), listType())
    			.orElse(Collections.emptyList());
    }
    
    /**
     * - Get additional booking from the remote repository.
     * - Triggers analytics
     * 
     * @param accessId
     * @param accountId
     * @param bankApi
     * @param pin
     */
    public void syncBookings(String accessId, String accountId, BankApi bankApi, String pin) {
    	// Set the synch status.
        bankAccountService.updateSyncStatus(accessId, accountId, BankAccount.SyncStatus.SYNC);
        accountSynchService.updateSyncStatus(accessId, accountId, BankAccount.SyncStatus.SYNC);
        UserData userData = uds.load();
        BankAccessEntity bankAccess = userData.bankAccessData(accessId).getBankAccess();
        BankAccountEntity bankAccount = userData.bankAccountData(accessId, accountId).getBankAccount();
        try {
            LoadBookingsResponse response = loadBookingsOnline(bankApi, bankAccess, bankAccount, pin);

            bankAccount.setBankAccountBalance(response.getBankAccountBalance());
            if (bankAccess.isStoreBookings()) {
                bankAccount.setLastSync(LocalDateTime.now());
                bankAccountService.saveBankAccount(bankAccount);
            }

            if (!bankAccess.isTemporary()) {
                //update bankaccess, passportstate changed
                bankAccessService.updateBankAccess(bankAccess);
            }

            processBookings(bankAccess, bankAccount, response);
        } catch (Exception e) {
            LoggerFactory.getLogger(getClass()).error("sync bookings failed", e);
            throw e;
        } finally {
            bankAccountService.updateSyncStatus(bankAccess.getId(), bankAccount.getId(), BankAccount.SyncStatus.PENDING);
            accountSynchService.updateSyncStatus(bankAccess.getId(), bankAccount.getId(), BankAccount.SyncStatus.PENDING);
        }
    }

    private void processBookings(BankAccessEntity bankAccess, 
    		BankAccountEntity bankAccount, LoadBookingsResponse response) {
    	
    	AccountSynchResult synchResult = accountSynchService.loadAccountSynchResult(bankAccess.getId(), bankAccount.getId());
    	AccountSynchPref accountSynchPref = accountSynchService.findAccountSynchPref(bankAccess.getId(), bankAccount.getId());
    	
        Map<String, List<BookingEntity>> bookings = BookingHelper.mapBookings(bankAccount, accountSynchPref, response.getBookings());
        
        // First update preference set.
//        accountSynchService.storeAccountSynchResult(bankAccess.getId(), bankAccount.getId(), synchResult.update(bookings.keySet()));
        
        Map<String, BookingFile> bookingFileMap = synchResult.bookingFileMap();
        Set<Entry<String,List<BookingEntity>>> entrySet = bookings.entrySet();
        for (Entry<String, List<BookingEntity>> entry : entrySet) {
        	List<BookingEntity> bookingEntities = entry.getValue();
            bookingEntities.forEach(booking ->
            response.getStandingOrders()
                    .stream()
                    .filter(so -> so.getAmount().negate().compareTo(booking.getAmount()) == 0 &&
                            Utils.inCycle(booking.getValutaDate(), so.getExecutionDay()) &&
                            Utils.usageContains(booking.getUsage(), so.getUsage())
                    )
                    .findFirst()
                    .ifPresent(standingOrder -> {
                        booking.setOtherAccount(standingOrder.getOtherAccount());
                        booking.setStandingOrder(true);
                    }));
            String period = entry.getKey();
			DocumentFQN bookingFQN = FQNUtils.bookingFQN(bankAccess.getId(),bankAccount.getId(),period);
			List<BookingEntity> existingBookings = load(bookingFQN, listType())
					.orElse(Collections.emptyList());
            bookingEntities = mergeBookings(existingBookings,bookingEntities);

            // Store meta data
            BookingFile bookingFile = bookingFileMap.get(period);
            if(bookingFile==null){
            	bookingFile = new BookingFile();
            	bookingFile.setPeriod(period);
            	synchResult.update(Collections.singletonList(bookingFile));            	
            }
            bookingFile.setNumberOfRecords(bookingEntities.size());
            accountSynchService.storeAccountSynchResult(bankAccess.getId(),bankAccount.getId(), synchResult);

            // Sort and store bookings
            Collections.sort(bookingEntities, (o1, o2) -> o2.getBookingDate().compareTo(o1.getBookingDate()));
            if (bankAccess.isStoreBookings()) {
            	store(bookingFQN, listType(), bookingEntities);
            }
		}
        standingOrderService.saveStandingOrders(bankAccount, response.getStandingOrders());
        bankAccountService.updateSyncStatus(bankAccount.getBankAccessId(), bankAccount.getId(), BankAccount.SyncStatus.READY);
        accountSynchService.updateSyncStatus(bankAccount.getBankAccessId(), bankAccount.getId(), BankAccount.SyncStatus.READY);
        
        if (bankAccess.isCategorizeBookings() || bankAccess.isStoreAnalytics() || bankAccess.isStoreAnonymizedBookings()) {
        	// create analytics
        	// identify and store contracts
        	// anonymize and store booking
        	analyticsService.startAccountAnalytics(bankAccount.getBankAccessId(), bankAccount.getId());
        }
    }

    private LoadBookingsResponse loadBookingsOnline(BankApi bankApi, BankAccessEntity bankAccess, BankAccountEntity bankAccount, String pin) {
        BankApiUser bankApiUser = userService.checkApiRegistration(bankApi, bankAccess.getBankCode());

        OnlineBankingService onlineBankingService = checkAndGetOnlineBankingService(bankAccess, bankAccount, pin, bankApiUser);

        String mappedBlz = bankService.findByBankCode(bankAccess.getBankCode())
                .orElseThrow(() -> new ResourceNotFoundException(BankEntity.class, bankAccess.getBankCode())).getBlzHbci();
        
        try {
            LoadBookingsResponse response = onlineBankingService.loadBookings(bankApiUser, bankAccess, mappedBlz, bankAccount, pin);
            response.setOnlineBankingService(onlineBankingService);
            return response;
        } catch (InvalidPinException e) {
            bankAccessService.setInvalidPin(bankAccess.getId());
            throw new de.adorsys.multibanking.exception.InvalidPinException(bankAccess.getId());
        }
    }

	private List<BookingEntity> mergeBookings(List<BookingEntity> dbBookings, List<BookingEntity> newBookings) {
        dbBookings.addAll(newBookings);

        return dbBookings
                .stream()
                .collect(Collectors.collectingAndThen(Collectors.toCollection(() ->
                        new TreeSet<>(Comparator.comparing(Booking::getExternalId))), ArrayList::new));
    }

    private OnlineBankingService checkAndGetOnlineBankingService(BankAccessEntity bankAccess, BankAccountEntity bankAccount, String pin, BankApiUser bankApiUser) {
        OnlineBankingService onlineBankingService = bankingServiceProducer.getBankingService(bankApiUser.getBankApi());

        //external (figo, finapi) account must exist, otherwise loading bookings will not work
        if (onlineBankingService.externalBankAccountRequired()) {
            checkExternalBankAccountExists(bankAccess, bankAccount, pin, bankApiUser, onlineBankingService);
        }

        return onlineBankingService;
    }

    private void checkExternalBankAccountExists(BankAccessEntity bankAccess, BankAccountEntity bankAccount, String pin, BankApiUser bankApiUser, OnlineBankingService onlineBankingService) {
        UserData userData = uds.load();
    	
        String externalAccountId = bankAccount.getExternalIdMap().get(onlineBankingService.bankApi());
        //account not created by given bank-api, account must be created, otherwise loading bookings will not work
        if (externalAccountId == null) {
            String blzHbci = bankService.findByBankCode(bankAccess.getBankCode())
                    .orElseThrow(() -> new ResourceNotFoundException(BankEntity.class, bankAccess.getBankCode())).getBlzHbci();
            List<BankAccount> apiBankAccounts = onlineBankingService.loadBankAccounts(bankApiUser, bankAccess, blzHbci, pin, bankAccess.isStorePin());
            BankAccessData bankAccessData = userData.bankAccessData(bankAccess.getId());
	        Collection<BankAccountData> dbBankAccounts = bankAccessData.getBankAccounts().values();
//            List<BankAccountEntity> dbBankAccounts = bankAccountService.loadForBankAccess(bankAccess.getId());
            apiBankAccounts.forEach(apiBankAccount -> {
                dbBankAccounts.forEach(dbBankAccountData -> {
                	BankAccountEntity dbBankAccount = dbBankAccountData.getBankAccount();
                    if (apiBankAccount.getAccountNumber().equals(dbBankAccount.getAccountNumber())) {
                        dbBankAccount.externalId(onlineBankingService.bankApi(), apiBankAccount.getExternalIdMap().get(onlineBankingService.bankApi()));
                        if (bankAccess.getId().equals(dbBankAccount.getId())) {
                            bankAccess.externalId(onlineBankingService.bankApi(), apiBankAccount.getExternalIdMap().get(onlineBankingService.bankApi()));
                        }
                    }
                });
            });
//            bankAccountService.saveBankAccounts(bankAccess.getId(),dbBankAccounts);
            uds.store(userData);
        }
        
    }

	private static TypeReference<List<BookingEntity>> listType(){
		return new TypeReference<List<BookingEntity>>() {};
	}
}
