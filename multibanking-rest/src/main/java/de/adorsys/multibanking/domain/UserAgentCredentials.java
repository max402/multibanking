package de.adorsys.multibanking.domain;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

/**
 * Stores credentials used by a user agent to secure storage of data on a 
 * user agent. Concrete example can be an AES key used by the user agent 
 * to encrypt data cached on the user device.
 * 
 * @author fpo
 *
 */
@Data
public class UserAgentCredentials {
	
	private Map<String, String> credentials = new HashMap<>();

}
