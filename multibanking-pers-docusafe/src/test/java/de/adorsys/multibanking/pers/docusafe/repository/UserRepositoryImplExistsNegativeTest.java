package de.adorsys.multibanking.pers.docusafe.repository;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import de.adorsys.multibanking.pers.spi.repository.UserRepositoryIf;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes=UserRepositoryImplTestConfig.class)
public class UserRepositoryImplExistsNegativeTest {

	@Autowired
	private UserRepositoryIf userRepository;
	
	@Test
	public void testExistsPositive() {
		Assert.assertFalse(userRepository.exists("francis"));
	}
}
