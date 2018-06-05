package de.adorsys.multibanking.service.analytics;

import org.adorsys.docusafe.business.DocumentSafeService;
import org.adorsys.docusafe.business.types.complex.DSDocument;
import org.adorsys.docusafe.service.types.DocumentContent;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.adorsys.multibanking.auth.SystemContext;
import de.adorsys.multibanking.auth.UserObjectPersistenceService;
import de.adorsys.multibanking.utils.FQNUtils;

/**
 * Images are stored accessible to everybody using the system id auth.
 * 
 * @author fpo 2018-03-20 06:46
 *
 */
@Service
public class SystemImageService {
    private UserObjectPersistenceService uos;
    public SystemImageService(ObjectMapper objectMapper, SystemContext systemContext, DocumentSafeService documentSafeService) {
        this.uos = new UserObjectPersistenceService(systemContext.getUser(), objectMapper, documentSafeService);
    }

	public boolean hasImage(String imageName){
		return uos.documentExists(FQNUtils.imageFQN(imageName), null);
	}

	/**
	 * Load an image from the system repository.
	 * 
	 * @param imageName
	 * @return
	 */
	public DSDocument loadStaticImage(String imageName){
        return uos.readDocument(FQNUtils.imageFQN(imageName), null);
	}
	
	/**
	 * Store an image in the system repository.
	 * 
	 * @param imageName
	 * @param data
	 */
	public void storeStaticImage(String imageName, byte[] data){
	    uos.store(FQNUtils.imageFQN(imageName), null, data);
	}
}
