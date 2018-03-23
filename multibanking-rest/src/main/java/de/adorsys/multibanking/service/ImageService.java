package de.adorsys.multibanking.service;

import org.adorsys.docusafe.business.types.complex.DSDocument;
import org.adorsys.docusafe.business.types.complex.DocumentFQN;
import org.springframework.stereotype.Service;

import de.adorsys.multibanking.service.base.BaseService;
import de.adorsys.multibanking.utils.FQNUtils;

/**
 * Images are stored accessible to everybody using the system id auth.
 * 
 * @author fpo 2018-03-20 06:46
 *
 */
@Service
public class ImageService extends BaseService {
	
	/**
	 * Loading an image to display to the user. If the user has put an image with the same 
	 * name in his repository, this image will be displayed. If not the system image will
	 * be displayed. 
	 * 
	 * This give some power user the chance to test image with their account before sharing
	 * the image with the rest of the system.
	 * 
	 * @param imageName
	 * @return
	 */
	public DSDocument loadImage(String imageName){
		DocumentFQN imageFQN = FQNUtils.imageFQN(imageName);
		if(documentExists(userIDAuth, imageFQN)) return loadUserImage(imageName);
		return loadStaticImage(imageName);
	}

	/**
	 * Load image from the user repository.
	 * 
	 * @param imageName
	 * @return
	 */
	public DSDocument loadUserImage(String imageName){
		DocumentFQN imageFQN = FQNUtils.imageFQN(imageName);
		return loadDocument(userIDAuth, imageFQN);
	}
	
	/**
	 * Store an image in the user repository.
	 * 
	 * @param imageName
	 * @param data
	 */
	public void storeUserImage(String imageName, byte[] data){
		DocumentFQN imageFQN = FQNUtils.imageFQN(imageName);
		storeDocument(userIDAuth, imageFQN, data);
	}

	/**
	 * Load an image from the system repository.
	 * 
	 * @param imageName
	 * @return
	 */
	public DSDocument loadStaticImage(String imageName){
		DocumentFQN imageFQN = FQNUtils.imageFQN(imageName);
		return loadDocument(systemIDAuth.getUserIDAuth(), imageFQN);
	}
	
	/**
	 * Store an image in the system repository.
	 * 
	 * @param imageName
	 * @param data
	 */
	public void storeStaticImage(String imageName, byte[] data){
		DocumentFQN imageFQN = FQNUtils.imageFQN(imageName);
		storeDocument(systemIDAuth.getUserIDAuth(), imageFQN, data);
	}

	/**
	 * Move the image from the user repository to the system repository.
	 * 
	 * @param imageName
	 */
	public void releaseImage(String imageName) {
		DSDocument loadUserImage = loadUserImage(imageName);
		storeStaticImage(imageName, loadUserImage.getDocumentContent().getValue());
	}
}
