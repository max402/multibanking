package org.adorsys.multibanking.onlinebanking.facade.mock;

import domain.BankAccess;
import domain.BankAccount;
import domain.request.LoadBalanceRequest;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by peter on 11.02.19 08:14.
 */
public class LoadBalancesTest {
    private final static Logger LOGGER = LoggerFactory.getLogger(LoadBalancesTest.class);

    @Test
    public void testBalances() {
        Assert.assertEquals(2, loadBankAccounts("p.spiessbach", "11111").size());
        Assert.assertEquals(2, loadBankAccounts("m.becker", "12345").size());
    }

    private List<BankAccount> loadBankAccounts(String user, String pin) {
        SimpleMockBanking simpleMockBanking = new SimpleMockBanking(null, null);
        BankAccess bankAccess = new BankAccess();
        bankAccess.setBankLogin(user);
        List<BankAccount> bankAccounts = simpleMockBanking.loadBalances(null, LoadBalanceRequest.builder()
                .bankApiUser(null)
                .bankAccess(bankAccess)
                .bankCode(null)
                .pin(pin)
                .build());
        bankAccounts.stream().forEach(bankAccount1 -> LOGGER.debug("found bank account:" + bankAccount1));
        return bankAccounts;
    }
}
