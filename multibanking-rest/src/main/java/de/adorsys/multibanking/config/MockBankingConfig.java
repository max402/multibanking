package de.adorsys.multibanking.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.adorsys.multibanking.auth.UserContext;
import de.adorsys.multibanking.service.interceptor.TokenBasedMockBanking;
import de.adorsys.onlinebanking.mock.MockBanking;

@Configuration
public class MockBankingConfig {
	
	@Autowired
	private UserContext userContext;
	
	private MockBanking mockBanking;
	
	@PostConstruct
	public void postConstruct(){
		mockBanking = new TokenBasedMockBanking(userContext);
	}

	@Bean
	public MockBanking mockBanking(){
		return mockBanking;
	}
}
