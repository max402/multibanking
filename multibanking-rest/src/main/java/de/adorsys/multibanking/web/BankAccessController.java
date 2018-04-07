package de.adorsys.multibanking.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.adorsys.multibanking.domain.BankAccessEntity;
import de.adorsys.multibanking.exception.BankAccessAlreadyExistException;
import de.adorsys.multibanking.web.annotation.UserResource;
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

    @RequestMapping(method = RequestMethod.POST)
    public HttpEntity<Void> createBankaccess(@RequestBody BankAccessEntity bankAccess) {
    	try {
    		bankAccessService.createBankAccess(bankAccess);
    		// Trigger Perform Services operations.
    		LOGGER.info("Bank access created for " + userId());
    		return new ResponseEntity<>(userDataLocationHeader(), HttpStatus.CREATED);
    	} catch(BankAccessAlreadyExistException e){
    		return new ResponseEntity<>(HttpStatus.CONFLICT);
    	}
    }

    @RequestMapping(value = "/{accessId}", method = RequestMethod.DELETE)
    public HttpEntity<Void> deleteBankAccess(@PathVariable String accessId) {
        if (bankAccessService.deleteBankAccess(accessId)) {
        	LOGGER.info("Bank Access [{}] deleted.", accessId);
        } else {
            return new ResponseEntity<Void>(HttpStatus.GONE);
        }

        return new ResponseEntity<Void>(userDataLocationHeader(), HttpStatus.NO_CONTENT);
    }
}
