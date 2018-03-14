package de.adorsys.multibanking.pers.docusafe.repository;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Assume;
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
public class UserRepositoryImplDeleteTest {

	@Autowired
	private UserRepositoryIf userRepository;
	
	@Before
	public void before(){
		UserEntity userEntity = new UserEntity();
		userRepository.save(userEntity);
		Optional<UserEntity> user = userRepository.findById("anyID");
		Assume.assumeTrue(user.isPresent());
	}
	
	@Test
	public void testDelete() {
		// User id does not mater.
		userRepository.delete("adfasdfa");
		// The User file shall not exits
		boolean exists = userRepository.exists("sdfasdf");
		Assert.assertFalse(exists);

		// What if i try to read the file
//		Optional<UserEntity> user = userRepository.findById("adfasdfa");
	}
	
}
