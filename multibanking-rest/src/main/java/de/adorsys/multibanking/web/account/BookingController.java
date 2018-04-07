package de.adorsys.multibanking.web.account;

import javax.servlet.http.HttpServletResponse;

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

import de.adorsys.multibanking.exception.InvalidBankAccessException;
import de.adorsys.multibanking.exception.ResourceNotFoundException;
import de.adorsys.multibanking.exception.SyncInProgressException;
import de.adorsys.multibanking.exception.UnexistentBookingFileException;
import de.adorsys.multibanking.exception.UserNotFoundException;
import de.adorsys.multibanking.exception.domain.ErrorConstants;
import de.adorsys.multibanking.service.BookingService;
import de.adorsys.multibanking.web.annotation.UserResource;
import de.adorsys.multibanking.web.common.BankAccountBasedController;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

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
    @ApiOperation(value = "Loads the user booking from the remote bank account.")
    @ApiResponses(value = { @ApiResponse(code = HttpServletResponse.SC_OK, message = "Ok"),
            @ApiResponse(code = HttpServletResponse.SC_UNAUTHORIZED, message = ErrorConstants.ERR_HTTP_CODE_UNAUTHENTICATED_DOC, response = Error.class),
    		@ApiResponse(code = HttpServletResponse.SC_PRECONDITION_FAILED, message = UserNotFoundException.RENDERED_MESSAGE_KEY, response = Error.class),
    		@ApiResponse(code = HttpServletResponse.SC_NOT_FOUND, message = ResourceNotFoundException.RENDERED_MESSAGE_KEY, response = Error.class),
    		@ApiResponse(code = HttpServletResponse.SC_BAD_REQUEST, message = UnexistentBookingFileException.MESSAGE_DOC, response = Error.class)})
    public @ResponseBody ResponseEntity<ByteArrayResource> getBookings(
            @PathVariable String accessId,
            @PathVariable String accountId,
            @RequestParam(required = true, name="period") String period
    ) {
    	checkBankAccountExists(accessId, accountId);
    	checkSynch(accessId, accountId);
    	return loadBytesForWeb(bookingService.getBookings(accessId, accountId, period));
    }
}
