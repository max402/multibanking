package de.adorsys.multibanking.service.producer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import de.adorsys.multibanking.domain.BankEntity;
import de.adorsys.multibanking.domain.UserData;
import de.adorsys.multibanking.domain.UserEntity;
import de.adorsys.multibanking.service.BankService;
import de.adorsys.multibanking.utils.Ids;
import de.adorsys.onlinebanking.mock.MockBanking;
import domain.BankApi;
import domain.BankApiUser;
import figo.FigoBanking;
import finapi.FinapiBanking;
import hbci4java.Hbci4JavaBanking;
import spi.OnlineBankingService;

/**
 * Created by alexg on 17.05.17.
 */
@Service
public class OnlineBankingServiceProducer {

    
    String defaultBankApi;
    BankService bankService;
    private MockBanking mockBanking;

    private Hbci4JavaBanking hbci4JavaBanking = new Hbci4JavaBanking();
    private FigoBanking figoBanking = new FigoBanking(BankApi.FIGO);
    private FigoBanking figoBankingAlternative = new FigoBanking(BankApi.FIGO_ALTERNATIVE);
    private FinapiBanking finapiBanking = new FinapiBanking();
    
    

    public OnlineBankingServiceProducer(@Value("${defaultBankApi:HBCI}")String defaultBankApi, BankService bankService,
            MockBanking mockBanking) {
        super();
        this.defaultBankApi = defaultBankApi;
        this.bankService = bankService;
        this.mockBanking = mockBanking;
    }

    private BankApi getBankApiForBlz(String blz) {
        BankEntity bankInfoEntity = bankService.findByBankCode(blz).orElse(null);

        if (bankInfoEntity != null && bankInfoEntity.getBankApi() != null) {
            return bankInfoEntity.getBankApi();
        }
        return BankApi.valueOf(defaultBankApi);
    }

    public OnlineBankingService getBankingService(String bankCode) {
        BankApi bankApi = getBankApiForBlz(bankCode);

        return getBankingService(bankApi);
    }

    public OnlineBankingService getBankingService(BankApi bankApi) {
        switch (bankApi) {
            case HBCI:
                return hbci4JavaBanking;
            case FIGO:
                return figoBanking;
            case FIGO_ALTERNATIVE:
                return figoBankingAlternative;
            case FINAPI:
                return finapiBanking;
            case MOCK:
                return mockBanking;
        }
        throw new IllegalStateException("unsupported bank api");
    }
    
    /**
     * Returns the bank API user. Registers with the banking API if necessary.
     * 
     * User must have been create before.
     * 
     * @param bankApi
     * @param bankCode
     * @return
     */
    public BankApiUser checkApiRegistration(BankApi bankApi, String bankCode, UserData userData) {
        OnlineBankingService onlineBankingService = bankApi != null
                ? getBankingService(bankApi)
                : getBankingService(bankCode);

        if (onlineBankingService.userRegistrationRequired()) {
            UserEntity userEntity = userData.getUserEntity();

            return userEntity.getApiUser()
                    .stream()
                    .filter(bankApiUser -> bankApiUser.getBankApi() == onlineBankingService.bankApi())
                    .findFirst()
                    .orElseGet(() -> {
                        BankApiUser bankApiUser = onlineBankingService.registerUser(Ids.uuid());
                        userEntity.getApiUser().add(bankApiUser);

                        return bankApiUser;
                    });
        } else {
            BankApiUser bankApiUser = new BankApiUser();
            bankApiUser.setBankApi(onlineBankingService.bankApi());
            return bankApiUser;
        }
    }    
    
}
