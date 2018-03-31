package de.adorsys.multibanking.service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.adorsys.docusafe.business.types.complex.DocumentFQN;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;

import de.adorsys.multibanking.domain.BankAccessCredentials;
import de.adorsys.multibanking.domain.BankAccessEntity;
import de.adorsys.multibanking.domain.BankAccountEntity;
import de.adorsys.multibanking.domain.UserEntity;
import de.adorsys.multibanking.exception.BankAccessAlreadyExistException;
import de.adorsys.multibanking.exception.InvalidPinException;
import de.adorsys.multibanking.exception.ResourceNotFoundException;
import de.adorsys.multibanking.service.base.BaseUserIdService;
import de.adorsys.multibanking.utils.FQNUtils;
import de.adorsys.multibanking.utils.Ids;
import domain.BankApiUser;
import spi.OnlineBankingService;

/**
 * A user can have 0 to N bank accesses.
 * 
 * @author fpo
 *
 */
@Service
public class BankAccessService extends BaseUserIdService  {

	private static final Logger log = LoggerFactory.getLogger(BankAccessService.class);

	@Autowired
    private OnlineBankingServiceProducer bankingServiceProducer;

    @Autowired
    private UserService userService;
    @Autowired
    private BankAccountService bankAccountService;
	
    
    /**
     * Create a bank access
     * - load and store bank accounts
     * 
     * @param bankAccess
     * @return
     */
    public BankAccessEntity createBankAccess(BankAccessEntity bankAccess) {
    	// Set user and access id
    	bankAccess.setUserId(auth().getUserID().getValue());
    	// Set an accessId if none.
    	if(StringUtils.isBlank(bankAccess.getId())){
    		bankAccess.setId(Ids.uuid());
    	} else {
    		// Check bank Access with Id does not exists.
    		if(exists(bankAccess.getId())){
    			throw new BankAccessAlreadyExistException(bankAccess.getId());
    		}
    	}

    	BankAccessCredentials credentials = BankAccessCredentials.cloneCredentials(bankAccess);
    	// Clean credentials
    	BankAccessCredentials.cleanCredentials(bankAccess);

		// disect credentials
        if (bankAccess.isStorePin()) {
        	store(FQNUtils.credentialFQN(credentials.getAccessId()), credentialsType(), credentials);
        }

        // store bank access
    	storeBankAccess(bankAccess);
    	
    	try {
	    	// pull and store bank accounts
	    	bankAccountService.synchBankAccounts(bankAccess, credentials);
    	} catch (InvalidPinException e){
    		// Set pin valid state to false.
            if (bankAccess.isStorePin()) {
            	invalidate(credentials);
            }
            throw e;
    	}
        
    	return bankAccess;
    }
    
	public List<BankAccessEntity> getBankAccesses() {
		return load(FQNUtils.bankAccessListFQN(), accessEntitiesType())
				.orElse(Collections.emptyList());
	}

    /**
     * Update the bank access object.
     * Credentials are reset but not updated. USe another interface for managing credentials.
     * 
     * @param bankAccessEntity
     */
	public void updateBankAccess(BankAccessEntity bankAccessEntity) {
		storeBankAccess(bankAccessEntity);
    }

    public boolean deleteBankAccess(String accessId) {
		int deleted = deleteListById(Collections.singletonList(accessId), BankAccessEntity.class, accessEntitiesType(), FQNUtils.bankAccessListFQN());
		if(deleted>0){
			// TODO: for transactionality. Still check existence of these files.
	    	removeRemoteRegistrations(accessId);
	    	deleteDirectory(FQNUtils.bankAccessDirFQN(accessId));
	    	return true;
		}
		return false;
    }

	public void setInvalidPin(String accessId) {
		DocumentFQN credentialsFQN = FQNUtils.credentialFQN(accessId);
		BankAccessCredentials credentials = load(credentialsFQN, credentialsType())
				.orElseThrow(() -> resourceNotFound(BankAccessCredentials.class, accessId));
		invalidate(credentials);
	}
	
	public BankAccessCredentials loadCredentials(String accessId){
		return load(FQNUtils.credentialFQN(accessId), credentialsType())
				.orElseThrow(() -> resourceNotFound(BankAccessCredentials.class, accessId));
	}
	
	/**
	 * Check existence by checking if the file containing the list of bank accounts exits.
	 * 
	 * @param accessId
	 * @return
	 */
	public boolean exists(String accessId){
		return documentExists(FQNUtils.bankAccountsFileFQN(accessId));
	}

	private void invalidate(BankAccessCredentials credentials) {
		DocumentFQN credentialsFQN = FQNUtils.credentialFQN(credentials.getAccessId());
		credentials.setPinValid(false);
		credentials.setLastValidationDate(new Date());
    	store(credentialsFQN, credentialsType(), credentials);
	}
	
    /*
     * Clean bank access credential before storage. 
     * @param bankAccess
     */
	private void storeBankAccess(BankAccessEntity bankAccess) {
		BankAccessCredentials.cleanCredentials(bankAccess);
		updateList(Collections.singletonList(bankAccess), 
				BankAccessEntity.class, accessEntitiesType(), FQNUtils.bankAccessListFQN());
	}
	
	public Optional<BankAccessEntity> loadbankAccess(String bankAcessId){
		return find(bankAcessId, BankAccessEntity.class, 
				accessEntitiesType(), FQNUtils.bankAccessListFQN());
	}

	private void removeRemoteRegistrations(String accessId) {
    	// Load bank Accounts
		List<BankAccountEntity> bankAccountEntities = bankAccountService.loadForBankAccess(accessId);
        UserEntity userEntity = userService.readUser();
        
        bankAccountEntities.stream().forEach(bankAccountEntity -> {
		   	bankAccountEntity.getExternalIdMap().keySet().forEach(bankApi -> {
	   			OnlineBankingService bankingService = bankingServiceProducer.getBankingService(bankApi);
	   			//remove remote bank api user
	   			if (bankingService.userRegistrationRequired()) {
	   				BankApiUser bankApiUser = userEntity.getApiUser()
	   						.stream()
	   						.filter(apiUser -> apiUser.getBankApi() == bankApi)
	   						.findFirst()
	   						.orElseThrow(() -> new ResourceNotFoundException(BankApiUser.class, bankApi.toString()));
	   				bankingService.removeBankAccount(bankAccountEntity, bankApiUser);
	   			}
	   		});
	   	});
	}
	
	private static TypeReference<List<BankAccessEntity>> accessEntitiesType(){
		return new TypeReference<List<BankAccessEntity>>() {};
	}
	
	private static TypeReference<BankAccessCredentials> credentialsType(){
		return new TypeReference<BankAccessCredentials>() {};
	}
	
}
