package de.adorsys.multibanking.auth;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.adorsys.docusafe.business.types.complex.DocumentFQN;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Java class to store contextual information associated with the user.
 * 
 * @author fpo
 *
 */
public class UserContextCache {
	
	private UserContext userContext;
	public UserContextCache(UserContext userContext) {
		super();
		this.userContext = userContext;
	}

	public <T> Optional<CacheEntry<T>> cacheHit(DocumentFQN documentFQN, TypeReference<T> valueType){
		if(!userContext.isCacheEnabled()) return Optional.empty();
		
		Map<DocumentFQN, CacheEntry<?>> typeCache = typeCache(valueType);
		CacheEntry<T> cacheEntry = (CacheEntry<T>) typeCache.get(documentFQN);
		return Optional.ofNullable(cacheEntry);
	}

	/**
	 * Checks if a document is in the cache.
	 * 
	 * @param documentFQN
	 * @param valueType
	 * @return
	 */
	public <T> boolean isCached(DocumentFQN documentFQN, TypeReference<T> valueType){
		if(!userContext.isCacheEnabled()) return false;
		
		if(valueType==null) return false;
		
		Map<DocumentFQN, CacheEntry<?>> typeCache = typeCache(valueType);
		CacheEntry<T> cacheEntry = (CacheEntry<T>) typeCache.get(documentFQN);
		return cacheEntry!=null;
	}
	
	/**
	 * @param documentFQN
	 * @param valueType
	 * @param entry
	 * @return true if cached. false is caching not supported.
	 */
	public <T> boolean cacheHit(DocumentFQN documentFQN, TypeReference<T> valueType, Optional<T> entry, boolean dirty){
		if(!userContext.isCacheEnabled()) return false;
		Map<DocumentFQN, CacheEntry<?>> typeCache = typeCache(valueType);
		CacheEntry<T> cacheEntry = new CacheEntry<>();
		cacheEntry.setDocFqn(documentFQN);
		cacheEntry.setEntry(entry);
		cacheEntry.setValueType(valueType);
		cacheEntry.setDirty(dirty);
		typeCache.put(documentFQN, cacheEntry);
		return true;
	}
	
	private <T> Map<DocumentFQN, CacheEntry<?>> typeCache(TypeReference<T> valueType) {
		Map<DocumentFQN, CacheEntry<?>> map = userContext.getCache().get(valueType.getType());
		if(map==null){
			map=new HashMap<>();
			userContext.getCache().put(valueType.getType(), map);
		}
		return map;
	}

}
