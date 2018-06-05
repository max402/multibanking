package de.adorsys.multibanking.service.analytics;

import org.adorsys.docusafe.business.DocumentSafeService;
import org.adorsys.docusafe.business.types.complex.DSDocument;
import org.adorsys.docusafe.service.types.DocumentContent;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.adorsys.multibanking.auth.UserContext;
import de.adorsys.multibanking.auth.UserObjectPersistenceService;
import de.adorsys.multibanking.utils.FQNUtils;

/**
 * Images are stored accessible to everybody using the system id auth.
 * 
 * @author fpo 2018-03-20 06:46
 *
 */
@Service
public class CustomImageService {
    private UserObjectPersistenceService uos;
    
	
	public CustomImageService(UserContext userContext, ObjectMapper objectMapper, DocumentSafeService documentSafeService) {
        this.uos = new UserObjectPersistenceService(userContext, objectMapper, documentSafeService);
    }

    /**
	 * Check if the user has his own copy of this image.
	 * 
	 * @param imageName
	 * @return
	 */
	public boolean hasImage(String imageName){
		return uos.documentExists(FQNUtils.imageFQN(imageName), null);
	}

	/**
	 * Load image from the user repository.
	 * 
	 * @param imageName
	 * @return
	 */
	public DSDocument loadUserImage(String imageName){
        return uos.readDocument(FQNUtils.imageFQN(imageName), null);
	}
	
	/**
	 * Store an image in the user repository.
	 * 
	 * @param imageName
	 * @param data
	 */
	public void storeUserImage(String imageName, byte[] data){
        uos.store(FQNUtils.imageFQN(imageName), null, data);
	}
}
