package de.adorsys.multibanking.service.interceptor;

import org.springframework.stereotype.Service;

import de.adorsys.multibanking.service.base.BaseSystemIdService;

@Service
public class SystemCacheService extends BaseSystemIdService {
	public void preHandle(){
		enableCaching();
	}
	
	public void postHandle(){
		flush();
	}
}
