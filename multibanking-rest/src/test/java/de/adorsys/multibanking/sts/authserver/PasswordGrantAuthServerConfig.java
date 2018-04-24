package de.adorsys.multibanking.sts.authserver;

import org.adorsys.docusafe.business.DocumentSafeService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.adorsys.multibanking.config.STSInMemoryConfig;
import de.adorsys.sts.persistence.FsUserDataRepository;
import de.adorsys.sts.resourceserver.persistence.InMemoryResourceServerRepository;
import de.adorsys.sts.resourceserver.persistence.ResourceServerRepository;
import de.adorsys.sts.resourceserver.service.UserDataRepository;
import de.adorsys.sts.token.passwordgrant.EnablePasswordGrant;

@Configuration
@EnablePasswordGrant
@Import({ STSInMemoryConfig.class })
@EnableConfigurationProperties
public class PasswordGrantAuthServerConfig {

	@Bean
	ResourceServerRepository resourceServerRepository() {
		return new InMemoryResourceServerRepository();
	}

	@Bean
	UserDataRepository userDataRepository(DocumentSafeService documentSafeService, ObjectMapper objectMapper) {
		return new FsUserDataRepository(documentSafeService, objectMapper);
	}
}
