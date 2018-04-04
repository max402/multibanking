package de.adorsys.multibanking.service;

import java.util.Collection;
import java.util.Date;
import java.util.Optional;

import org.adorsys.docusafe.business.types.complex.DocumentFQN;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;

import de.adorsys.multibanking.domain.BankAccessCredentials;
import de.adorsys.multibanking.domain.BankAccessData;
import de.adorsys.multibanking.domain.BankAccessEntity;
import de.adorsys.multibanking.domain.BankAccountData;
import de.adorsys.multibanking.domain.BankAccountEntity;
import de.adorsys.multibanking.domain.UserData;
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

	@Autowired
    UserDataService uds;

	@Autowired
    private OnlineBankingServiceProducer bankingServiceProducer;

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
//    
//	public List<BankAccessEntity> getBankAccesses() {
//		return load(FQNUtils.bankAccessListFQN(), accessEntitiesType())
//				.orElse(Collections.emptyList());
//	}

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
    	UserData userData = uds.load();
    	BankAccessData accessData = userData.getBankAccesses().remove(accessId);
		if(accessData!=null){
			uds.store(userData);
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
		UserData userData = uds.load();
		return userData.getBankAccesses().containsKey(accessId);
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
		UserData userData = uds.load();
		BankAccessData accessData = userData.getBankAccess(bankAccess.getId())
				.orElseGet(() -> {
					BankAccessData b = new BankAccessData();
					userData.getBankAccesses().put(bankAccess.getId(), b);
					return b;
				});

		accessData.setBankAccess(bankAccess);
		uds.store(userData);
	}
	
//	public Optional<BankAccessEntity> loadbankAccess(String bankAcessId){
//		UserData userData = uds.load();
//		if(userData.getBankAccesses().containsKey(bankAcessId)) return Optional.empty();
//		return userData.getBankAccesses().containsKey(bankAcessId)?
//				Optional.of(userData.getBankAccesses().get(bankAcessId).getBankAccess()):
//					Optional.empty();
//	}

	private void removeRemoteRegistrations(String accessId) {
		UserData userData = uds.load();
		if(!userData.getBankAccesses().containsKey(accessId)) return;
		BankAccessData accessData = userData.getBankAccesses().get(accessId);
		
    	// Load bank Accounts
		Collection<BankAccountData> bankAccountDataList = accessData.getBankAccounts().values();
        UserEntity userEntity = userData.getUserEntity();
        
		bankAccountDataList.stream().forEach(bankAccountData -> {
			BankAccountEntity bankAccountEntity = bankAccountData.getBankAccount();
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

	private static TypeReference<BankAccessCredentials> credentialsType(){
		return new TypeReference<BankAccessCredentials>() {};
	}
	
}
