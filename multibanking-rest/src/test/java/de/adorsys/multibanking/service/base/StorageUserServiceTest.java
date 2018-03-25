package de.adorsys.multibanking.service.base;

import org.adorsys.docusafe.business.exceptions.UserIDAlreadyExistsException;
import org.adorsys.docusafe.business.exceptions.UserIDDoesNotExistException;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import com.nimbusds.jose.jwk.JWK;

import de.adorsys.multibanking.exception.ResourceNotFoundException;
import de.adorsys.multibanking.service.config.BaseServiceTest;
import de.adorsys.multibanking.utils.Ids;

@RunWith(SpringRunner.class)
public class StorageUserServiceTest extends BaseServiceTest {
    
	@Autowired
	private StorageUserService storageUserService;
	
    @Test
	public void testCreateUserAndCheckUserExists() {
    	auth(Ids.uuid(), Ids.uuid());
    	storageUserService.createUser(userIdAuth);
    	Assert.assertTrue(storageUserService.userExists(userIdAuth.getUserID()));
	}
    
	@Test
	public void testUserExists_false() {
    	auth(Ids.uuid(), Ids.uuid());
    	Assert.assertFalse(storageUserService.userExists(userIdAuth.getUserID()));
	}

	@Test(expected=UserIDAlreadyExistsException.class)
	public void testCreateUserAgain() {
    	auth(Ids.uuid(), Ids.uuid());
    	storageUserService.createUser(userIdAuth);
    	Assume.assumeTrue(storageUserService.userExists(userIdAuth.getUserID()));
    	storageUserService.createUser(userIdAuth);
	}

	@Test
	public void testDeleteUser() {
    	auth(Ids.uuid(), Ids.uuid());
    	storageUserService.createUser(userIdAuth);
    	Assume.assumeTrue(storageUserService.userExists(userIdAuth.getUserID()));
    	storageUserService.deleteUser(userIdAuth);
    	Assert.assertFalse(storageUserService.userExists(userIdAuth.getUserID()));
	}

	@Test(expected=UserIDDoesNotExistException.class)
	public void testDeleteUserAgain() {
    	auth(Ids.uuid(), Ids.uuid());
    	storageUserService.createUser(userIdAuth);
    	Assume.assumeTrue(storageUserService.userExists(userIdAuth.getUserID()));
    	storageUserService.deleteUser(userIdAuth);
    	Assume.assumeFalse(storageUserService.userExists(userIdAuth.getUserID()));
    	storageUserService.deleteUser(userIdAuth);
	}
	
	@Test
	public void testFindPublicEncryptionKey() {
    	auth(Ids.uuid(), Ids.uuid());
    	storageUserService.createUser(userIdAuth);
    	JWK jwk = storageUserService.findPublicEncryptionKey(userIdAuth.getUserID());
    	Assert.assertNotNull(jwk);
	}
    
	@Test(expected=ResourceNotFoundException.class)
	public void testFindPublicEncryptionKeyWrongUser() {
    	auth(Ids.uuid(), Ids.uuid());
    	storageUserService.findPublicEncryptionKey(userIdAuth.getUserID());
	}

}
