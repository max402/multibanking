package de.adorsys.multibanking.pers.docusafe.repository;

import org.adorsys.docusafe.business.types.UserID;
import org.adorsys.docusafe.business.types.complex.UserIDAuth;
import org.adorsys.encobject.domain.ReadKeyPassword;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(TestConfig.class)
public class UserRepositoryImplTestConfig {
	UserIDAuth userIDAuth = new UserIDAuth(new UserID("sampleUser"), new ReadKeyPassword("password4thisSimpleUser"));

	@Bean
	public UserIDAuth getUserIDAuth() {
		return userIDAuth;
	}
}
