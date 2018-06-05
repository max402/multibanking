package de.adorsys.multibanking.auth;

import javax.servlet.http.HttpServletRequest;

import org.adorsys.docusafe.business.types.UserID;
import org.adorsys.docusafe.business.types.complex.UserIDAuth;
import org.adorsys.encobject.domain.ReadKeyPassword;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import de.adorsys.multibanking.service.base.StorageUserService;
import de.adorsys.sts.resourceserver.model.ResourceServer;
import de.adorsys.sts.resourceserver.model.ResourceServerAndSecret;
import de.adorsys.sts.tokenauth.BearerToken;
import de.adorsys.sts.tokenauth.BearerTokenValidator;

public class UserContextService {

    private BearerTokenValidator bearerTokenValidator;
    private String tokenExchangeSystemUserPassword;
    private String stsAudienceName;
    private StorageUserService storageUserService;
    
    

    public UserContextService(BearerTokenValidator bearerTokenValidator,
            String tokenExchangeSystemUserPassword, 
            String stsAudienceName, StorageUserService storageUserService) {
        super();
        this.bearerTokenValidator = bearerTokenValidator;
        this.tokenExchangeSystemUserPassword = tokenExchangeSystemUserPassword;
        this.stsAudienceName = stsAudienceName;
        this.storageUserService = storageUserService;
    }


    public UserContext getUserContext(HttpServletRequest request) {
        if(!SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) return anonymousUser();

        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        if(StringUtils.isBlank(userId) || ANONYMOUS_USER_NAME.equalsIgnoreCase(userId)) return anonymousUser();
        
        UserContext userContext = new UserContext();
        ResourceServer resourceServer = new ResourceServer();
        resourceServer.setAudience(stsAudienceName);
		ResourceServerAndSecret resourceServerAndSecret = ResourceServerAndSecret.builder().resourceServer(resourceServer).build();
		loadUserCredentials(resourceServerAndSecret, userId);
        UserIDAuth userIDAuth = new UserIDAuth(new UserID(userId), new ReadKeyPassword(resourceServerAndSecret.getRawSecret()));
        userContext.setAuth(userIDAuth);

        String token = request.getHeader(BearerTokenValidator.HEADER_KEY);
        BearerToken bearerToken = bearerTokenValidator.extract(token);
        userContext.setBearerToken(bearerToken);

        if (StringUtils.isNotBlank(resourceServerAndSecret.getRawSecret()) && !storageUserService.userExists(userContext.getAuth().getUserID())) {
            storageUserService.createUser(userContext.getAuth());
        }

        return userContext;
    }
    
    public static final String ANONYMOUS_USER_NAME = "anonymousUser";
    private static UserContext anonymousUser(){
    	UserContext userContext = new UserContext();
        UserIDAuth userIDAuth = new UserIDAuth(new UserID(ANONYMOUS_USER_NAME), null);
        userContext.setAuth(userIDAuth);
        return userContext;
    }


	private void loadUserCredentials(ResourceServerAndSecret resourceServer, String user){
		resourceServer.setRawSecret(tokenExchangeSystemUserPassword);
		
//		if(userDataService==null) return;
//		boolean store = false;
//		userDataService.addAccount(user, tokenExchangeSystemUserPassword);
//		UserCredentials userCredentials = userDataService.loadUserCredentials(user, tokenExchangeSystemUserPassword);
//		if(userCredentials ==null){
//			userCredentials = new UserCredentials();
//			store = true;
//		}
//		
//		String credentialForResourceServer = userCredentials.getCredentialForResourceServer(resourceServer.getResourceServer().getAudience());
//		if(credentialForResourceServer==null){
//			// create one
//			credentialForResourceServer = RandomStringUtils.randomGraph(16);
//			userCredentials.setCredentialForResourceServer(resourceServer.getResourceServer().getAudience(), credentialForResourceServer);
//			store = true;
//		}
//		resourceServer.setRawSecret(credentialForResourceServer);
//
//		if(store){
//			userDataService.storeUserCredentials(user, tokenExchangeSystemUserPassword, userCredentials);
//		}
	}
}
