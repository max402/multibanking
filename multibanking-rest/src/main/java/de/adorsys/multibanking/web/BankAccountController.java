package de.adorsys.multibanking.web;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.adorsys.multibanking.domain.BankAccessEntity;
import de.adorsys.multibanking.domain.BankAccountEntity;
import de.adorsys.multibanking.exception.ResourceNotFoundException;
import de.adorsys.multibanking.service.BookingService;
import de.adorsys.multibanking.web.common.BankAccountBasedController;
import domain.BankApi;

/**
 * @author alexg on 07.02.17.
 * @author fpo 2018-03-20 11:45
 */
@UserResource
@RestController
@SuppressWarnings("unused")
@RequestMapping(path = BankAccountController.BASE_PATH)
public class BankAccountController extends BankAccountBasedController {
	public static final String BASE_PATH = "/api/v1/bankaccesses/{accessId}/accounts"; 

    private static final Logger LOGGER = LoggerFactory.getLogger(BankAccountController.class);

    @Autowired
    private BookingService bookingService;

    @RequestMapping(method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
    public ResponseEntity<List<BankAccountEntity>> getBankAccounts(@PathVariable String accessId) {
    	checkBankAccessExists(accessId);
    	return returnDocument(bankAccountService.loadForBankAccess(accessId), MediaType.APPLICATION_JSON_UTF8);
    }

    @RequestMapping(value = "/{accountId}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
    public ResponseEntity<BankAccountEntity> getBankAccount(
    		@PathVariable String accessId, @PathVariable("accountId") String accountId) {
    	checkBankAccessExists(accessId);
        BankAccountEntity bankAccountEntity = bankAccountService.loadBankAccount(accessId, accountId)
                .orElseThrow(() -> new ResourceNotFoundException(BankAccountEntity.class, accountId));
        return returnDocument(bankAccountEntity, MediaType.APPLICATION_JSON_UTF8);
    }

    @RequestMapping(path = "/{accountId}/sync", method = RequestMethod.PUT)
    public HttpEntity<Void> syncBookings(
            @PathVariable String accessId,
            @PathVariable String accountId,
            @RequestBody(required = false) String pin) {
    	
        BankAccessEntity bankAccess = bankAccessService.loadbankAccess(accessId)
                .orElseThrow(() -> new ResourceNotFoundException(BankAccessEntity.class, accessId));

        BankAccountEntity bankAccount = bankAccountService.loadBankAccount(accessId, accountId)
                .orElseThrow(() -> new ResourceNotFoundException(BankAccountEntity.class, accountId));

        checkSynch(accessId, accountId);

        BankApi bankApi=null;
		bookingService.syncBookings(bankAccess, bankAccount, bankApi, pin);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
