package de.adorsys.multibanking.service.base;

import org.adorsys.docusafe.business.types.complex.UserIDAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.adorsys.multibanking.auth.UserContext;

/**
 * Services that access the repository of the current user use this service.
 * 
 * @author fpo 2018-04-06 05:00
 *
 */
@Service
public class UserObjectService extends CacheBasedService {
	@Autowired
	private UserContext userContext;

	@Override
	public UserContext user() {
		return userContext;
	}

	@Override
	public UserIDAuth auth() {
		return userContext.getAuth();
	}
}
