package de.adorsys.multibanking.service.base;

import org.adorsys.docusafe.business.types.complex.UserIDAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.adorsys.multibanking.auth.SystemContext;
import de.adorsys.multibanking.auth.UserContext;

/**
 * Service that access the system repository use this service.
 * 
 * @author fpo
 *
 */
@Service
public abstract class BaseSystemIdService extends BaseService {
	@Autowired
	private SystemContext systemContext;

	@Override
	public UserContext user() {
		return systemContext.getUser();
	}

	@Override
	public UserIDAuth auth() {
		return systemContext.getUser().getAuth();
	}
}
