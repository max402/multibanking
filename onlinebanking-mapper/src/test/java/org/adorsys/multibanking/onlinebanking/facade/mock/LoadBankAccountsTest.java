package org.adorsys.multibanking.onlinebanking.facade.mock;

import domain.BankAccess;
import domain.request.LoadAccountInformationRequest;
import domain.response.LoadAccountInformationResponse;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by peter on 11.02.19 07:48.
 */
public class LoadBankAccountsTest {
    private final static Logger LOGGER = LoggerFactory.getLogger(LoadBankAccountsTest.class);

    @Test
    public void loadBankAccounts() {
        Assert.assertEquals(2, loadBankAccounts("p.spiessbach", "11111").getBankAccounts().size());
        Assert.assertEquals(2, loadBankAccounts("m.becker", "12345").getBankAccounts().size());
    }

    private LoadAccountInformationResponse loadBankAccounts(String user, String pin) {
        LoadAccountInformationResponse response;
        SimpleMockBanking simpleMockBanking = new SimpleMockBanking(null, null);
        BankAccess bankAccess = new BankAccess();
        bankAccess.setBankLogin(user);

        response = simpleMockBanking.loadBankAccounts(
                null,
                LoadAccountInformationRequest.builder()
                        .bankApiUser(null)
                        .bankAccess(bankAccess)
                        .bankCode(null)
                        .pin(pin)
                        .storePin(false)
                        .updateTanTransportTypes(true)
                        .build()
        );

        response.getBankAccounts().stream().forEach(bankAccount1 -> LOGGER.debug("found bank account:" + bankAccount1));
        return response;
    }
}
