package de.adorsys.multibanking.web;

import de.adorsys.multibanking.auth.UserContext;
import de.adorsys.multibanking.auth.UserContextService;
import de.adorsys.multibanking.domain.BankEntity;
import de.adorsys.multibanking.service.BankService;
import de.adorsys.multibanking.service.base.StorageUserService;
import de.adorsys.multibanking.web.base.BaseControllerIT;
import de.adorsys.multibanking.web.base.UserContextMockHelper;
import de.adorsys.multibanking.web.base.entity.BankAccessStructure;
import de.adorsys.multibanking.web.base.entity.UserPasswordTuple;
import de.adorsys.sts.resourceserver.service.UserDataRepository;
import de.adorsys.sts.token.api.TokenResponse;
import de.adorsys.sts.token.authentication.TokenAuthenticationService;
import de.adorsys.sts.tokenauth.BearerTokenValidator;

import org.adorsys.cryptoutils.storeconnectionfactory.ExtendedStoreConnectionFactory;
import org.adorsys.encobject.service.api.ExtendedStoreConnection;
import org.junit.Assume;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.InputStream;
import java.util.Optional;

/**
 * Created by peter on 07.05.18 at 17:34.
 */
public abstract class MB_BaseTest extends BaseControllerIT {
    @Autowired
    public BankService bankService;
    public UserPasswordTuple userPasswordTuple;
    public BankAccessStructure theBeckerTuple = new BankAccessStructure("19999999", "m.becker", "12345");
    public String PIN = "12345";
    public String WRONG_PIN = "22344";
    @MockBean
    UserContext userContextMock;
    
    private UserContextService userContextService;
    
    @Autowired
    private TokenAuthenticationService tokenAuthenticationService;
    
    @Autowired
    private BearerTokenValidator bearerTokenValidator;

//    @Autowired
//    private UserDataRepository userDataService;    
    
//    @Value("${secretstorage.system.user.password}:sampleSecret")
    private String tokenExchangeSystemUserPassword="sampleSecret";
    
    @Value("${sts.audience_name}")
    private String stsAudienceName;

    @Autowired
    private StorageUserService storageUserService;


    @Before
    public void setupBank() throws Exception {
//        ExtendedStoreConnection c = ExtendedStoreConnectionFactory.get();
//        c.listAllBuckets().forEach(bucket -> {
//            if (! bucket.getObjectHandle().getContainer().equals("bp-system")) {
//                c.deleteContainer(bucket);
//            }
//        });
        userPasswordTuple = new UserPasswordTuple("peter", "allwaysTheSamePassword");
        TokenResponse tokenResponse = auth(userPasswordTuple);
        mockUserContext(tokenResponse.getAccess_token(), userContextMock);

        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("mock_bank.json");
        bankService.importBanks(inputStream);
        Optional<BankEntity> bankEntity = bankService.findByBankCode("19999999");
        Assume.assumeTrue(bankEntity.isPresent());
    }
    
    protected void mockUserContext(String token, UserContext userContextMock) {
        userContextService = new UserContextService(bearerTokenValidator, tokenExchangeSystemUserPassword, stsAudienceName, storageUserService);
        UserContextMockHelper.mockUserContext(token, userContextMock, tokenAuthenticationService, userContextService);
    }
    
}
