package de.adorsys.multibanking.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.adorsys.docusafe.business.DocumentSafeService;
import org.adorsys.docusafe.business.types.complex.DSDocument;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.adorsys.multibanking.auth.UserContext;
import de.adorsys.multibanking.auth.UserObjectPersistenceService;
import de.adorsys.multibanking.domain.AccountAnalyticsEntity;
import de.adorsys.multibanking.domain.AccountSynchPref;
import de.adorsys.multibanking.domain.BankAccessCredentials;
import de.adorsys.multibanking.domain.BankAccessData;
import de.adorsys.multibanking.domain.BankAccessEntity;
import de.adorsys.multibanking.domain.BankAccountData;
import de.adorsys.multibanking.domain.BankAccountEntity;
import de.adorsys.multibanking.domain.BankEntity;
import de.adorsys.multibanking.domain.ContractEntity;
import de.adorsys.multibanking.domain.StandingOrderEntity;
import de.adorsys.multibanking.domain.UserData;
import de.adorsys.multibanking.domain.UserEntity;
import de.adorsys.multibanking.exception.BankAccessAlreadyExistException;
import de.adorsys.multibanking.exception.InvalidBankAccessException;
import de.adorsys.multibanking.exception.ResourceNotFoundException;
import de.adorsys.multibanking.exception.UserNotFoundException;
import de.adorsys.multibanking.service.producer.OnlineBankingServiceProducer;
import de.adorsys.multibanking.utils.FQNUtils;
import de.adorsys.multibanking.utils.Ids;
import domain.BankAccount;
import domain.BankApi;
import domain.BankApiUser;
import domain.StandingOrder;
import spi.OnlineBankingService;

/**
 * Manage Access to the user data. Manages all state information for this user account.
 * 
 * Consumer shall read this once and have all information need to initialize a user interface.
 * 
 * @author fpo 2018-03-17 08:38
 *
 */
@Service
public class BankDataService {
    private final static Logger LOGGER = LoggerFactory.getLogger(BankDataService.class);
	private final UserObjectPersistenceService uos;
	private final BankService bankService;
	private final BankAccessCredentialService credentialService; 
	private final OnlineBankingServiceProducer bankingServiceProducer;
	private final SynchBankAccountsService synchBankAccountService;

    public BankDataService(UserContext userContext, BankService bankService, BankAccessCredentialService credentialService,
            OnlineBankingServiceProducer bankingServiceProducer, SynchBankAccountsService synchBankAccountService, ObjectMapper objectMapper, DocumentSafeService documentSafeService) {
        this.uos = new UserObjectPersistenceService(userContext, objectMapper, documentSafeService);
        this.bankService = bankService;
        this.credentialService = credentialService;
        this.bankingServiceProducer = bankingServiceProducer;
        this.synchBankAccountService = synchBankAccountService;
    }

    public UserData load(){
		return uos.load(FQNUtils.userDataFQN(), valueType())
				.orElseThrow(() -> new UserNotFoundException(uos.userId()));
	}
	
	public boolean exists(){
		return uos.documentExists(FQNUtils.userDataFQN(), valueType());
	}

	public void store(UserData userData){
		uos.store(FQNUtils.userDataFQN(), valueType(), userData);		
	}
	
    public DSDocument loadDocument() {
        return uos.readDocument(FQNUtils.userDataFQN(), valueType());
    }
	
	
    /**
     * Returns the user entity or create one if the user does not exist.
     */
    public UserData createUser(Date expire) {
    	UserEntity userEntity = new UserEntity();
    	userEntity.setApiUser(new ArrayList<>());
    	userEntity.setId(uos.userId());
    	userEntity.setExpireUser(expire);
    	
    	UserData userData = new UserData();
    	userData.setUserEntity(userEntity);
    	store(userData);
    	return userData;
    }
	
	private static final TypeReference<UserData> valueType(){
		return new TypeReference<UserData>() {};
	}
	
