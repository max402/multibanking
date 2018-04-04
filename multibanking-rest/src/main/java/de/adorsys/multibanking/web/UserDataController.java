package de.adorsys.multibanking.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import de.adorsys.multibanking.service.UserDataService;
import de.adorsys.multibanking.web.common.BankAccessBasedController;

/**
 * @author fpo 2018-03-20 11:45
 */
@UserResource
@RestController
@Controller
@RequestMapping(path = UserDataController.BASE_PATH)
public class UserDataController extends BankAccessBasedController {
	public static final String BASE_PATH = "/api/v1";
	
	@Autowired
	private UserDataService uds;
    
	/**
	 * Returns a document containing the last stored and flushed version of user data.
	 * 
	 * @return
	 */
    @RequestMapping(method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
    public @ResponseBody ResponseEntity<ByteArrayResource> loadUserData() {
    	return loadBytesForWeb(uds.loadDocument());
    }
    
}
