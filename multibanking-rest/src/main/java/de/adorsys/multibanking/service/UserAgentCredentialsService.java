package de.adorsys.multibanking.service;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.jwk.JWK;

import de.adorsys.multibanking.domain.UserAgentCredentials;
import de.adorsys.multibanking.domain.UserData;
import de.adorsys.multibanking.service.base.BaseUserIdService;
import de.adorsys.multibanking.service.crypto.KeyGen;
import de.adorsys.multibanking.utils.FQNUtils;
import de.adorsys.multibanking.utils.Ids;

/**
 * Provide Access to the user Agent credential record.
 * 
 * Each user agent can use a UUID to store some data on the server.
 * 
 * Local user agent keys are used to encrypt data before local storage on the user agent.
 * 
 * @author fpo 2018-04-04 06:49
 *
 */
@Service
public class UserAgentCredentialsService extends BaseUserIdService {

	public UserAgentCredentials load(String userAgentId){
		return load(FQNUtils.userAgentCredentialFQN(userAgentId), valueType())
				.orElseThrow(() -> resourceNotFound(UserData.class, auth().getUserID().getValue()));
	}
	
	public boolean exists(String userAgentId){
		return documentExists(FQNUtils.userAgentCredentialFQN(userAgentId), valueType());
	}

	public void store(UserAgentCredentials userAgentCredentials){
		store(FQNUtils.userAgentCredentialFQN(userAgentCredentials.getUserAgentId()), valueType(), userAgentCredentials);		
	}

	public JWK newAESKey(String keyId, EncryptionMethod encryptionMethod){
		return KeyGen.newAESKey(keyId, encryptionMethod);
	}

	/**
	 * Generate a UUID for use by the client. This can be used by clients who do not
	 * have the capability of generating uid.
	 * 
	 * @return
	 */
	public String newUUID(){
		return Ids.uuid();
	}
	
	private static TypeReference<UserAgentCredentials> valueType(){
		return new TypeReference<UserAgentCredentials>() {};
	}
}
