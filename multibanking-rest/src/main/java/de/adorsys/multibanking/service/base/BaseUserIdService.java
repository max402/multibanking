package de.adorsys.multibanking.service.base;

import org.adorsys.docusafe.business.types.complex.UserIDAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Services that access the repository of the current user use this service.
 * 
 * @author fpo
 *
 */
@Service
public abstract class BaseUserIdService extends BaseService {
	@Autowired
	protected UserIDAuth userIDAuth;	
}
