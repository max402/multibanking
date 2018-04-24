package de.adorsys.multibanking.web.base;

import java.net.URI;
import java.util.Collections;

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
import org.springframework.web.util.UriComponentsBuilder;

import de.adorsys.multibanking.auth.SystemContext;
import de.adorsys.multibanking.auth.UserContext;
import de.adorsys.multibanking.service.config.Tp;
import de.adorsys.multibanking.service.old.TestConstants;
import de.adorsys.multibanking.utils.Ids;

@RunWith(SpringRunner.class)
@ActiveProfiles({"InMemory"})
@SpringBootTest(properties={Tp.p1,Tp.p2,Tp.p3,Tp.p4,Tp.p5,Tp.p6,Tp.p7,Tp.p8,Tp.p9,Tp.p10,Tp.p11,Tp.p12,
		Tp.p13,Tp.p14,Tp.p15,Tp.p16,Tp.p17,Tp.p18,Tp.p19,Tp.p20,Tp.p21,Tp.p22,Tp.p23,
		Tp.p24,Tp.p25,Tp.p26,Tp.p27,Tp.p28,Tp.p29,Tp.p30,Tp.p31,Tp.p32,Tp.p33,Tp.p34,Tp.p35,Tp.p36,Tp.p37 },
		webEnvironment = WebEnvironment.DEFINED_PORT)
public abstract class BaseControllerITTest {

    @LocalServerPort
    private int port;

//    @MockBean
//    protected UserContext userContext;
//    
//    @Autowired
//    protected SystemContext systemContext;
    
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
    
    protected PasswordGrantResponse auth(String userId, String password){
    	URI uri = authPath()
		.queryParam("grant_type", "password")
		.queryParam("username", userId)
		.queryParam("password", password)
		.queryParam("audience", "multibanking")
		.build().toUri();
    	
    	PasswordGrantResponse resp = testRestTemplate.getForObject(uri, PasswordGrantResponse.class);

        final String accessTokenValue = resp.getAccessToken();

        testRestTemplate.getRestTemplate().setInterceptors(

                Collections.singletonList((request, body, execution) -> {
                    request.getHeaders().add("Authorization", "Bearer " + accessTokenValue);
                    return execution.execute(request, body);
                }));
        
        return resp;
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
//    	auth("anonymous", Ids.uuid());
    }
    
    protected final UriComponentsBuilder authPath(){
    	return UriComponentsBuilder.fromPath("/token/password-grant");
	}
    
}
