package de.adorsys.multibanking.service.old;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.security.Security;

import org.adorsys.docusafe.business.types.complex.DSDocument;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import de.adorsys.multibanking.domain.BankAccessEntity;
import de.adorsys.multibanking.domain.BankAccountEntity;
import de.adorsys.multibanking.domain.UserEntity;
import de.adorsys.multibanking.service.AnalyticsService;
import de.adorsys.multibanking.service.BankAccessService;
import de.adorsys.multibanking.service.BookingService;
import de.adorsys.multibanking.service.OnlineBankingServiceProducer;
import de.adorsys.multibanking.service.PaymentService;
import de.adorsys.multibanking.service.UserService;
import de.adorsys.onlinebanking.mock.MockBanking;
import domain.BankApi;

/**
 * Created by alexg on 09.10.17.
 * @author fpo 2018-03-24 02:26
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles({"InMemory"})
@Ignore
public class MockBankingTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BankAccessService bankAccessService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private AnalyticsService analyticsService;

    @Autowired
    private UserService userService;

    @MockBean
    private OnlineBankingServiceProducer bankingServiceProducer;

    private static final Logger LOG = LoggerFactory.getLogger(MockBankingTest.class);

    @BeforeClass
    public static void beforeClass() {
		TestConstants.setup();

        System.setProperty("FIGO_CLIENT_ID", "CdunSr9hi4Q6rL65u-l-coQngofLdNbyjACwFoDOd_OU");
        System.setProperty("FIGO_SECRET", "Sx9FNf1Uze0NZTgXq40ljDeWIpauTJaiZPkhDrc6Vavs");
        System.setProperty("FIGO_TECH_USER", "figo-user");
        System.setProperty("FIGO_TECH_USER_CREDENTIAL", "test123");
        System.setProperty("mockConnectionUrl", "https://multibanking-mock.dev.adorsys.de");

        Security.addProvider(new BouncyCastleProvider());
    }

    @Before
    public void beforeTest() throws IOException {
        MockitoAnnotations.initMocks(this);

        when(bankingServiceProducer.getBankingService(anyString())).thenReturn(new MockBanking());
        when(bankingServiceProducer.getBankingService(BankApi.FIGO)).thenReturn(new MockBanking());
        when(bankingServiceProducer.getBankingService(BankApi.HBCI)).thenReturn(new MockBanking());
        when(bankingServiceProducer.getBankingService(BankApi.MOCK)).thenReturn(new MockBanking());
    }

    @Test
    public void testSyncBookings() {
    	// TODO: inject UserIdAuth
//    	UserEntity userEntity = TestUtil.getUserEntity("test-user-id");
    	UserEntity userEntity = userService.createUser(null);

        BankAccessEntity bankAccessEntity = TestUtil.getBankAccessEntity("test-user-id", "test-access-id", "19999999", "12345");
        bankAccessEntity.setBankLogin("m.becker");
        bankAccessEntity.setCategorizeBookings(true);
        bankAccessEntity.setStoreAnalytics(true);

        BankAccountEntity bankAccountEntity = TestUtil.getBankAccountEntity("test-account-id");
        bankAccountEntity.setUserId("test-user-id");
        bankAccountEntity.setIban("DE81199999993528307800");
        bankAccountEntity.setAccountNumber("765551851");

        bookingService.syncBookings(bankAccessEntity, bankAccountEntity, BankApi.MOCK, "12345");

        DSDocument loadDomainAnalytics = analyticsService.loadDomainAnalytics("test-access-id", "test-account-id");
        
        Assert.assertNotNull(loadDomainAnalytics);
//        LOG.info(analyticsEntity.get().toString());
    }
}