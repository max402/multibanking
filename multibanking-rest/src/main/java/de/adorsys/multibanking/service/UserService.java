package de.adorsys.multibanking.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import org.adorsys.cryptoutils.exceptions.BaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;

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
		return load(FQNUtils.userFQN(), valueType())
				.orElseThrow(() -> resourceNotFound(UserEntity.class, auth().getUserID().getValue()));
	}

    /**
     * Returns the user entity or create one if the user does not exist.
     */
    public UserEntity createUser(Date expire) {
    	storageUserService.createUser(auth());
    	UserEntity userEntity = new UserEntity();
    	userEntity.setApiUser(new ArrayList<>());
    	userEntity.setId(auth().getUserID().getValue());
    	userEntity.setExpireUser(expire);
    	store(userEntity);
    	return userEntity;
    }

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
        	if(!storageUserService.userExists(auth().getUserID())) 
        		throw new BaseException("Storage user with id: "+ auth().getUserID().getValue() + " non existent ");
        	
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
		store(FQNUtils.userFQN(), valueType(), userEntity);		
	}
	
	private static TypeReference<UserEntity> valueType(){
		return new TypeReference<UserEntity>() {};
	}
	
}
