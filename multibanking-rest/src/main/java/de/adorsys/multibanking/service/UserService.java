package de.adorsys.multibanking.service;

import java.util.ArrayList;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.jwk.JWK;

import de.adorsys.multibanking.domain.UserEntity;
import de.adorsys.multibanking.exception.ResourceNotFoundException;
import de.adorsys.multibanking.service.base.BaseService;
import de.adorsys.multibanking.utils.FQNUtils;
import domain.BankApi;
import domain.BankApiUser;
import spi.OnlineBankingService;

/**
 * Manage Access to the user descriptive record.
 * 
 * @author fpo 2018-03-17 08:38
 *
 */
@Service
public class UserService extends BaseService {

    @Autowired
    private OnlineBankingServiceProducer bankingServiceProducer;

    /**
     * Returns the bank API user. Registers with the banking API if necessary.
     * 
     * User must have been create before.
     * 
     * @param bankApi
     * @param bankCode
     * @return
     */
    public BankApiUser checkApiRegistration(BankApi bankApi, String bankCode) {
        OnlineBankingService onlineBankingService = bankApi != null
                ? bankingServiceProducer.getBankingService(bankApi)
                : bankingServiceProducer.getBankingService(bankCode);

        if (onlineBankingService.userRegistrationRequired()) {
        	if(!userExists()) throw new ResourceNotFoundException(UserEntity.class, userIDAuth.getUserID().getValue());
        	
            UserEntity userEntity = load();

            return userEntity.getApiUser()
                    .stream()
                    .filter(bankApiUser -> bankApiUser.getBankApi() == onlineBankingService.bankApi())
                    .findFirst()
                    .orElseGet(() -> {
                        BankApiUser bankApiUser = onlineBankingService.registerUser(UUID.randomUUID().toString());
                        userEntity.getApiUser().add(bankApiUser);
                        store(userEntity);

                        return bankApiUser;
                    });
        } else {
            BankApiUser bankApiUser = new BankApiUser();
            bankApiUser.setBankApi(onlineBankingService.bankApi());
            return bankApiUser;
        }
    }

	public boolean userExists() {
		return documentSafeService.userExists(userIDAuth.getUserID());
	}
	
	/**
	 * Read the user or throw an illegal state exception if the user does not exist. This
	 * call only be called from a process that assumes preexistence of the user.
	 * @return
	 */
	public UserEntity readUserOtThrowException(){
		UserEntity entity = load();
		if(entity!=null) return entity;
		throw new IllegalStateException("Entity record is not supposed to be missing.");
	}

    /**
     * Returns the user entity or create one if the user does not exist.
     */
    public UserEntity readOrCreateUser() {
        if (!userExists()) {
			documentSafeService.createUser(userIDAuth);

			UserEntity userEntity = new UserEntity();
            userEntity.setApiUser(new ArrayList<>());
            userEntity.setId(userIDAuth.getUserID().getValue());
            store(userEntity);
            return userEntity;
        } else {
        	return load();
        }
    }

	public void saveUser(UserEntity userEntity) {
		userEntity.setId(userIDAuth.getUserID().getValue());
		store(userEntity);
	}

	/**
	 * Delete the user directory, removing all files associeated with this user.
	 */
	public void deleteUser() {
		documentSafeService.destroyUser(userIDAuth);
	}
	
	/**
	 * Retrieves an encryption public key for this user. Key will be user to send sensitive informations
	 * to this application. Like the banking PIN.
	 * 
	 * @return
	 */
	public JWK findPublicEncryptionKey(){
		return documentSafeService.findPublicEncryptionKey(userIDAuth.getUserID());
	}

	private UserEntity load() {
		return load(userIDAuth, FQNUtils.userFQN(), UserEntity.class);
	}
	private void store(UserEntity userEntity){
		store(userIDAuth, FQNUtils.userFQN(), userEntity);		
	}
}
