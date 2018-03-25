package de.adorsys.multibanking.service;

import org.adorsys.docusafe.business.types.complex.DSDocument;
import org.springframework.stereotype.Service;

import de.adorsys.multibanking.service.base.BaseUserIdService;
import de.adorsys.multibanking.utils.FQNUtils;

/**
 * Images are stored accessible to everybody using the system id auth.
 * 
 * @author fpo 2018-03-20 06:46
 *
 */
@Service
public class CustomImageService extends BaseUserIdService {
	
	/**
	 * Check if the user has his own copy of this image.
	 * 
	 * @param imageName
	 * @return
	 */
	public boolean hasImage(String imageName){
		return documentExists(userIDAuth, FQNUtils.imageFQN(imageName));
	}

	/**
	 * Load image from the user repository.
	 * 
	 * @param imageName
	 * @return
	 */
	public DSDocument loadUserImage(String imageName){
		return loadDocument(userIDAuth, FQNUtils.imageFQN(imageName));
	}
	
	/**
	 * Store an image in the user repository.
	 * 
	 * @param imageName
	 * @param data
	 */
	public void storeUserImage(String imageName, byte[] data){
		storeDocument(userIDAuth, FQNUtils.imageFQN(imageName), data);
	}
}
