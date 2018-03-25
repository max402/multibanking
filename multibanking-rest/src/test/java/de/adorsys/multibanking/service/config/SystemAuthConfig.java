package de.adorsys.multibanking.service.config;

import javax.annotation.PostConstruct;

import org.adorsys.docusafe.business.DocumentSafeService;
import org.adorsys.docusafe.business.types.complex.UserIDAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import de.adorsys.multibanking.auth.SystemIDAuth;
import de.adorsys.multibanking.service.old.TestConstants;

/**
 * This is a sample configuration for the system user. The system user will be 
 * set up by the containing application.
 * 
 * @author fpo
 *
 */
@Configuration
public class SystemAuthConfig {
	@Autowired
	private DocumentSafeService documentSafeService;
	
	@PostConstruct
	public void postConstruct(){
		SystemIDAuth systemIDAuth = TestConstants.getSystemUserIDAuth();
		UserIDAuth userIDAuth = systemIDAuth.getUserIDAuth();
		if(!documentSafeService.userExists(userIDAuth.getUserID())){
			documentSafeService.createUser(userIDAuth);
		}
	}
    
    @Bean
    @Primary
    public SystemIDAuth systemIDAuth(){
    	return TestConstants.getSystemUserIDAuth();
    }
}
