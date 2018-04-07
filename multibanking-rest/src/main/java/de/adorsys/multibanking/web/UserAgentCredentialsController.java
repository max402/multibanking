package de.adorsys.multibanking.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.adorsys.multibanking.domain.UserAgentCredentials;
import de.adorsys.multibanking.service.UserAgentCredentialsService;
import de.adorsys.multibanking.web.annotation.UserResource;
import de.adorsys.multibanking.web.common.BaseController;

/**
 * @author fpo 2018-04-04 07:01
 */
@UserResource
@RestController
@Controller
@RequestMapping(path = UserAgentCredentialsController.BASE_PATH)
public class UserAgentCredentialsController extends BaseController {
	public static final String BASE_PATH = "/api/v1/useragents/{userAgentId}"; 
	
    private final static Logger LOGGER = LoggerFactory.getLogger(UserAgentCredentialsController.class);
    
    @Autowired
    private UserAgentCredentialsService userAgentCredentialsService;

    @RequestMapping(method = RequestMethod.GET, produces =MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserAgentCredentials> getUserAgentCredentials(@PathVariable String userAgentId) {
    	UserAgentCredentials credentials = userAgentCredentialsService.load(userAgentId);
//		LOGGER.info("User agent credential requested for " + userId());
        return returnDocument(credentials, MediaType.APPLICATION_JSON_UTF8);
    }
    
    @RequestMapping(method = RequestMethod.PUT, consumes =MediaType.APPLICATION_JSON_UTF8_VALUE)
    public HttpEntity<Void> postUserAgentCredentials(@PathVariable String userAgentId, 
    		@RequestBody UserAgentCredentials userAgentCredentials) {
		userAgentCredentials.setUserAgentId(userAgentId);
		userAgentCredentialsService.store(userAgentCredentials);
		LOGGER.info("User agent credential stored for " + userId());
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
