package de.adorsys.multibanking.service;

import de.adorsys.onlinebanking.mock.BearerTokenAuthorizationInterceptor;
import de.adorsys.onlinebanking.mock.MockBanking;
import de.adorsys.sts.tokenauth.BearerToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
class TokenBasedMockBanking extends MockBanking {
    @Autowired
    private BearerToken bearerToken;

    @Override
    public RestTemplate getRestTemplate(String bankLogin, String bankCode, String pin) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new BearerTokenAuthorizationInterceptor(bearerToken.getToken()));
        return restTemplate;
    }

}
