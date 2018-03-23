package de.adorsys.multibanking.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.IntStream;

import org.adorsys.docusafe.business.types.complex.DocumentFQN;
import org.adorsys.docusafe.business.types.complex.UserIDAuth;
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
import de.adorsys.multibanking.exception.InvalidPinException;
import de.adorsys.multibanking.exception.ResourceNotFoundException;
import de.adorsys.multibanking.service.base.BaseService;
import de.adorsys.multibanking.utils.FQNUtils;
import de.adorsys.multibanking.utils.IdFactory;
import domain.BankApiUser;
import spi.OnlineBankingService;

/**
 * A user can 0 to N bank accesses.
 * 
 * @author fpo
 *
 */
@Service
public class BankAccessService extends BaseService  {

    @Autowired
    private OnlineBankingServiceProducer bankingServiceProducer;

    @Autowired
    private UserIDAuth userIDAuth;
    
    @Autowired
    private UserService userService;
    @Autowired
    private BankAccountService bankAccountService;
	
    private static final Logger log = LoggerFactory.getLogger(BankAccessService.class);
    
    /**
     * Create a bank access
     * - load and store bank accounts
     * Any PIN information found here will be deleted. PIN storage is implemented in a proper process.
     * 
     * @param bankAccess
     * @return
     */
    public BankAccessEntity createBankAccess(BankAccessEntity bankAccess) {
    	// Set user and access id
    	bankAccess.setUserId(userIDAuth.getUserID().getValue());
    	// Set an accessId if none.
    	if(StringUtils.isBlank(bankAccess.getId()))bankAccess.setId(IdFactory.uuid());

    	BankAccessCredentials credentials = BankAccessCredentials.cloneCredentials(bankAccess);
    	// Clean credentials
    	BankAccessCredentials.cleanCredentials(bankAccess);

		// disect credentials
        if (bankAccess.isStorePin()) {
        	store(userIDAuth, FQNUtils.credentialFQN(credentials.getAccessId()), credentials);
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
    
	public List<BankAccessEntity> getBanAccesses() {
		return load(userIDAuth, FQNUtils.bankAccessListFQN(), new TypeReference<List<BankAccessEntity>>(){});
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
    	
		DocumentFQN bankAccessListFQN = FQNUtils.bankAccessListFQN();
		List<BankAccessEntity> entities = loadInternal(bankAccessListFQN);
    	OptionalInt indexOpt = IntStream.range(0, entities.size())
   		     .filter(i -> StringUtils.equals(accessId,entities.get(i).getId()))
   		     .findFirst();
	   	if(!indexOpt.isPresent()) return false;
	   		
	   	entities.remove(indexOpt.getAsInt());
	   	store(userIDAuth, bankAccessListFQN, entities);
	   	
    	removeRemoteRegistrations(accessId);
	   	deleteDirectory(userIDAuth, FQNUtils.bankAccessDirFQN(accessId));

        return true;
    }

	public void setInvalidPin(String accessId) {
		DocumentFQN credentialsFQN = FQNUtils.credentialFQN(accessId);
		BankAccessCredentials credentials = load(userIDAuth, credentialsFQN, BankAccessCredentials.class);
		invalidate(credentials);
	}
	
	public BankAccessCredentials loadCredentials(String accessId){
		DocumentFQN credentialsFQN = FQNUtils.credentialFQN(accessId);
		return load(userIDAuth, credentialsFQN, BankAccessCredentials.class);
	}
	
	public boolean exists(String accessId){
		return documentSafeService.documentExists(userIDAuth, FQNUtils.credentialFQN(accessId));
	}

	private void invalidate(BankAccessCredentials credentials) {
		DocumentFQN credentialsFQN = FQNUtils.credentialFQN(credentials.getAccessId());
		credentials.setPinValid(false);
		credentials.setLastValidationDate(new Date());
    	store(userIDAuth, credentialsFQN, credentials);
	}
	
    /*
     * Clean bank access credential before storage. 
     * @param bankAccess
     */
	private void storeBankAccess(BankAccessEntity bankAccess) {
		BankAccessCredentials.cleanCredentials(bankAccess);

		DocumentFQN bankAccessListFQN = FQNUtils.bankAccessListFQN();
		// Store base.
		List<BankAccessEntity> entities = loadInternal(bankAccessListFQN);
    	OptionalInt indexOpt = IntStream.range(0, entities.size())
    		     .filter(i -> StringUtils.equals(bankAccess.getId(),entities.get(i).getId()))
    		     .findFirst();
    	if(indexOpt.isPresent()){
    		entities.set(indexOpt.getAsInt(), bankAccess);
    	} else {
    		entities.add(bankAccess);
    	}
    	storeDocument(userIDAuth, bankAccessListFQN, toByte(entities.toArray(new BankAccessEntity[entities.size()])));

        log.info("Bank connection [{}] created.", bankAccess.getId());
	}
	
	public Optional<BankAccessEntity> loadbankAccess(String bankAcessId){
		DocumentFQN bankAccessListFQN = FQNUtils.bankAccessListFQN();
		List<BankAccessEntity> entities = loadInternal(bankAccessListFQN);
		return entities.stream().filter(b -> StringUtils.equalsAnyIgnoreCase(bankAcessId, b.getId())).findFirst();
	}

	private List<BankAccessEntity> loadInternal(DocumentFQN bankAccessListFQN) {
		return load(userIDAuth, bankAccessListFQN, new TypeReference<List<BankAccessEntity>>(){});
	}
        
	private void removeRemoteRegistrations(String accessId) {
    	// Load bank Accounts
		List<BankAccountEntity> bankAccountEntities = bankAccountService.loadForBankAccess(accessId);
        UserEntity userEntity = userService.readUserOtThrowException();
        
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
}
