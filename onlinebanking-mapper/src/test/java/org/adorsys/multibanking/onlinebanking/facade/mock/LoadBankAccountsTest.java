package org.adorsys.multibanking.onlinebanking.facade.mock;

import domain.BankAccess;
import domain.BankAccount;
import domain.request.LoadAccountInformationRequest;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Created by peter on 11.02.19 07:48.
 */
public class LoadBankAccountsTest {
    private final static Logger LOGGER = LoggerFactory.getLogger(LoadBankAccountsTest.class);
    @Test
    public void loadBankAccounts() {
        Assert.assertEquals(2, loadBankAccounts("p.spiessbach","11111").size());
        Assert.assertEquals(2, loadBankAccounts("m.becker","12345").size());
    }

    private List<BankAccount> loadBankAccounts(String user, String pin) {
        List<BankAccount> bankAccounts;
            SimpleMockBanking simpleMockBanking = new SimpleMockBanking(null, null);
            BankAccess bankAccess = new BankAccess();
            bankAccess.setBankLogin(user);

            bankAccounts = Optional.ofNullable(simpleMockBanking.loadBankAccounts(
                    null,
                    LoadAccountInformationRequest.builder()
                            .bankApiUser(null)
                            .bankAccess(bankAccess)
                            .bankCode(null)
                            .pin(pin)
                            .storePin(false)
                            .updateTanTransportTypes(true)
                            .build()
            )).map(ba -> ba.getBankAccounts()).orElse(Collections.EMPTY_LIST);

        bankAccounts.stream().forEach(bankAccount1 -> LOGGER.info("found bank account:" + bankAccount1));
        return bankAccounts;
    }
}
