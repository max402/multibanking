package de.adorsys.multibanking.pers.docusafe.repository;

import org.adorsys.docusafe.business.types.UserID;
import org.adorsys.docusafe.business.types.complex.UserIDAuth;
import org.adorsys.encobject.domain.ReadKeyPassword;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import de.adorsys.multibanking.pers.docusafe.repository.UserRepositoryImplTest3.UserRepositoryImplTestConfig;
import de.adorsys.multibanking.pers.spi.repository.UserRepositoryIf;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes=UserRepositoryImplTestConfig.class)
public class UserRepositoryImplTest3 {

	@Autowired
	private UserRepositoryIf userRepository;
	
	@Configuration
	@Import(TestConfig.class)
	static class UserRepositoryImplTestConfig {
		UserIDAuth userIDAuth = new UserIDAuth(new UserID("sampleUser"), new ReadKeyPassword("password4thisSimpleUser"));

		@Bean
		public UserIDAuth getUserIDAuth() {
			return userIDAuth;
		}
		
		
	}
	
	@Before
	public void before(){
	}

	@BeforeClass
	public static void beforeClass(){
		System.out.println("Before test running");
	}
	

	@Test
	public void testExistsNegative() {
		Assert.assertFalse(userRepository.exists("xxxxxxx"));
	}
	
	@Test
	public void testExistsPositive() {
		Assert.assertFalse(userRepository.exists("francis"));
	}
	
	
	@Test
	public void testFindById() {

	}


	@Test
	public void testSave() {
//		fail("Not yet implemented");
	}


}
