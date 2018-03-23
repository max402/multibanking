package de.adorsys.multibanking.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.adorsys.docusafe.business.types.complex.DocumentFQN;
import org.adorsys.docusafe.business.types.complex.UserIDAuth;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;

import de.adorsys.multibanking.domain.BankAccessCredentials;
import de.adorsys.multibanking.domain.BankAccessEntity;
import de.adorsys.multibanking.domain.BankAccountEntity;
import de.adorsys.multibanking.domain.BankEntity;
import de.adorsys.multibanking.exception.BankAccessAlreadyExistException;
import de.adorsys.multibanking.exception.InvalidBankAccessException;
import de.adorsys.multibanking.exception.ResourceNotFoundException;
import de.adorsys.multibanking.service.base.BaseService;
import de.adorsys.multibanking.utils.FQNUtils;
import domain.BankAccount;
import domain.BankAccount.SyncStatus;
import domain.BankApi;
import domain.BankApiUser;
import exception.InvalidPinException;
import spi.OnlineBankingService;

@Service
public class BankAccountService extends BaseService {
    private static final Logger log = LoggerFactory.getLogger(BankAccessService.class);

    @Autowired
    private OnlineBankingServiceProducer bankingServiceProducer;
    @Autowired
    private UserIDAuth userIDAuth;
    @Autowired
    private BankService bankService;
    @Autowired
    private UserService userService;
	
	public List<BankAccountEntity> loadForBankAccess(String bankAccessId) {
		return load(userIDAuth, FQNUtils.bankAccountsFileFQN(bankAccessId), new TypeReference<List<BankAccountEntity>>(){});
	}

	public Optional<BankAccountEntity> loadBankAccount(String accessId, String accountId) {
		List<BankAccountEntity> accounts = load(userIDAuth, FQNUtils.bankAccountsFileFQN(accessId), new TypeReference<List<BankAccountEntity>>(){});
		return accounts.stream().filter(a -> StringUtils.equalsAnyIgnoreCase(accountId, a.getId())).findFirst();
	}
	
    public void synchBankAccounts(BankAccessEntity bankAccess, BankAccessCredentials credentials){
    	List<BankAccountEntity> bankAccounts = loadFromBankingAPI(bankAccess, credentials, null);
        
        if (bankAccounts.size() == 0) {
            throw new InvalidBankAccessException(bankAccess.getBankCode());
        }

        bankAccounts.forEach(account -> {account.bankAccessId(bankAccess.getId());});
        store(userIDAuth, FQNUtils.bankAccountsFileFQN(bankAccess.getId()), bankAccounts);
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
    
	public void updateSyncStatus(String bankAccessId, String accountId, BankAccount.SyncStatus syncStatus) {
		DocumentFQN bankAccountsFQN = FQNUtils.bankAccountsFileFQN(bankAccessId);
		List<BankAccountEntity> list = load(userIDAuth, bankAccountsFQN, new TypeReference<List<BankAccountEntity>>(){});
		BankAccountEntity accountEntity = list.stream().filter(b -> StringUtils.equalsAnyIgnoreCase(accountId, b.getId()))
			.findFirst().orElseThrow(() -> resourceNotFound(BankAccount.class, accountId));
		accountEntity.setSyncStatus(syncStatus);
		store(userIDAuth, bankAccountsFQN, list);
	}
	
	public void saveBankAccount(BankAccountEntity in){
		DocumentFQN bankAccountsFQN = FQNUtils.bankAccountsFileFQN(in.getBankAccessId());
		List<BankAccountEntity> list = load(userIDAuth, bankAccountsFQN, new TypeReference<List<BankAccountEntity>>(){});
		BankAccountEntity accountEntity = list.stream().filter(b -> StringUtils.equalsAnyIgnoreCase(in.getId(), b.getId()))
			.findFirst().orElseThrow(() -> resourceNotFound(BankAccount.class, in.getId()));
		BeanUtils.copyProperties(in, accountEntity);
		store(userIDAuth, bankAccountsFQN, list);
	}

	public void saveBankAccount(String bankAccessId, List<BankAccountEntity> list){
		DocumentFQN bankAccountsFQN = FQNUtils.bankAccountsFileFQN(bankAccessId);
		store(userIDAuth, bankAccountsFQN, list);
	}

	public boolean exists(String accessId, String accountId) {
		DocumentFQN bankAccountsFQN = FQNUtils.bankAccountsFileFQN(accessId);
		List<BankAccountEntity> list = load(userIDAuth, bankAccountsFQN, new TypeReference<List<BankAccountEntity>>(){});
		return list.stream().filter(b -> StringUtils.equalsAnyIgnoreCase(accountId, b.getId()))
			.findFirst().isPresent();
	}
	
	public Optional<BankAccountEntity> getBankAccount(String bankAccessId, String accountId){
		DocumentFQN bankAccountsFQN = FQNUtils.bankAccountsFileFQN(bankAccessId);
		List<BankAccountEntity> list = load(userIDAuth, bankAccountsFQN, new TypeReference<List<BankAccountEntity>>(){});
		return list.stream().filter(b -> StringUtils.equalsAnyIgnoreCase(accountId, b.getId()))
			.findFirst();
	}
	

	public SyncStatus getSyncStatus(String accessId, String accountId) {
		DocumentFQN bankAccountsFQN = FQNUtils.bankAccountsFileFQN(accessId);
		List<BankAccountEntity> list = load(userIDAuth, bankAccountsFQN, new TypeReference<List<BankAccountEntity>>(){});
		BankAccountEntity accountEntity = list.stream().filter(b -> StringUtils.equalsAnyIgnoreCase(accountId, b.getId()))
			.findFirst().orElseThrow(() -> resourceNotFound(BankAccount.class, accountId));
		return accountEntity.getSyncStatus();
	}
    
    private void filterAccounts(BankAccessEntity bankAccess, OnlineBankingService onlineBankingService, List<BankAccount> bankAccounts) {
        List<BankAccountEntity> userBankAccounts = loadForBankAccess(bankAccess.getId());

        //filter out previous created accounts
        Iterator<BankAccount> accountIterator = bankAccounts.iterator();
        while (accountIterator.hasNext()) {
            BankAccount newAccount = accountIterator.next();
            userBankAccounts.stream().filter(bankAccountEntity -> {
                String newAccountExternalID = newAccount.getExternalIdMap().get(onlineBankingService.bankApi());
                String existingAccountExternalID = bankAccountEntity.getExternalIdMap().get(onlineBankingService.bankApi());

                return newAccountExternalID.equals(existingAccountExternalID);
            }).findFirst().ifPresent(bankAccountEntity -> {
                accountIterator.remove();
            });
        }

        //all accounts created in the past
        if (bankAccounts.size() == 0) {
            throw new BankAccessAlreadyExistException();
        }

        bankAccess.setBankName(bankAccounts.get(0).getBankName());
    }

}
