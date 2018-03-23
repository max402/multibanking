package de.adorsys.multibanking.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping(path = "api/v1/bank")
public class BankController extends BaseController {

	@Autowired
	BankService bankService;

	@RequestMapping(value = "/{bankCode}", method = RequestMethod.GET)
	public ResponseEntity<BankEntity> getBank(@PathVariable String bankCode) {
		BankEntity bankEntity = bankService.findByBankCode(bankCode)
				.orElseThrow(() -> new ResourceNotFoundException(BankEntity.class, bankCode));
		return returnDocument(bankEntity);
	}

	@GetMapping
	public  ResponseEntity<List<BankEntity>> searchBank(@RequestParam String query) {
		return returnDocument(bankService.load());
	}
}
