package de.adorsys.multibanking.pers.docusafe.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import de.adorsys.multibanking.domain.UserEntity;
import de.adorsys.multibanking.pers.spi.repository.UserRepositoryIf;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes=UserRepositoryImplTestConfig.class)
public class UserRepositoryImplSaveTest {

	@Autowired
	private UserRepositoryIf userRepository;
	
	@Test
	public void testSave() {
		UserEntity userEntity = new UserEntity();
		userRepository.save(userEntity);
	}
}
