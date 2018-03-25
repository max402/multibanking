package de.adorsys.multibanking.service;

import org.adorsys.docusafe.business.types.complex.DSDocument;
import org.springframework.stereotype.Service;

import de.adorsys.multibanking.service.base.BaseSystemIdService;
import de.adorsys.multibanking.utils.FQNUtils;

/**
 * Images are stored accessible to everybody using the system id auth.
 * 
 * @author fpo 2018-03-20 06:46
 *
 */
@Service
public class SystemImageService extends BaseSystemIdService {

	public boolean hasImage(String imageName){
		return documentExists(FQNUtils.imageFQN(imageName));
	}

	/**
	 * Load an image from the system repository.
	 * 
	 * @param imageName
	 * @return
	 */
	public DSDocument loadStaticImage(String imageName){
		return loadDocument(FQNUtils.imageFQN(imageName));
	}
	
	/**
	 * Store an image in the system repository.
	 * 
	 * @param imageName
	 * @param data
	 */
	public void storeStaticImage(String imageName, byte[] data){
		storeDocument(FQNUtils.imageFQN(imageName), data);
	}
}
