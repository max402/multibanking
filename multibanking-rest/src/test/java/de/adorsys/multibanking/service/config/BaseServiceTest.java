package de.adorsys.multibanking.service.config;

import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.adorsys.docusafe.business.types.UserID;
import org.adorsys.docusafe.business.types.complex.UserIDAuth;
import org.adorsys.encobject.domain.ReadKeyPassword;
import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import de.adorsys.multibanking.service.BankService;
import de.adorsys.multibanking.service.UserService;
import de.adorsys.multibanking.service.old.TestConstants;
import de.adorsys.multibanking.utils.Ids;

@ActiveProfiles({"InMemory"})
@SpringBootTest(properties={Tp.p1,Tp.p2,Tp.p3,Tp.p4,Tp.p5,Tp.p6,Tp.p7,Tp.p8,Tp.p9,Tp.p10,Tp.p11,Tp.p12,
		Tp.p13,Tp.p14,Tp.p15,Tp.p16,Tp.p17,Tp.p18,Tp.p19,Tp.p20,Tp.p21,Tp.p22,Tp.p23,
		Tp.p24,Tp.p25,Tp.p26,Tp.p27,Tp.p28,Tp.p29})
public class BaseServiceTest {

	@Autowired
    protected UserService userService;

    @Autowired
    private BankService bankService;
	
    @MockBean
    protected UserIDAuth userIdAuth;

    @BeforeClass
    public static void beforeClass() {
    	TestConstants.setup();
    }
    
    protected void auth(String userId, String password){
    	when(userIdAuth.getUserID()).thenReturn(new UserID(userId));
    	when(userIdAuth.getReadKeyPassword()).thenReturn(new ReadKeyPassword(password));
    }

    protected void randomAuthAndUser(){
    	randomAuthAndUser(null);
    }
    protected void randomAuthAndUser(Date expire){
    	auth(randomUserId(), randomPassword());
    	userService.createUser(expire);
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
    	return userIdAuth.getUserID().getValue();
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
    private final String randomPassword(){
    	return Ids.uuid();
    }
}
