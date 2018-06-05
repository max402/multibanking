package de.adorsys.multibanking.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import de.adorsys.multibanking.domain.BankAccessCredentials;
import de.adorsys.multibanking.domain.BankAccessData;
import de.adorsys.multibanking.domain.BankAccessEntity;
import de.adorsys.multibanking.domain.BankAccountData;
import de.adorsys.multibanking.domain.BankAccountEntity;
import de.adorsys.multibanking.domain.UserData;
import de.adorsys.multibanking.exception.InvalidBankAccessException;

@Service
public class SynchBankAccountsService {
    private final static Logger LOGGER = LoggerFactory.getLogger(SynchBankAccountsService.class);

    public UserData synchBankAccounts(BankAccessEntity bankAccess, BankAccessCredentials credentials, BankDataService bds){
        List<BankAccountEntity> bankAccounts = bds.loadFromBankingAPI(bankAccess, credentials, null);

        if (bankAccounts.size() == 0) {
            throw new InvalidBankAccessException(bankAccess.getBankCode());
        }
        UserData userData = bds.load();
        BankAccessData bankAccessData = userData.bankAccessDataOrException(bankAccess.getId());
        //Map<String, BankAccountData> bankAccountDataMap = bankAccessData.getBankAccounts();
        List<BankAccountData> bankAccountData = bankAccessData.getBankAccounts();
        bankAccounts.forEach(account -> {
            account.bankAccessId(bankAccess.getId());
            Optional<BankAccountData> accountData = bankAccessData.getBankAccount(account.getId());
            if(!accountData.isPresent()){
                accountData = Optional.of(new BankAccountData());
                bankAccountData.add(accountData.get());
            }
            accountData.get().setBankAccount(account);
        });
        bds.store(userData);
        LOGGER.info(String.format("[%s] accounts for connection [%s] created.", bankAccounts.size(), bankAccess.getId()));
        return userData;
    }
    
}
