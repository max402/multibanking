package org.adorsys.multibanking.onlinebanking.mapper.api;

import domain.BankAccess;
import domain.BankAccount;
import domain.response.LoadAccountInformationResponse;
import org.adorsys.multibanking.onlinebanking.mapper.api.domain.BankAccessData;
import org.adorsys.multibanking.onlinebanking.mapper.api.domain.BankAccessEntity;
import org.adorsys.multibanking.onlinebanking.mapper.api.domain.BankAccountData;
import org.adorsys.multibanking.onlinebanking.mapper.api.domain.BankAccountEntity;
import org.adorsys.multibanking.onlinebanking.mapper.api.domain.UserData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by peter on 06.02.19 11:59.
 *
 */
public class Mapper {
    private final static Logger LOGGER = LoggerFactory.getLogger(Mapper.class);
    UserData merge(UserData userData, LoadAccountInformationResponse loadAccountInformationResponse) {
        // String bankAccessID = loadAccountInformationResponse.getBankAccess();
        // TODO Wie komme ich an die bankAccessID
        BankAccessData destBankAccessData = new BankAccessData();
        {
            BankAccess sourceBankAccess = loadAccountInformationResponse.getBankAccess();
            BankAccessEntity destBankAccessEntity = new BankAccessEntity();

            destBankAccessEntity.setBankCode(sourceBankAccess.getBankCode());
            destBankAccessEntity.setBankName(sourceBankAccess.getBankName());
            destBankAccessEntity.setBankLogin(sourceBankAccess.getBankLogin());
            destBankAccessEntity.setBankLogin2(sourceBankAccess.getBankLogin2());
            destBankAccessEntity.setExternalIdMap(sourceBankAccess.getExternalIdMap());

            destBankAccessData.setBankAccess(destBankAccessEntity);
        }
        {
            for (BankAccount sourceBankAccount : loadAccountInformationResponse.getBankAccounts()) {
                BankAccountData destBankAccountData = new BankAccountData();
                {
                    BankAccountEntity destBankAccountEntity = new BankAccountEntity();
                    destBankAccountEntity.setExternalIdMap(sourceBankAccount.getExternalIdMap());
                    destBankAccountEntity.setOwner(sourceBankAccount.getOwner());
                    destBankAccountEntity.setCountry(sourceBankAccount.getCountry());
                    destBankAccountEntity.setBic(sourceBankAccount.getBlz());
                    destBankAccountEntity.setBankName(sourceBankAccount.getBankName());
                    destBankAccountEntity.setAccountNumber(sourceBankAccount.getAccountNumber());
                    destBankAccountEntity.setCurrency(sourceBankAccount.getCurrency());
                    destBankAccountEntity.setName(sourceBankAccount.getName());
                    destBankAccountEntity.setBic(sourceBankAccount.getBic());
                    destBankAccountEntity.setIban(sourceBankAccount.getIban());
                    destBankAccountEntity.setSyncStatus(sourceBankAccount.getSyncStatus());
                    destBankAccountEntity.setLastSync(sourceBankAccount.getLastSync());

                    destBankAccountData.setBankAccount(destBankAccountEntity);
                }

                destBankAccessData.getBankAccounts().add(destBankAccountData);
            }
        }

        userData.getBankAccesses().add(destBankAccessData);
        return userData;
    }

}
