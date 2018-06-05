package de.adorsys.multibanking.usercontext;

import javax.servlet.http.HttpServletRequest;

import de.adorsys.multibanking.auth.UserObjectPersistenceService;

/**
 * {@link CacheInRequestContext} maintains the cache in the context of a request.
 * 
 * It is totally stateles and operates in the context of the defined request.
 * 
 * @author fpo
 *
 */
public class CacheInRequestContext {
    private static String USER_CONTEXT_CACHE = UserObjectPersistenceService.class.getName();
    
    // The target request
    private final HttpServletRequest request;
    
    public CacheInRequestContext(HttpServletRequest request) {
        this.request = request;
    }

    /**
     * Activate first level caching for this request.
     * 
     * This is done by 
     * 
     * @param userContext
     */
    public void activateCache(UserObjectPersistenceService cache){
        request.setAttribute(USER_CONTEXT_CACHE, cache);
    }
    

    public void flushCaches() {
        UserObjectPersistenceService cache = (UserObjectPersistenceService) request.getAttribute(USER_CONTEXT_CACHE);
        if(cache!=null)cache.flush();
        request.removeAttribute(USER_CONTEXT_CACHE);
    }
}
