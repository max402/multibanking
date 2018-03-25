package de.adorsys.multibanking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import de.adorsys.multibanking.auth.UserContext;
import de.adorsys.onlinebanking.mock.BearerTokenAuthorizationInterceptor;
import de.adorsys.onlinebanking.mock.MockBanking;
import de.adorsys.sts.tokenauth.BearerToken;

@Service
class TokenBasedMockBanking extends MockBanking {
    @Autowired
    private UserContext userContext;

    @Override
    public RestTemplate getRestTemplate(String bankLogin, String bankCode, String pin) {
        RestTemplate restTemplate = new RestTemplate();
        BearerToken bearerToken = userContext.getBearerToken();
        if(bearerToken!=null)
        	restTemplate.getInterceptors().add(new BearerTokenAuthorizationInterceptor(bearerToken.getToken()));
        return restTemplate;
    }

}
