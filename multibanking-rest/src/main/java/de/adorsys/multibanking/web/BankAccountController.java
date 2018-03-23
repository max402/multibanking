package de.adorsys.multibanking.web;

import de.adorsys.multibanking.domain.BankAccessEntity;
import de.adorsys.multibanking.domain.BankAccountEntity;
import de.adorsys.multibanking.domain.BookingEntity;
import de.adorsys.multibanking.exception.ResourceNotFoundException;
import de.adorsys.multibanking.exception.SyncInProgressException;
import de.adorsys.multibanking.pers.spi.repository.BankAccessRepositoryIf;
import de.adorsys.multibanking.pers.spi.repository.BankAccountRepositoryIf;
import de.adorsys.multibanking.service.BankAccessService;
import de.adorsys.multibanking.service.BankAccountService;
import de.adorsys.multibanking.service.BookingService;
import de.adorsys.multibanking.web.common.BankAccountBasedController;
import de.adorsys.multibanking.web.common.BaseController;
import domain.BankAccount;
import domain.BankApi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * @author alexg on 07.02.17.
 * @author fpo 2018-03-20 11:45
 */
@UserResource
@RestController
@SuppressWarnings("unused")
@RequestMapping(path = "api/v1/bankaccesses/{accessId}/accounts")
public class BankAccountController extends BankAccountBasedController {

    private static final Logger log = LoggerFactory.getLogger(BankAccountController.class);

    @Autowired
    private BookingService bookingService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<BankAccountEntity>> getBankAccounts(@PathVariable String accessId) {
    	checkBankAccessExists(accessId);
    	return returnDocument(bankAccountService.loadForBankAccess(accessId));
    }

    @RequestMapping(value = "/{accountId}", method = RequestMethod.GET)
    public ResponseEntity<BankAccountEntity> getBankAccount(
    		@PathVariable String accessId, @PathVariable("accountId") String accountId) {
    	checkBankAccessExists(accessId);
        BankAccountEntity bankAccountEntity = bankAccountService.getBankAccount(accessId, accountId)
                .orElseThrow(() -> new ResourceNotFoundException(BankAccountEntity.class, accountId));
        return returnDocument(bankAccountEntity);
    }

    @RequestMapping(path = "/{accountId}/sync", method = RequestMethod.PUT)
    public HttpEntity<Void> syncBookings(
            @PathVariable String accessId,
            @PathVariable String accountId,
            @RequestBody(required = false) String pin) {
    	
        BankAccessEntity bankAccess = bankAccessService.loadbankAccess(accessId)
                .orElseThrow(() -> new ResourceNotFoundException(BankAccessEntity.class, accessId));

        BankAccountEntity bankAccount = bankAccountService.getBankAccount(accessId, accountId)
                .orElseThrow(() -> new ResourceNotFoundException(BankAccountEntity.class, accountId));

        checkSynch(accessId, accountId);

        BankApi bankApi=null;
		bookingService.syncBookings(bankAccess, bankAccount, bankApi, pin);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
