package de.adorsys.multibanking.web;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import de.adorsys.multibanking.domain.BankAccessEntity;
import de.adorsys.multibanking.web.common.BankAccessBasedController;

/**
 * @author alexg on 07.02.17.
 * @author fpo 2018-03-20 11:45
 */
@UserResource
@RestController
@Controller
@RequestMapping(path = BankAccessController.BASE_PATH)
public class BankAccessController extends BankAccessBasedController {
	public static final String BASE_PATH = "/api/v1/bankaccesses"; 
	
    private final static Logger LOGGER = LoggerFactory.getLogger(BankAccessController.class);
    
    @RequestMapping(method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
    public @ResponseBody ResponseEntity<List<BankAccessEntity>>  getBankAccesses() {
    	return returnDocument(bankAccessService.getBanAccesses(), MediaType.APPLICATION_JSON_UTF8);
    }
    
    @RequestMapping(value = "/{accessId}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
    public @ResponseBody ResponseEntity<BankAccessEntity>  getBankAccess(@PathVariable String accessId) {
    	return returnDocument(bankAccessService.loadbankAccess(accessId)
    			.orElseThrow(() -> resourceNotFound(BankAccessEntity.class, accessId)), MediaType.APPLICATION_JSON_UTF8);
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public HttpEntity<Void> createBankaccess(@RequestBody BankAccessEntity bankAccess) {
    	bankAccessService.createBankAccess(bankAccess);
    	// Trigger Perform Services operations.
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(linkTo(methodOn(BankAccessController.class).getBankAccesses()).toUri());
		LOGGER.info("Start getBankAccesses for " + userId());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{accessId}", method = RequestMethod.DELETE)
    public HttpEntity<Void> deleteBankAccess(@PathVariable String accessId) {
        if (bankAccessService.deleteBankAccess(accessId)) {
        	LOGGER.info("Bank Access [{}] deleted.", accessId);
        } else {
            return new ResponseEntity<Void>(HttpStatus.GONE);
        }

        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }
}