	//================= Bank Access =====================//
    /**
     * Create a bank access
     * - load and store bank accounts
     *
     * @param bankAccess
     * @return
     */
    public BankAccessEntity createBankAccess(BankAccessEntity bankAccess) {
        // Set user and access id
        bankAccess.setUserId(uos.userId());
        // Set an accessId if none.
        if(StringUtils.isBlank(bankAccess.getId())){
            bankAccess.setId(Ids.uuid());
        } else {
            // Check bank Access with Id does not exists.
            if(accessExists(bankAccess.getId())){
                throw new BankAccessAlreadyExistException(bankAccess.getId());
            }
        }

        BankAccessCredentials credentials = BankAccessCredentials.cloneCredentials(bankAccess);
        // Clean credentials
        BankAccessCredentials.cleanCredentials(bankAccess);

        // disect credentials
        if (bankAccess.isStorePin()) {
            credentialService.store(credentials);
        }

        // store bank access
        storeBankAccess(bankAccess);

        try {
            // pull and store bank accounts
            synchBankAccounts(bankAccess, credentials);
        } catch (exception.InvalidPinException e){
            // Set pin valid state to false.
            if (bankAccess.isStorePin()) {
                credentialService.invalidate(credentials);
            }
            throw new de.adorsys.multibanking.exception.InvalidPinException(bankAccess.getId());
        } catch (de.adorsys.multibanking.exception.InvalidPinException e){
            if (bankAccess.isStorePin()) {
                credentialService.invalidate(credentials);
            }
            throw e;
        }

        return bankAccess;
    }

    /**
     * Update the bank access object.
     * Credentials are reset but not updated. USe another interface for managing credentials.
     *
     * @param bankAccessEntity
     */
    public void updateBankAccess(BankAccessEntity bankAccessEntity) {
        storeBankAccess(bankAccessEntity);
    }

    public boolean deleteBankAccess(String accessId) {
        return deleteBankAccessInternal(accessId, load());
    }

    /**
     * Check existence by checking if the file containing the list of bank accounts exits.
     *
     * @param accessId
     * @return
     */
    public boolean accessExists(String accessId){
        UserData userData = load();
        return userData.containsKey(accessId);
    }

    /*
     * Clean bank access credential before storage.
     * @param bankAccess
     */
    private void storeBankAccess(BankAccessEntity bankAccess) {
        BankAccessCredentials.cleanCredentials(bankAccess);
        UserData userData;
        if(!exists()){
            userData = createUser(null);
        } else {
            userData = load();
        }
        BankAccessData accessData = userData.getBankAccess(bankAccess.getId())
                .orElseGet(() -> {
                    BankAccessData b = new BankAccessData();
                    userData.put(bankAccess.getId(), b);
                    return b;
                });

        accessData.setBankAccess(bankAccess);
        store(userData);
    }

    private boolean deleteBankAccessInternal(String accessId, UserData userData) {
        BankAccessData accessData = userData.remove(accessId);
        if(accessData!=null){
            store(userData);
            uos.markDirForDeletion(FQNUtils.bankAccessDirFQN(accessId));
            removeRemoteRegistrations(accessData, userData);
            return true;
        }
        return false;
    }
    
    private void removeRemoteRegistrations(BankAccessData accessData, UserData userData) {
        // Load bank Accounts
        List<BankAccountData> bankAccountDataList = accessData.getBankAccounts();
        UserEntity userEntity = userData.getUserEntity();

        bankAccountDataList.stream().forEach(bankAccountData -> {
            BankAccountEntity bankAccountEntity = bankAccountData.getBankAccount();
            bankAccountEntity.getExternalIdMap().keySet().forEach(bankApi -> {
                OnlineBankingService bankingService = bankingServiceProducer.getBankingService(bankApi);
                //remove remote bank api user
                if (bankingService.userRegistrationRequired()) {
                    BankApiUser bankApiUser = userEntity.getApiUser()
                            .stream()
                            .filter(apiUser -> apiUser.getBankApi() == bankApi)
                            .findFirst()
                            .orElseThrow(() -> new ResourceNotFoundException(BankApiUser.class, bankApi.toString()));
                    bankingService.removeBankAccount(bankAccountEntity, bankApiUser);
                }
            });
        });
    }
    
    
    // =============== Bank Account ==================
    public void synchBankAccounts(BankAccessEntity bankAccess, BankAccessCredentials credentials){
        synchBankAccountService.synchBankAccounts(bankAccess, credentials, this);
    }

