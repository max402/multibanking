package de.adorsys.multibanking.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import org.adorsys.cryptoutils.exceptions.BaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.adorsys.multibanking.domain.UserEntity;
import de.adorsys.multibanking.service.base.BaseUserIdService;
import de.adorsys.multibanking.service.base.StorageUserService;
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
public class UserService extends BaseUserIdService {

    @Autowired
    private OnlineBankingServiceProducer bankingServiceProducer;
    @Autowired
    private StorageUserService storageUserService;
    @Autowired
    DeleteExpiredUsersService deleteExpiredUsersService;

	/**
	 * Read the user or throw an illegal state exception if the user does not exist. This
	 * call only be called from a process that assumes pre-existence of the user.
	 * @return
	 */
	public UserEntity readUser(){
		return load(userIDAuth, FQNUtils.userFQN(), UserEntity.class)
				.orElseThrow(() -> resourceNotFound(UserEntity.class, userIDAuth.getUserID().getValue()));
	}

    /**
     * Returns the user entity or create one if the user does not exist.
     */
    public UserEntity createUser(Date expire) {
    	storageUserService.createUser(userIDAuth);
    	UserEntity userEntity = new UserEntity();
    	userEntity.setApiUser(new ArrayList<>());
    	userEntity.setId(userIDAuth.getUserID().getValue());
    	userEntity.setExpireUser(expire);
    	store(userEntity);
    	return userEntity;
    }
//
//	public void saveUser(UserEntity userEntity) {
//		userEntity.setId(userIDAuth.getUserID().getValue());
//		store(userEntity);
//	}
//
//	/**
//	 * Delete the user directory, removing all files associeated with this user.
//	 */
//	public void deleteUser() {
//		deleteUser(userIDAuth);
//	}
//	
//	/**
//	 * Retrieves an encryption public key for this user. Key will be user to send sensitive informations
//	 * to this application. Like the banking PIN.
//	 * 
//	 * @return
//	 */
//	public JWK findPublicEncryptionKey(){
//		return documentSafeService.findPublicEncryptionKey(userIDAuth.getUserID());
//	}

//	private Optional<UserEntity> load() {
//		return load(userIDAuth, FQNUtils.userFQN(), UserEntity.class);
//	}
	

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
        	if(!storageUserService.userExists(userIDAuth.getUserID())) 
        		throw new BaseException("Storage user with id: "+ userIDAuth.getUserID().getValue() + " non existent ");
        	
            UserEntity userEntity = readUser();

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
	
	private void store(UserEntity userEntity){
		if(userEntity.getExpireUser()!=null)
			deleteExpiredUsersService.scheduleExpiry(userEntity);
		store(userIDAuth, FQNUtils.userFQN(), userEntity);		
	}
}
