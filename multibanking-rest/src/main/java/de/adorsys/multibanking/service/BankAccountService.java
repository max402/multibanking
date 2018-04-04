package de.adorsys.multibanking.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.adorsys.multibanking.domain.BankAccessCredentials;
import de.adorsys.multibanking.domain.BankAccessData;
import de.adorsys.multibanking.domain.BankAccessEntity;
import de.adorsys.multibanking.domain.BankAccountData;
import de.adorsys.multibanking.domain.BankAccountEntity;
import de.adorsys.multibanking.domain.BankEntity;
import de.adorsys.multibanking.domain.UserData;
import de.adorsys.multibanking.exception.BankAccessAlreadyExistException;
import de.adorsys.multibanking.exception.InvalidBankAccessException;
import de.adorsys.multibanking.exception.ResourceNotFoundException;
import de.adorsys.multibanking.service.base.BaseUserIdService;
import domain.BankAccount;
import domain.BankAccount.SyncStatus;
import domain.BankApi;
import domain.BankApiUser;
import exception.InvalidPinException;
import spi.OnlineBankingService;

@Service
public class BankAccountService extends BaseUserIdService {
    private static final Logger log = LoggerFactory.getLogger(BankAccessService.class);

	@Autowired
    private UserDataService uds;
    
    @Autowired
    private OnlineBankingServiceProducer bankingServiceProducer;
    @Autowired
    private BankService bankService;
    @Autowired
    private UserService userService;
	
	public Optional<BankAccountEntity> loadBankAccount(String accessId, String accountId) {
		Optional<BankAccountData> bankAccountData = uds.load().bankAccessData(accessId).getBankAccount(accountId);
		if(bankAccountData.isPresent()) return Optional.of(bankAccountData.get().getBankAccount());
		return Optional.empty();
	}
	
    public void synchBankAccounts(BankAccessEntity bankAccess, BankAccessCredentials credentials){
    	List<BankAccountEntity> bankAccounts = loadFromBankingAPI(bankAccess, credentials, null);
        
        if (bankAccounts.size() == 0) {
            throw new InvalidBankAccessException(bankAccess.getBankCode());
        }
        UserData userData = uds.load();
        BankAccessData bankAccessData = userData.bankAccessData(bankAccess.getId());
        Map<String, BankAccountData> bankAccountDataMap = bankAccessData.getBankAccounts();
        bankAccounts.forEach(account -> {
        	account.bankAccessId(bankAccess.getId());
        	BankAccountData accountData = bankAccountDataMap.get(account.getId());
        	if(accountData==null){
        		accountData = new BankAccountData();
        		bankAccountDataMap.put(account.getId(), accountData);
        	}
        	accountData.setBankAccount(account);
        });
        uds.store(userData);
        log.info("[{}] accounts for connection [{}] created.", bankAccounts.size(), bankAccess.getId());
    }
	
    public List<BankAccountEntity> loadFromBankingAPI(BankAccessEntity bankAccess, BankAccessCredentials credentials, BankApi bankApi) {
        OnlineBankingService onlineBankingService = bankApi != null
                ? bankingServiceProducer.getBankingService(bankApi)
                : bankingServiceProducer.getBankingService(bankAccess.getBankCode());

        if (!onlineBankingService.bankSupported(bankAccess.getBankCode())) {
            throw new InvalidBankAccessException(bankAccess.getBankCode());
        }

        BankApiUser bankApiUser = userService.checkApiRegistration(bankApi, bankAccess.getBankCode());
        String blzHbci = bankService.findByBankCode(bankAccess.getBankCode())
                .orElseThrow(() -> new ResourceNotFoundException(BankEntity.class, bankAccess.getBankCode())).getBlzHbci();

        List<BankAccount> bankAccounts;
        try {
            bankAccounts = onlineBankingService.loadBankAccounts(bankApiUser, bankAccess, blzHbci, credentials.getPin(), false);
        } catch (InvalidPinException e) {
            throw new de.adorsys.multibanking.exception.InvalidPinException(bankAccess.getId());
        }

        if (onlineBankingService.bankApi() == BankApi.FIGO) {
            filterAccounts(bankAccess, onlineBankingService, bankAccounts);
        }

        List<BankAccountEntity> bankAccountEntities = new ArrayList<>();

        bankAccounts.forEach(source -> {
            BankAccountEntity target = new BankAccountEntity();
            BeanUtils.copyProperties(source, target);
            target.setUserId(bankAccess.getUserId());
            bankAccountEntities.add(target);
        });

        return bankAccountEntities;
    }
    
	public void updateSyncStatus(String bankAccessId, String accountId, final BankAccount.SyncStatus syncStatus) {
		UserData userData = uds.load();
		userData.bankAccountData(bankAccessId, accountId).getBankAccount().setSyncStatus(syncStatus);
		uds.store(userData);
	}
	
	/**
	 * Saves an existing bank account. Will not add the bank account if absent.
	 * Adding a bank account only occurs thru synch.
	 * 
	 * @param in
	 */
	public void saveBankAccount(BankAccountEntity in){
		UserData userData = uds.load();
		userData.bankAccountData(in.getBankAccessId(), in.getId()).setBankAccount(in);
		uds.store(userData);
	}

	public void saveBankAccounts(String accessId, List<BankAccountEntity> accounts){
		UserData userData = uds.load();
		for (BankAccountEntity in : accounts) {
			userData.bankAccountData(in.getBankAccessId(), in.getId()).setBankAccount(in);
		}
		uds.store(userData);
	}
	
	public boolean exists(String accessId, String accountId) {
		UserData userData = uds.load();
		return userData.bankAccessData(accessId).getBankAccounts().containsKey(accountId);
	}
	
	public SyncStatus getSyncStatus(String accessId, String accountId) {
		UserData userData = uds.load();
		return userData.bankAccountData(accessId, accountId).getBankAccount().getSyncStatus();
	}
    
    private void filterAccounts(BankAccessEntity bankAccess, OnlineBankingService onlineBankingService, List<BankAccount> bankAccounts) {
    	UserData userData = uds.load();
    	Collection<BankAccountData> userBankAccounts = userData.bankAccessData(bankAccess.getId()).getBankAccounts().values();
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

//
//	private static class SetStatusFnct implements Function<BankAccountEntity, Void> {
//		private BankAccount.SyncStatus syncStatus;
//		private SetStatusFnct(SyncStatus syncStatus) {
//			this.syncStatus = syncStatus;
//		}
//		@Override
//		public Void apply(BankAccountEntity t) {
//			t.setSyncStatus(syncStatus); 
//			return null;
//		}
//	}
//
//	private static TypeReference<List<BankAccountEntity>> listType(){
//		return new TypeReference<List<BankAccountEntity>>() {};
//	}
}
