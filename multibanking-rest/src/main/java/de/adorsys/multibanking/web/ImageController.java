package de.adorsys.multibanking.web;

import java.io.IOException;

import org.adorsys.cryptoutils.exceptions.BaseException;
import org.adorsys.docusafe.business.types.complex.DSDocument;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import de.adorsys.multibanking.service.ImageService;
import de.adorsys.multibanking.web.common.BaseController;

/**
 * The image controller.
 * - User can upload image in his repository
 * - User can share image with admin
 * - Admin can release image
 * 
 * @author fpo
 *
 */
@UserResource
@RestController
@RequestMapping(path = "api/v1/image")
public class ImageController extends BaseController {
	@Autowired
	private ImageService imageService;

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
	@GetMapping(value = "/{imageName}", produces = MediaType.IMAGE_PNG_VALUE)
	public @ResponseBody ResponseEntity<ByteArrayResource>  getImage(@PathVariable String imageName) throws IOException {
		DSDocument loadedImage = imageService.loadImage(imageName);
		return loadBytesForWeb(loadedImage, MediaType.IMAGE_PNG);
	}

	@RequestMapping(path = "/{imageName}", method = RequestMethod.PUT, consumes=MediaType.IMAGE_PNG_VALUE)
    public HttpEntity<?> putImage(@PathVariable String imageName, @RequestParam MultipartFile imageFile) {
        if (!imageFile.isEmpty())return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("File is empty");
        try {
			imageService.storeUserImage(imageName, IOUtils.toByteArray(imageFile.getInputStream()));
		} catch (IOException e) {
			throw new BaseException(e);
		}
        return new ResponseEntity<>(HttpStatus.OK);
    }
	
	@RequestMapping(path = "/{imageName}/release", method = RequestMethod.POST)
    public HttpEntity<?> patchImage(@PathVariable String imageName) {
		imageService.releaseImage(imageName);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
