package de.adorsys.multibanking.web.common;

import org.springframework.beans.factory.annotation.Autowired;

import de.adorsys.multibanking.domain.BankAccessEntity;
import de.adorsys.multibanking.exception.ResourceNotFoundException;
import de.adorsys.multibanking.service.BankDataService;
import de.adorsys.multibanking.service.BankService;

public class BankAccessBasedController extends BaseController {
    @Autowired
    protected BankDataService bds;
    @Autowired
    protected BankService bankService;
    protected void checkBankAccessExists(String accessId){
        if (!bds.accessExists(accessId)) 
            throw new ResourceNotFoundException(BankAccessEntity.class, accessId);
    }
}
