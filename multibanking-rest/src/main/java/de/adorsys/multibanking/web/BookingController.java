package de.adorsys.multibanking.web;

import org.adorsys.docusafe.business.types.complex.DSDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import de.adorsys.multibanking.domain.BankAccessEntity;
import de.adorsys.multibanking.domain.BankAccountEntity;
import de.adorsys.multibanking.exception.ResourceNotFoundException;
import de.adorsys.multibanking.exception.SyncInProgressException;
import de.adorsys.multibanking.service.BankAccessService;
import de.adorsys.multibanking.service.BankAccountService;
import de.adorsys.multibanking.service.BookingService;
import de.adorsys.multibanking.service.OnlineBankingServiceProducer;
import de.adorsys.multibanking.web.common.BankAccountBasedController;
import de.adorsys.multibanking.web.common.BaseController;
import domain.BankAccount;

/**
 * @author alexg on 07.02.17.
 * @author fpo 2018-03-20 11:46
 */
@UserResource
@RestController
@SuppressWarnings({"unused"})
@RequestMapping(path = "api/v1/bankaccesses/{accessId}/accounts/{accountId}/bookings")
public class BookingController extends BankAccountBasedController {

    @Autowired
    private BookingService bookingService;
    @Autowired
    private BankAccessService bankAccessService;
    @Autowired
    private BankAccountService bankAccountService;
    @Autowired
    private OnlineBankingServiceProducer bankingServiceProducer;

    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<ByteArrayResource> getBookings(
            @PathVariable String accessId,
            @PathVariable String accountId,
            @RequestParam(required = false) String period
    ) {
    	checkBankAccountExists(accessId, accountId);
    	checkSynch(accessId, accountId);
    	return loadBytesForWeb(bookingService.getBookings(accessId, accountId, period));
    }
}
