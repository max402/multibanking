package de.adorsys.multibanking.pers.docusafe.repository;

import java.util.Optional;

import org.adorsys.docusafe.business.types.complex.UserIDAuth;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import de.adorsys.multibanking.domain.UserEntity;
import de.adorsys.multibanking.pers.spi.repository.UserRepositoryIf;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes=UserRepositoryImplTestConfig.class)
public class UserRepositoryImplFindTest {

	@Autowired
	private UserRepositoryIf userRepository;
	
	@Before
	public void before(){
		UserEntity userEntity = new UserEntity();
		userRepository.save(userEntity);
	}
	
	@Autowired
	private UserIDAuth userIDAuth;
	
	@Test
	public void testSave() {
		// the id does not matter here. We return the Entity 
		// with the id associated with the user record.
		Optional<UserEntity> user = userRepository.findById("anyID");
		Assert.assertTrue(user.isPresent());
		Assert.assertEquals(userIDAuth.getUserID().getValue(), user.get().getId());
	}
	
	
}
