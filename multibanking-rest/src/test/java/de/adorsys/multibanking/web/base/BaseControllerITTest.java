package de.adorsys.multibanking.web.base;

import static org.mockito.Mockito.when;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.adorsys.docusafe.business.types.UserID;
import org.adorsys.docusafe.business.types.complex.DocumentFQN;
import org.adorsys.docusafe.business.types.complex.UserIDAuth;
import org.adorsys.encobject.domain.ReadKeyPassword;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import de.adorsys.multibanking.auth.CacheEntry;
import de.adorsys.multibanking.auth.RequestCounter;
import de.adorsys.multibanking.auth.SystemContext;
import de.adorsys.multibanking.auth.UserContext;
import de.adorsys.multibanking.service.config.Tp;
import de.adorsys.multibanking.service.old.TestConstants;
import de.adorsys.multibanking.utils.Ids;

@RunWith(SpringRunner.class)
@ActiveProfiles({"InMemory"})
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, properties={Tp.p1,Tp.p2,Tp.p3,Tp.p4,Tp.p5,Tp.p6,Tp.p7,Tp.p8,Tp.p9,Tp.p10,Tp.p11,Tp.p12,
		Tp.p13,Tp.p14,Tp.p15,Tp.p16,Tp.p17,Tp.p18,Tp.p19,Tp.p20,Tp.p21,Tp.p22,Tp.p23,
		Tp.p24,Tp.p25,Tp.p26,Tp.p27,Tp.p28,Tp.p29})
public abstract class BaseControllerITTest {

    @LocalServerPort
    private int port;

    @MockBean
    protected UserContext userContext;
    
    @Autowired
    protected SystemContext systemContext;
    
    @Autowired
    protected TestRestTemplate testRestTemplate;

    private String baseUri;
    private String baseApiUri;

    @BeforeClass
    public static void beforeClass() {
    	TestConstants.setup();
    }
    
    @Before
    public void setup() {
        this.baseUri = "http://localhost:" + port;
        this.baseApiUri = this.baseUri;
    }

    /**
     * <h3>Return the current test environment base uri.</h3>
     * 
     * <p>
     * This is due to the fact that, spring bootstrap the testing environment on
     * a random port, to not interfere with any boot application running on a
     * default port.
     * 
     * So we need to manually build the baseUri for the testing environment.
     * </p>
     * 
     * @return baseUri String
     */
    public String getBaseUri() {
        return this.baseUri;
    }

    /**
     * <p>
     * Return the current api uri
     * </p>
     * 
     * @return
     */
    public String getBaseApiUri() {
        return this.baseApiUri;
    }
    
    protected void auth(String userId, String password){
    	UserIDAuth userIDAuth = new UserIDAuth(new UserID(userId), new ReadKeyPassword(password));
    	RequestCounter requestCounter = new RequestCounter();
    	Map<Type, Map<DocumentFQN, CacheEntry<?>>> cache = new HashMap<>();
    	when(userContext.getAuth()).thenReturn(userIDAuth);
    	when(userContext.getRequestCounter()).thenReturn(requestCounter);
    	when(userContext.getCache()).thenReturn(cache);
    	
    }
    
    /**
     * AuthenticateUser For FurtherRequests
     * 
     * @param userName
     * @return
     */
    protected void authenticateUserForFurtherRequests(String userName) {
    	auth(userName, Ids.uuid());
    }

    @After
    public void tearDown() {
    	auth("anonymous", Ids.uuid());
    }
}
