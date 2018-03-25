package de.adorsys.multibanking.service.config;

import javax.annotation.PostConstruct;

import org.adorsys.docusafe.business.DocumentSafeService;
import org.adorsys.docusafe.business.types.complex.UserIDAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import de.adorsys.multibanking.auth.SystemContext;
import de.adorsys.multibanking.auth.UserContext;
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
		SystemContext sys = TestConstants.getSystemUserIDAuth();
		UserContext user = sys.getUser();
		if(!documentSafeService.userExists(user.getAuth().getUserID())){
			documentSafeService.createUser(user.getAuth());
		}
	}
    
    @Bean
    @Primary
    public SystemContext systemIDAuth(){
    	return TestConstants.getSystemUserIDAuth();
    }
}
