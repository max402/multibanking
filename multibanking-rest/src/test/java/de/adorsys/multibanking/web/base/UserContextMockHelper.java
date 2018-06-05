package de.adorsys.multibanking.web.base;

import javax.servlet.http.HttpServletRequest;

import org.adorsys.cryptoutils.exceptions.BaseException;
import org.adorsys.docusafe.business.types.UserID;
import org.adorsys.docusafe.business.types.complex.UserIDAuth;
import org.adorsys.encobject.domain.ReadKeyPassword;
import org.mockito.BDDMockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import de.adorsys.multibanking.auth.RequestMock;
import de.adorsys.multibanking.auth.UserContext;
import de.adorsys.multibanking.auth.UserContextService;
import de.adorsys.sts.token.authentication.TokenAuthenticationService;

public class UserContextMockHelper {

    public static void mockUserContext(String token, UserContext userContextMock, TokenAuthenticationService tokenAuthenticationService,
            UserContextService userContextService) {
        HttpServletRequest request = new RequestMock("Bearer " + token);
        Authentication authentication = tokenAuthenticationService.getAuthentication(request);
        if(authentication==null || !authentication.isAuthenticated()){
            throw new BaseException("Can not set authentication. ");
        }
        SecurityContextHolder.getContext().setAuthentication(tokenAuthenticationService.getAuthentication(request));
        UserContext userContext = userContextService.getUserContext(request);
        UserIDAuth auth = new UserIDAuth(new UserID(userContext.getAuth().getUserID().getValue()),
                new ReadKeyPassword(userContext.getAuth().getReadKeyPassword().getValue()));
        BDDMockito.when(userContextMock.getAuth()).thenReturn(auth);
        BDDMockito.when(userContextMock.getRequestCounter()).thenReturn(userContext.getRequestCounter());
        BDDMockito.when(userContextMock.getCache()).thenReturn(userContext.getCache());
    }
}