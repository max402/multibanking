package de.adorsys.multibanking.config.service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.adorsys.docusafe.business.DocumentSafeService;
import org.adorsys.docusafe.business.types.UserID;
import org.adorsys.docusafe.business.types.complex.DocumentFQN;
import org.adorsys.docusafe.business.types.complex.UserIDAuth;
import org.adorsys.encobject.domain.ReadKeyPassword;
import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.adorsys.multibanking.auth.CacheEntry;
import de.adorsys.multibanking.auth.RequestCounter;
import de.adorsys.multibanking.auth.SystemContext;
import de.adorsys.multibanking.auth.UserContext;
import de.adorsys.multibanking.auth.UserObjectPersistenceService;
import de.adorsys.multibanking.service.BankService;
import de.adorsys.multibanking.service.BankAccessCredentialService;
import de.adorsys.multibanking.service.BankDataService;
import de.adorsys.multibanking.service.base.StorageUserService;
import de.adorsys.multibanking.service.old.TestConstants;
import de.adorsys.multibanking.service.producer.OnlineBankingServiceProducer;
import de.adorsys.multibanking.usercontext.CacheInRequestContext;
import de.adorsys.multibanking.utils.Ids;
import de.adorsys.multibanking.utils.PrintMap;
import de.adorsys.onlinebanking.mock.MockBanking;
import figo.FigoBanking;

@ActiveProfiles({"InMemory"})
@SpringBootTest
public abstract class BaseServiceTest {
    private final static Logger LOGGER = LoggerFactory.getLogger(BaseServiceTest.class);

    @MockBean
    protected FigoBanking figoBanking;
    @MockBean
    protected MockBanking mockBanking;
    @MockBean
    protected OnlineBankingServiceProducer bankingServiceProducer;
    
    @MockBean
    protected UserContext userContext;
    @MockBean
    protected DocumentSafeService documentSafeService;

    @MockBean
    protected BankAccessCredentialService credentialService;
    @MockBean
    protected UserObjectPersistenceService uos;
    @MockBean
    protected UserObjectPersistenceService sos;
    @MockBean
    protected BankDataService bds;
    @MockBean
    protected BankService bankService;
    protected ObjectMapper objectMapper = new ObjectMapper();
    protected HttpServletRequest request;

    @Autowired
    protected SystemContext systemContext;    
    @Autowired
    private StorageUserService storageUserService;
    
    @Rule
    public TestName testName = new TestName();
    
    protected static final Map<String, RequestCounter> rcMap = new HashMap<>();

    @BeforeClass
    public static void beforeClass() {
    	TestConstants.setup();
    }
    
    @Before
    public void beforeTest() throws Exception {
        request = new MockHttpServletRequest();
//        uos = new UserObjectPersistenceService(userContext, objectMapper, documentSafeService);
//        sos = new UserObjectPersistenceService(systemContext.getUser(), objectMapper, documentSafeService);
        CacheInRequestContext requestContext = new CacheInRequestContext(request);
        requestContext.activateCache(uos);
        requestContext.activateCache(sos);
//        bds = new BankDataService(uos);
//        bankService = new BankService(objectMapper, systemContext, documentSafeService);
//        credentialService = new BankAccessCredentialService(uos);
        MockitoAnnotations.initMocks(this);
        when(bankingServiceProducer.getBankingService(anyString())).thenReturn(mockBanking);
        randomAuthAndUser();
        importBanks();
    }
    
    
    @AfterClass
    public static void afterClass(){
    	LOGGER.debug(PrintMap.print(rcMap));
    }
    
    protected void auth(String userId, String password){
    	auth(userId, password, true);
    }
    protected void auth(String userId, String password, boolean createUser){
    	UserIDAuth userIDAuth = new UserIDAuth(new UserID(userId), new ReadKeyPassword(password));
    	if(createUser && !storageUserService.userExists(userIDAuth.getUserID()))
    		storageUserService.createUser(userIDAuth);
    	RequestCounter requestCounter = new RequestCounter();
    	Map<Type, Map<DocumentFQN, CacheEntry<?>>> cache = new HashMap<>();
    	when(userContext.getAuth()).thenReturn(userIDAuth);
    	when(userContext.getRequestCounter()).thenReturn(requestCounter);
    	when(userContext.getCache()).thenReturn(cache);
    }
    protected void randomAuthAndUser(){
    	randomAuthAndUser(null);
    }
    protected void randomAuthAndUser(Date expire){
    	auth(randomUserId(), allwaysTheSamePassword());
    	bds.createUser(expire);
    }
    
    boolean banksImported = false;
    protected void importBanks() throws IOException{
        if (!banksImported) {
        	InputStream inputStream = BaseServiceTest.class.getClassLoader().getResource("catalogue/banks/test-bank-catalogue.yml").openStream();
        	bankService.importBanks(inputStream);
        	IOUtils.closeQuietly(inputStream);
            banksImported = true;
        }
    }
    
    /**
     * For documentation purpose.
     * @return
     */
    protected final String userId(){
    	return userContext.getAuth().getUserID().getValue();
    }
    protected final UserIDAuth auth(){
    	return userContext.getAuth();
    }
    
    /**
     * For documentation purpose.
     * @return
     */
    protected final String randomAccessId(){
    	return Ids.uuid();
    }
    protected final String randomAccountId(){
    	return Ids.uuid();
    }

    private final String randomUserId(){
    	return Ids.uuid();
    }

    private final String allwaysTheSamePassword(){
        return "same-password-all-the-time";
//    	return Ids.uuid();
    }
}
