package de.adorsys.multibanking.service;

import org.adorsys.docusafe.business.types.complex.DSDocument;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;

import de.adorsys.multibanking.domain.UserData;
import de.adorsys.multibanking.service.base.BaseUserIdService;
import de.adorsys.multibanking.utils.FQNUtils;

/**
 * Manage Access to the user data. Manages all state information for this user account.
 * 
 * Consumer shall read this once and have all information need to initialize a user interface.
 * 
 * @author fpo 2018-03-17 08:38
 *
 */
@Service
public class UserDataService extends BaseUserIdService {

	public UserData load(){
		return load(FQNUtils.userDataFQN(), valueType())
				.orElseThrow(() -> resourceNotFound(UserData.class, auth().getUserID().getValue()));
	}
	
	public boolean exists(){
		return documentExists(FQNUtils.userDataFQN(), valueType());
	}

	public void store(UserData userData){
		store(FQNUtils.userDataFQN(), valueType(), userData);		
	}
	
    public DSDocument loadDocument() {
    	return loadDocument(FQNUtils.userDataFQN());
    }
	
	private static TypeReference<UserData> valueType(){
		return new TypeReference<UserData>() {};
	}
}
