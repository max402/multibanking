package de.adorsys.multibanking.web.account;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import de.adorsys.multibanking.domain.BankAccessEntity;
import de.adorsys.multibanking.exception.BankAccessAlreadyExistException;
import de.adorsys.multibanking.exception.InvalidBankAccessException;
import de.adorsys.multibanking.exception.UserNotFoundException;
import de.adorsys.multibanking.exception.domain.ErrorConstants;
import de.adorsys.multibanking.web.annotation.UserResource;
import de.adorsys.multibanking.web.common.BankAccessBasedController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @author alexg on 07.02.17.
 * @author fpo 2018-03-20 11:45
 */
@UserResource
@RestController
@Controller
@RequestMapping(path = BankAccessController.BASE_PATH)
@Api(value = BankAccessController.BASE_PATH, 
	tags = "Bank Access")
public class BankAccessController extends BankAccessBasedController {
	public static final String BASE_PATH = "/api/v1/bankaccesses"; 
    private final static Logger LOGGER = LoggerFactory.getLogger(BankAccessController.class);

    @RequestMapping(method = RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "Creates and adds a new bank access to the list of bank accesses of this user.")
    @ApiResponses(value = { @ApiResponse(code = HttpServletResponse.SC_CREATED, message = "Provided bank access successfuly created"),
            @ApiResponse(code = HttpServletResponse.SC_UNAUTHORIZED, message = ErrorConstants.ERR_HTTP_CODE_UNAUTHENTICATED_DOC, response = Error.class),
    		@ApiResponse(code = HttpServletResponse.SC_PRECONDITION_FAILED, message = UserNotFoundException.RENDERED_MESSAGE_KEY, response = Error.class),
    		@ApiResponse(code = HttpServletResponse.SC_FORBIDDEN, message = InvalidBankAccessException.MESSAGE_DOC, response = Error.class),
            @ApiResponse(code = HttpServletResponse.SC_CONFLICT, message = BankAccessAlreadyExistException.MESSAGE_DOC, response = Error.class),
            @ApiResponse(code = HttpServletResponse.SC_BAD_REQUEST, message = ErrorConstants.ERR_HTTP_CODE_BAD_REQUEST_DOC, response = Error.class) })
    public HttpEntity<Void> createBankaccess(@ApiParam(name = "bankAccess", 
    	value = "The bank access data containing (Bank Code, Bank name, Bank login, PIN") 
    	@RequestBody(required = true) BankAccessEntity bankAccess) {
		bankAccessService.createBankAccess(bankAccess);
		// Trigger Perform Services operations.
		LOGGER.info("Bank access created for " + userId());
		return new ResponseEntity<>(userDataLocationHeader(), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{accessId}", method = RequestMethod.DELETE)
    @ApiOperation(value = "Deletes the bank access with the given id.")
    @ApiResponses(value = { @ApiResponse(code = HttpServletResponse.SC_NO_CONTENT, message = "Bank access with provided id successfuly deleted"),
    		@ApiResponse(code = HttpServletResponse.SC_UNAUTHORIZED, message = ErrorConstants.ERR_HTTP_CODE_UNAUTHENTICATED_DOC, response = Error.class),
    		@ApiResponse(code = HttpServletResponse.SC_PRECONDITION_FAILED, message = UserNotFoundException.RENDERED_MESSAGE_KEY, response = Error.class),
    		@ApiResponse(code = HttpServletResponse.SC_GONE, message = "Bank access with provided id is gone")})
    public HttpEntity<Void> deleteBankAccess(@PathVariable String accessId) {
        if (bankAccessService.deleteBankAccess(accessId)) {
        	LOGGER.info("Bank Access [{}] deleted.", accessId);
        	return new ResponseEntity<Void>(userDataLocationHeader(), HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<Void>(HttpStatus.GONE);
        }
    }
}
