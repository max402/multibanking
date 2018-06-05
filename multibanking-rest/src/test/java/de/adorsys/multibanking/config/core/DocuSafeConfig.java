package de.adorsys.multibanking.config.core;

import org.adorsys.docusafe.business.DocumentSafeService;
import org.adorsys.docusafe.business.impl.DocumentSafeServiceImpl;
import org.adorsys.docusafe.business.types.UserID;
import org.adorsys.docusafe.business.types.complex.UserIDAuth;
import org.adorsys.encobject.domain.ReadKeyPassword;
import org.adorsys.encobject.filesystem.FileSystemExtendedStorageConnection;
import org.adorsys.encobject.service.api.ExtendedStoreConnection;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.adorsys.multibanking.auth.SystemContext;
import de.adorsys.multibanking.auth.UserContext;
import de.adorsys.sts.persistence.FsUserDataRepository;
import de.adorsys.sts.resourceserver.service.UserDataRepository;

/**
 * Sample config for the docusafe. Beware of the wrapping for exception handling.
 * @author fpo
 *
 */
@Configuration
public class DocuSafeConfig {

    @Value("${docusafe.system.user.name:mbs-system-user}")
    String docusafeSystemUserName;
    @Value("${docusafe.system.user.password:mbs-system-password}")
    String docusafeSystemUserPassword;
    @Value("${docusafe.filesystem.dir:target/docusafe}")
    String docusafeFileSystemDir;
    

    @Bean
    SystemContext systemContext() {
        UserIDAuth systemId = new UserIDAuth(new UserID(docusafeSystemUserName), new ReadKeyPassword(docusafeSystemUserPassword));
        UserContext userContext = new UserContext();
        userContext.setAuth(systemId);
        return new SystemContext(userContext);
    }

    @Bean
    DocumentSafeService docusafe(SystemContext systemContext) {
        DocumentSafeService safe = newDocusafe("multibanking");
        UserIDAuth systemId = systemContext.getUser().getAuth();
        if (!safe.userExists(systemId.getUserID())) {
            safe.createUser(systemId);
        }
        return safe;
    }

    /**
     * The user data repository is used to store user credential here while waiting to implement a proper 
     * secret server to do the work.
     * 
     * @param objectMapper
     * @return
     */
    @Bean
    UserDataRepository userDataRepository(ObjectMapper objectMapper) {
        DocumentSafeService safe = newDocusafe("usercredentials");
        return new FsUserDataRepository(safe, objectMapper);
    }
    
    public DocumentSafeService newDocusafe(String containerName){
        String dir = docusafeFileSystemDir + "/" + RandomStringUtils.randomAlphanumeric(5).toLowerCase(); 
        ExtendedStoreConnection extendedStorageConnection = new FileSystemExtendedStorageConnection(dir + "/" + containerName);
        return new DocumentSafeServiceImpl(extendedStorageConnection);
        
    }
}
