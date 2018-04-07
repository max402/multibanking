package de.adorsys.multibanking.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import de.adorsys.multibanking.service.BookingService;
import de.adorsys.multibanking.web.annotation.UserResource;
import de.adorsys.multibanking.web.common.BankAccountBasedController;

/**
 * @author alexg on 07.02.17.
 * @author fpo 2018-03-20 11:46
 */
@UserResource
@RestController
@RequestMapping(path = BookingController.BASE_PATH)
public class BookingController extends BankAccountBasedController {
	public static final String BASE_PATH = "/api/v1/bankaccesses/{accessId}/accounts/{accountId}/bookings"; 

    @Autowired
    private BookingService bookingService;

    @RequestMapping(method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_UTF8_VALUE }, params={"period"})
    public @ResponseBody ResponseEntity<ByteArrayResource> getBookings(
            @PathVariable String accessId,
            @PathVariable String accountId,
            @RequestParam(required = false, name="period") String period
    ) {
    	checkBankAccountExists(accessId, accountId);
    	checkSynch(accessId, accountId);
    	return loadBytesForWeb(bookingService.getBookings(accessId, accountId, period));
    }
}