    public List<BankAccountEntity> loadFromBankingAPI(BankAccessEntity bankAccess, 
            BankAccessCredentials credentials, BankApi bankApi) {
        OnlineBankingService onlineBankingService = bankApi != null
                ? bankingServiceProducer.getBankingService(bankApi)
                : bankingServiceProducer.getBankingService(bankAccess.getBankCode());

        if (!onlineBankingService.bankSupported(bankAccess.getBankCode())) {
            throw new InvalidBankAccessException(bankAccess.getBankCode());
        }
        
        UserData userData = load();
        BankApiUser bankApiUser = bankingServiceProducer.checkApiRegistration(bankApi, bankAccess.getBankCode(), userData);
        String blzHbci = bankService.findByBankCode(bankAccess.getBankCode())
                .orElseThrow(() -> new ResourceNotFoundException(BankEntity.class, bankAccess.getBankCode())).getBlzHbci();

        List<BankAccount> bankAccounts;
        try {
            bankAccounts = onlineBankingService.loadBankAccounts(bankApiUser, bankAccess, blzHbci, credentials.getPin(), false);
        } catch (exception.InvalidPinException e) {
            throw new de.adorsys.multibanking.exception.InvalidPinException(bankAccess.getId());
        }

        if (onlineBankingService.bankApi() == BankApi.FIGO) {
            filterAccounts(bankAccess, onlineBankingService, bankAccounts);
        }

        List<BankAccountEntity> bankAccountEntities = new ArrayList<>();

        bankAccounts.forEach(source -> {
            BankAccountEntity target = new BankAccountEntity();
            target.id(source.getIban());
            BeanUtils.copyProperties(source, target);
            target.setUserId(bankAccess.getUserId());
            bankAccountEntities.add(target);
        });
        store(userData);
        return bankAccountEntities;
    }

    public BankAccountData loadBankAccount(String accessId, String accountId) {
        UserData userData = load();
        return userData.bankAccountDataOrException(accessId, accountId);
    }
    
    /**
     * Saves an existing bank account. Will not add the bank account if absent.
     * Adding a bank account only occurs thru synch.
     *
     * @param in
     */
    public void saveBankAccount(BankAccountEntity in){
        UserData userData = load();
        userData.bankAccountDataOrException(in.getBankAccessId(), in.getId()).setBankAccount(in);
        store(userData);
    }

    public void saveBankAccounts(String accessId, List<BankAccountEntity> accounts){
        UserData userData = load();
        for (BankAccountEntity in : accounts) {
            userData.bankAccountDataOrException(in.getBankAccessId(), in.getId()).setBankAccount(in);
        }
        store(userData);
    }

    public boolean accountExists(String accessId, String accountId) {
        UserData userData = load();
        return userData.bankAccessDataOrException(accessId).containsKey(accountId);
    }

//  public SyncStatus getSyncStatus(String accessId, String accountId) {
//      UserData userData = uds.load();
//      return userData.bankAccountData(accessId, accountId).getBankAccount().getSyncStatus();
//  }

    public AccountSynchPref loadAccountLevelSynchPref(String accessId, String accountId){
        return load().bankAccountDataOrException(accessId, accountId).getAccountSynchPref();
    }
    public void storeAccountLevelSynchPref(String accessId, String accountId, AccountSynchPref pref){
        UserData userData = load();
        userData.bankAccountDataOrException(accessId, accountId).setAccountSynchPref(pref);
        store(userData);
    }

    public AccountSynchPref loadAccessLevelSynchPref(String accessId){
        return load().bankAccessDataOrException(accessId).getAccountSynchPref();
    }
    public void storeAccessLevelSynchPref(String accessId, AccountSynchPref pref){
        UserData userData = load();
        userData.bankAccessDataOrException(accessId).setAccountSynchPref(pref);
        store(userData);
    }

    public AccountSynchPref loadUserLevelSynchPref(){
        return load().getAccountSynchPref();
    }
    public void storeUserLevelSynchPref(AccountSynchPref pref){
        UserData userData = load();
        userData.setAccountSynchPref(pref);
        store(userData);
    }

    /**
     * Search the neares account synch preference for the given account
     * @param id
     * @param id2
     * @return
     */
    public AccountSynchPref findAccountSynchPref(String accessId, String accountId) {
        AccountSynchPref synchPref = loadAccountLevelSynchPref(accessId, accountId);
        if(synchPref==null)
            synchPref = loadAccessLevelSynchPref(accessId);
        if(synchPref==null)
            synchPref = loadUserLevelSynchPref();
        if(synchPref==null)
            synchPref = new AccountSynchPref();

        return synchPref;
    }

