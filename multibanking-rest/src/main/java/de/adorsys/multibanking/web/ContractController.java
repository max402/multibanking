package de.adorsys.multibanking.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.adorsys.multibanking.domain.ContractEntity;
import de.adorsys.multibanking.service.ContractService;
import de.adorsys.multibanking.web.common.BankAccountBasedController;

/**
 * 
 * @author fpo 2018-03-20 11:50
 *
 */
@UserResource
@RestController
@RequestMapping(path = "api/v1/bankaccesses/{accessId}/accounts/{accountId}/contracts")
public class ContractController extends BankAccountBasedController {

    @Autowired
    private ContractService contractService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<ContractEntity>> getContracts(@PathVariable String accessId, @PathVariable String accountId) {
    	checkBankAccountExists(accessId, accountId);
    	checkSynch(accessId, accountId);
    	return returnDocument(contractService.getContracts(accessId, accountId));
    }
}
