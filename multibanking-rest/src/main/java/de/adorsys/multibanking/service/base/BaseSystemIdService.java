package de.adorsys.multibanking.service.base;

import org.adorsys.docusafe.business.types.complex.UserIDAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.adorsys.multibanking.auth.SystemIDAuth;

/**
 * Service that access the system repository use this service.
 * 
 * @author fpo
 *
 */
@Service
public abstract class BaseSystemIdService extends BaseService {
	@Autowired
	private SystemIDAuth systemIDAuth;
	
	protected UserIDAuth userIDAuth(){
		return systemIDAuth.getUserIDAuth();
	}
}