    /**
     * Store standing orders in the user data record. Uses the delivered orderId to identify
     * existing records and exchange them.
     *
     * @param bankAccount
     * @param standingOrders
     */
    public void saveStandingOrders(BankAccountEntity bankAccount, List<StandingOrder> standingOrders) {
        UserData userData = load();
        Map<String, StandingOrderEntity> standingOrdersMap = userData.bankAccountDataOrException(bankAccount.getBankAccessId(), bankAccount.getId()).getStandingOrders();
        standingOrders.stream()
                .map(standingOrder -> {
                    // Assign an order id if none.
                    if(StringUtils.isBlank(standingOrder.getOrderId())){
                        standingOrder.setOrderId(Ids.uuid());
                    }

                    // Check existence of this standing order in the user data record.
                    // Instantiate and add one if none.
                    StandingOrderEntity target = standingOrdersMap.get(standingOrder.getOrderId());
                    if(target==null){
                        target = new StandingOrderEntity();
                        Ids.id(target);
                        standingOrdersMap.put(standingOrder.getOrderId(), target);
                        target.setAccountId(bankAccount.getId());
                        target.setUserId(bankAccount.getUserId());
                    }

                    // Update the record.
                    BeanUtils.copyProperties(standingOrder, target);
                    return target;
                });
        store(userData);
    }

    /**
     * Replace all contracts associated with the given bank account.
     * 
     * We assume to contracts given are associated with the bank account.
     *
     */
    public void saveContracts(String accountId, List<ContractEntity> contractEntities){
        UserData userData = load();
        BankAccountData bankAccountData = findBankAccountData(userData, accountId);
        if(bankAccountData==null) throw new ResourceNotFoundException(BankAccountData.class, accountId);
        // Set ids.
        BankAccountEntity bankAccount = bankAccountData.getBankAccount();
        contractEntities.stream().forEach(c -> {
            c.setAccessId(bankAccount.getBankAccessId());
            c.setUserId(bankAccount.getUserId());
            c.setAccountId(bankAccount.getId());
        });

        bankAccountData.setContracts(new ArrayList<>(contractEntities));
        store(userData);
    }

    public void saveAccountAnalytics(String accountId, AccountAnalyticsEntity analytic){
        UserData userData = load();
        BankAccountData bankAccountData = findBankAccountData(userData, accountId);
        if(bankAccountData==null) throw new ResourceNotFoundException(BankAccountData.class, accountId);
        BankAccountEntity bankAccount = bankAccountData.getBankAccount();
        analytic.setUserId(bankAccount.getUserId());
        analytic.setAccountId(bankAccount.getId());
        bankAccountData.setAnalytic(analytic);
        store(userData);
    }
    
    private BankAccountData findBankAccountData(UserData userData, String accountId){
        List<BankAccessData> bankAccesses = userData.getBankAccesses();
        for (BankAccessData b : bankAccesses) {
            Optional<BankAccountData> bankAccount = b.getBankAccount(accountId);
            if(bankAccount.isPresent()) return bankAccount.get();
        }
        return null;
    }

    private void filterAccounts(BankAccessEntity bankAccess, OnlineBankingService onlineBankingService, List<BankAccount> bankAccounts) {
        UserData userData = load();
        List<BankAccountData> userBankAccounts = userData.bankAccessDataOrException(bankAccess.getId()).getBankAccounts();
//        List<BankAccountEntity> userBankAccounts = loadForBankAccess(bankAccess.getId());

        //filter out previous created accounts
        Iterator<BankAccount> accountIterator = bankAccounts.iterator();
        while (accountIterator.hasNext()) {
            BankAccount newAccount = accountIterator.next();
            userBankAccounts.stream().filter(bankAccountData -> {
                String newAccountExternalID = newAccount.getExternalIdMap().get(onlineBankingService.bankApi());
                String existingAccountExternalID = bankAccountData.getBankAccount().getExternalIdMap().get(onlineBankingService.bankApi());

                return newAccountExternalID.equals(existingAccountExternalID);
            }).findFirst().ifPresent(bankAccountEntity -> {
                accountIterator.remove();
            });
        }

        //all accounts created in the past
        if (bankAccounts.size() == 0) {
            throw new BankAccessAlreadyExistException(bankAccess.getId());
        }

        bankAccess.setBankName(bankAccounts.get(0).getBankName());
    }
    
	
}
