package de.adorsys.multibanking.service.interceptor;

import org.springframework.stereotype.Service;

import de.adorsys.multibanking.service.base.BaseUserIdService;

@Service
public class UserCacheService extends BaseUserIdService {
	public void preHandle(){
		enableCaching();
	}
	
	public void postHandle(){
		flush();
	}
}
