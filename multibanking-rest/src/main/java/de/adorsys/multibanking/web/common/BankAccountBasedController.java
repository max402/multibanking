package de.adorsys.multibanking.web.common;

import org.springframework.beans.factory.annotation.Autowired;

import de.adorsys.multibanking.domain.BankAccessEntity;
import de.adorsys.multibanking.exception.ResourceNotFoundException;
import de.adorsys.multibanking.exception.SyncInProgressException;
import de.adorsys.multibanking.service.BankAccountService;
import domain.BankAccount;

public abstract class BankAccountBasedController extends BankAccessBasedController {

    @Autowired
    protected BankAccountService bankAccountService;

    protected void checkBankAccountExists(String accessId, String accountId){
    	checkBankAccessExists(accessId);
        if (!bankAccountService.exists(accessId, accountId)) 
            throw new ResourceNotFoundException(BankAccessEntity.class, accessId);
    }
    
    protected void checkSynch(String accessId, String accountId){
        if (bankAccountService.getSyncStatus(accessId, accountId) == BankAccount.SyncStatus.SYNC) {
            throw new SyncInProgressException(accountId);
        }
    }
}
