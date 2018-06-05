package de.adorsys.multibanking.service.base;

import static org.mockito.Mockito.when;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.adorsys.docusafe.business.DocumentSafeService;
import org.adorsys.docusafe.business.exceptions.UserIDAlreadyExistsException;
import org.adorsys.docusafe.business.exceptions.UserIDDoesNotExistException;
import org.adorsys.docusafe.business.impl.DocumentSafeServiceImpl;
import org.adorsys.docusafe.business.types.UserID;
import org.adorsys.docusafe.business.types.complex.DocumentFQN;
import org.adorsys.docusafe.business.types.complex.UserIDAuth;
import org.adorsys.encobject.domain.ReadKeyPassword;
import org.adorsys.encobject.filesystem.FileSystemExtendedStorageConnection;
import org.adorsys.encobject.service.api.ExtendedStoreConnection;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.nimbusds.jose.jwk.JWK;

import de.adorsys.multibanking.auth.CacheEntry;
import de.adorsys.multibanking.auth.RequestCounter;
import de.adorsys.multibanking.auth.UserContext;
import de.adorsys.multibanking.exception.ResourceNotFoundException;
import de.adorsys.multibanking.utils.Ids;

@RunWith(SpringRunner.class)
public class StorageUserServiceTest{// extends BaseServiceTest {
    @MockBean
    protected UserContext userContext;

	private StorageUserService storageUserService;
	
	private static DocumentSafeService documentSafeService;
	
    @BeforeClass
    public static void beforeClass(){
        ExtendedStoreConnection extendedStoreConnection = new FileSystemExtendedStorageConnection("target/" + Ids.uuid());
        documentSafeService =  new ExceptionHandlingDocumentSafeService(new DocumentSafeServiceImpl(extendedStoreConnection));
    }

    @Before
	public void before(){
        storageUserService = new StorageUserService(documentSafeService);
	}

    @Test
	public void testCreateUserAndCheckUserExists() {
    	auth(Ids.uuid(), Ids.uuid(), false);
    	storageUserService.createUser(auth());
    	Assert.assertTrue(storageUserService.userExists(auth().getUserID()));
	}

	@Test
	public void testUserExists_false() {
    	auth(Ids.uuid(), Ids.uuid(), false);
    	Assert.assertFalse(storageUserService.userExists(auth().getUserID()));
	}

	@Test(expected=UserIDAlreadyExistsException.class)
	public void testCreateUserAgain() {
    	auth(Ids.uuid(), Ids.uuid(), false);
    	storageUserService.createUser(auth());
    	Assume.assumeTrue(storageUserService.userExists(auth().getUserID()));
    	storageUserService.createUser(auth());
	}

	@Test
	public void testDeleteUser() {
    	auth(Ids.uuid(), Ids.uuid(), false);
    	storageUserService.createUser(auth());
    	Assume.assumeTrue(storageUserService.userExists(auth().getUserID()));
    	storageUserService.deleteUser(auth());
    	Assert.assertFalse(storageUserService.userExists(auth().getUserID()));
	}

	@Test(expected=UserIDDoesNotExistException.class)
	public void testDeleteUserAgain() {
    	auth(Ids.uuid(), Ids.uuid(), false);
    	storageUserService.createUser(auth());
    	Assume.assumeTrue(storageUserService.userExists(auth().getUserID()));
    	storageUserService.deleteUser(auth());
    	Assume.assumeFalse(storageUserService.userExists(auth().getUserID()));
    	storageUserService.deleteUser(auth());
	}

	@Test
	public void testFindPublicEncryptionKey() {
    	auth(Ids.uuid(), Ids.uuid(), false);
    	storageUserService.createUser(auth());
    	JWK jwk = storageUserService.findPublicEncryptionKey(auth().getUserID());
    	Assert.assertNotNull(jwk);
	}

	@Test (expected = ResourceNotFoundException.class)
	public void testFindPublicEncryptionKeyWrongUser() {
            auth(Ids.uuid(), Ids.uuid(), false);
            storageUserService.findPublicEncryptionKey(auth().getUserID());
	}
	
    protected final UserIDAuth auth(){
        return userContext.getAuth();
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


}
