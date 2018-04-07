package de.adorsys.multibanking.web.account;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.adorsys.multibanking.exception.InvalidBankAccessException;
import de.adorsys.multibanking.exception.ResourceNotFoundException;
import de.adorsys.multibanking.exception.SyncInProgressException;
import de.adorsys.multibanking.exception.UserNotFoundException;
import de.adorsys.multibanking.exception.domain.ErrorConstants;
import de.adorsys.multibanking.service.BookingService;
import de.adorsys.multibanking.web.annotation.UserResource;
import de.adorsys.multibanking.web.common.BankAccountBasedController;
import domain.BankApi;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @author alexg on 07.02.17.
 * @author fpo 2018-03-20 11:45
 */
@UserResource
@RestController
@RequestMapping(path = BankAccountController.BASE_PATH)
public class BankAccountController extends BankAccountBasedController {
	public static final String BASE_PATH = "/api/v1/bankaccesses/{accessId}/accounts"; 

    @Autowired
    private BookingService bookingService;

    @RequestMapping(path = "/{accountId}/sync", method = RequestMethod.PUT)
    @ApiOperation(value = "Loads the user booking from the remote bank account.")
    @ApiResponses(value = { @ApiResponse(code = HttpServletResponse.SC_NO_CONTENT, message = "Bank account with provided id successfuly synchronized"),
            @ApiResponse(code = HttpServletResponse.SC_UNAUTHORIZED, message = ErrorConstants.ERR_HTTP_CODE_UNAUTHENTICATED_DOC, response = Error.class),
    		@ApiResponse(code = HttpServletResponse.SC_PRECONDITION_FAILED, message = UserNotFoundException.RENDERED_MESSAGE_KEY, response = Error.class),
    		@ApiResponse(code = HttpServletResponse.SC_NOT_FOUND, message = ResourceNotFoundException.RENDERED_MESSAGE_KEY, response = Error.class),
    		@ApiResponse(code = SyncInProgressException.SC_PROCESSING, message = SyncInProgressException.RENDERED_MESSAGE_KEY, response = Error.class),
    		@ApiResponse(code = HttpServletResponse.SC_FORBIDDEN, message = InvalidBankAccessException.MESSAGE_DOC, response = Error.class),
    		@ApiResponse(code = HttpServletResponse.SC_BAD_REQUEST, message = ErrorConstants.ERR_HTTP_CODE_BAD_REQUEST_DOC, response = Error.class)})
    public HttpEntity<Void> syncBookings(
            @PathVariable String accessId,
            @PathVariable String accountId,
            @RequestBody(required = false) String pin) {

    	checkBankAccountExists(accessId, accountId);
        checkSynch(accessId, accountId);

        BankApi bankApi=null;
		bookingService.syncBookings(accessId, accountId, bankApi, pin);

        return new ResponseEntity<>(userDataLocationHeader(),HttpStatus.NO_CONTENT);
    }
}
