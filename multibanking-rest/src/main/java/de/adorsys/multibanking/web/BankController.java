package de.adorsys.multibanking.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.adorsys.multibanking.domain.BankEntity;
import de.adorsys.multibanking.exception.ResourceNotFoundException;
import de.adorsys.multibanking.service.BankService;
import de.adorsys.multibanking.web.common.BaseController;

/**
 * @author alexg on 07.02.17.
 * @author fpo 2018-03-20 11:45
 */
@UserResource
@RestController
@RequestMapping(path = BankController.BASE_PATH)
public class BankController extends BaseController {
	public static final String BASE_PATH = "/api/v1/bank"; 

	@Autowired
	BankService bankService;

	@RequestMapping(value = "/{bankCode}", method = RequestMethod.GET)
	public ResponseEntity<BankEntity> getBank(@PathVariable String bankCode) {
		BankEntity bankEntity = bankService.findByBankCode(bankCode)
				.orElseThrow(() -> new ResourceNotFoundException(BankEntity.class, bankCode));
		return returnDocument(bankEntity, MediaType.APPLICATION_JSON_UTF8);
	}

    @RequestMapping(method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_UTF8_VALUE }, params="query")
	public  ResponseEntity<ByteArrayResource> searchBank(@RequestParam(name="query") String query) {
		return loadBytesForWeb(bankService.search(query), MediaType.APPLICATION_JSON_UTF8);
	}
}
