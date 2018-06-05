package de.adorsys.multibanking.config.authserver;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import de.adorsys.multibanking.config.core.STSInMemoryConfig;
import de.adorsys.sts.resourceserver.persistence.InMemoryResourceServerRepository;
import de.adorsys.sts.resourceserver.persistence.ResourceServerRepository;
import de.adorsys.sts.token.passwordgrant.EnablePasswordGrant;

@Configuration
@EnablePasswordGrant
@Import({ STSInMemoryConfig.class })
@EnableConfigurationProperties
@Profile("IntegrationTest")
public class PasswordGrantAuthServerConfig {

	@Bean
	ResourceServerRepository resourceServerRepository() {
		return new InMemoryResourceServerRepository();
	}

//	@Bean
//	UserDataRepository userDataRepository(ObjectMapper objectMapper) {
//		// Warning you can not use the client applications docusafe here.
//		FileSystemExtendedStorageConnection storageConnection = new FileSystemExtendedStorageConnection("target/authServer/"+Ids.uuid());
//		DocumentSafeServiceImpl documentSafeServiceImpl = new DocumentSafeServiceImpl(storageConnection);
//		ExceptionHandlingDocumentSafeService documentSafeService = new ExceptionHandlingDocumentSafeService(documentSafeServiceImpl);
//		return new FsUserDataRepository(documentSafeService, objectMapper);
//	}
}
