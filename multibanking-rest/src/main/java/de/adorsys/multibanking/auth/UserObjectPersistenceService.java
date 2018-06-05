package de.adorsys.multibanking.auth;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import org.adorsys.cryptoutils.exceptions.BaseException;
import org.adorsys.docusafe.business.DocumentSafeService;
import org.adorsys.docusafe.business.types.complex.DSDocument;
import org.adorsys.docusafe.business.types.complex.DocumentDirectoryFQN;
import org.adorsys.docusafe.business.types.complex.DocumentFQN;
import org.adorsys.docusafe.service.types.DocumentContent;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.adorsys.multibanking.exception.ResourceNotFoundException;

/**
 * Java class to store contextual information associated with the user.
 * 
 * @author fpo
 *
 */
public class UserObjectPersistenceService {
    private final static Logger LOGGER = LoggerFactory.getLogger(UserObjectPersistenceService.class);
	
	private UserContext userContext;
    private ObjectMapper objectMapper;
    private DocumentSafeService documentSafeService;
	public UserObjectPersistenceService(UserContext userContext, ObjectMapper objectMapper, DocumentSafeService documentSafeService) {
        this.userContext = userContext;
        this.objectMapper = objectMapper;
        this.documentSafeService = documentSafeService;
    }

    /**
	 * Check for the existence of an entry in the cache. Return empty if object not 
	 * object not in cache.
	 * 
	 * @param documentFQN
	 * @param valueType
	 * @return
	 */
	public <T> Optional<CacheEntry<T>> cacheHit(DocumentFQN documentFQN, TypeReference<T> valueType){
	    if(valueType==null) return Optional.empty();
		Map<DocumentFQN, CacheEntry<?>> typeCache = typeCache(valueType);
		@SuppressWarnings("unchecked")
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
		return entry(documentFQN, valueType).isPresent();
	}

	public <T> boolean isDirty(DocumentFQN documentFQN, TypeReference<T> valueType) {
	    // always return true for non cached object.
        if(valueType==null) return true;
		Optional<CacheEntry<T>> entry = entry(documentFQN, valueType);
		if(entry.isPresent()) return entry.get().isDirty();
		
		// return true
		return true;
	}

	private <T> Optional<CacheEntry<T>> entry(DocumentFQN documentFQN, TypeReference<T> valueType){
		if(valueType==null) return Optional.empty();
		Map<DocumentFQN, CacheEntry<?>> typeCache = typeCache(valueType);
		return Optional.ofNullable((CacheEntry<T>) typeCache.get(documentFQN));
	}

    public <T> Optional<CacheEntry<T>> remove(DocumentFQN documentFQN, TypeReference<T> valueType){
        if(valueType==null) return Optional.empty();
        Map<DocumentFQN, CacheEntry<?>> typeCache = typeCache(valueType);
        if(typeCache.containsKey(documentFQN)){
            return Optional.ofNullable((CacheEntry<T>)typeCache.remove(documentFQN));
        }
        return Optional.empty();
    }
	
	/**
	 * @param documentFQN
	 * @param valueType
	 * @param entry
	 * @return true if cached. false is caching not supported.
	 */
	public <T> boolean cacheHit(DocumentFQN documentFQN, TypeReference<T> valueType, Optional<T> entry, boolean dirty){
	    if(valueType==null) return false;
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
        if(valueType==null) return null;
		Map<DocumentFQN, CacheEntry<?>> map = userContext.getCache().get(valueType.getType());
		if(map==null){
			map=new HashMap<>();
			userContext.getCache().put(valueType.getType(), map);
		}
		return map;
	}

	public void clearCached(DocumentDirectoryFQN dir) {
        LOGGER.debug("Clearing cache for " + dir);
		Map<Type, Map<DocumentFQN, CacheEntry<?>>> cache = userContext.getCache();
		Collection<Map<DocumentFQN,CacheEntry<?>>> values = cache.values();
		String path = dir.getValue();
		for (Map<DocumentFQN, CacheEntry<?>> map : values) {
			Set<Entry<DocumentFQN,CacheEntry<?>>> entrySet = map.entrySet();
			Set<DocumentFQN> keyToRemove = new HashSet<>();
			for (Entry<DocumentFQN, CacheEntry<?>> entry : entrySet) {
				DocumentFQN documentFQN = entry.getKey();
				if(StringUtils.startsWith(documentFQN.getValue(), path)){
					if(entry.getValue()==null || !entry.getValue().isDirty())
						keyToRemove.add(documentFQN);
				}
			}
			for (DocumentFQN documentFQN : keyToRemove) {
		        LOGGER.debug("Removing from cache Cache " + documentFQN);
				map.remove(documentFQN);
			}
		}
	}
	
    /**
     * Load file from location documentFQN and parse using valueType. Check and
     * return from cache is available. Caches result it not yet done.
     *
     * @param documentFQN
     * @param valueType
     * @return
     */
    public <T> Optional<T> load(DocumentFQN documentFQN, TypeReference<T> valueType) {
        if(valueType==null)throw new BaseException("Method does not accept null value type.");
        LOGGER.debug("load: " + documentFQN);
        // Log request count
        userContext.getRequestCounter().load(documentFQN);

        // Check cache.
        Optional<CacheEntry<T>> cacheHit = cacheHit(documentFQN, valueType);
        if (cacheHit.isPresent()) {
            LOGGER.debug("loaded from cache: " + documentFQN);
            userContext.getRequestCounter().cacheHit(documentFQN);
            return cacheHit.get().getEntry();
        }

        // Return empty if base document does not exist.
        if (!documentSafeService.documentExists(userContext.getAuth(), documentFQN)){
            LOGGER.debug("load, doc not found: " + documentFQN);
            return Optional.empty();
        }

        try {
            LOGGER.debug("loading from file: " + documentFQN);
            Optional<T> ot = Optional.of(objectMapper.readValue(documentSafeService.readDocument(userContext.getAuth(), documentFQN).getDocumentContent().getValue(), valueType));

            // Cache document.
            cacheHit(documentFQN, valueType, ot, false);
            return ot;
        } catch (IOException e) {
            throw new BaseException(e);
        }
    }	
    
    private void deleteDirectory(DocumentDirectoryFQN dirFQN) {
        // First remove all cached object from this dir.
        clearCached(dirFQN);
        documentSafeService.deleteFolder(userContext.getAuth(), dirFQN);
    }
    
    /**
     * Check existence of a document in the storage. Uses the valueType to locate
     * and check existence of a cached version.
     *
     * @param documentFQN
     * @param valueType
     * @return
     */
    public <T> boolean documentExists(DocumentFQN documentFQN, TypeReference<T> valueType) {
        if (isCached(documentFQN, valueType))return true;
        return documentSafeService.documentExists(userContext.getAuth(), documentFQN);
    }
    
    public <T> boolean deleteDocument(DocumentFQN documentFQN, TypeReference<T> valueType) {
        LOGGER.debug("deleteDocument " + documentFQN);
        
        // Remove from cache
        Optional<CacheEntry<T>> removed = remove(documentFQN, valueType);
        boolean docExist=false;
        try {
            docExist = documentSafeService.documentExists(userContext.getAuth(), documentFQN);
        } catch (BaseException b){
            LOGGER.warn("error checking existence of Document " + documentFQN);
            // No Action. might nit have been flushed yet.
        }
        if(docExist){
            documentSafeService.deleteDocument(userContext.getAuth(), documentFQN);
            return true;
        }
        return removed!=null;
    }

    /**
     * Store the file in cache. If cache not supported, flush document.
     *
     * @param documentFQN
     * @param valueType
     * @param entity
     */
    public <T> void store(DocumentFQN documentFQN, TypeReference<T> valueType, T entity) {
        LOGGER.debug("store: " + documentFQN);
        userContext.getRequestCounter().store(documentFQN);
        boolean cacheHit = cacheHit(documentFQN, valueType, Optional.ofNullable(entity), true);
        if (!cacheHit) {
            LOGGER.debug("flush im store " + documentFQN);
            flush(documentFQN, entity);
        } else {
            LOGGER.debug("No flush, will store on cache flush " + documentFQN);
        }
    }

    public ResourceNotFoundException resourceNotFound(Class<?> klass, String id) {
        return new ResourceNotFoundException(klass, id);
    }

    protected <T> void flush(DocumentFQN documentFQN, T entity) {
        LOGGER.debug("flushing: " + documentFQN);

        userContext.getRequestCounter().flush(documentFQN);
        DSDocument dsDocument = toDSDocument(documentFQN, entity);
        documentSafeService.storeDocument(userContext.getAuth(), dsDocument);
    }
    
    private <T> DSDocument toDSDocument(DocumentFQN documentFQN, T entity) {
        DocumentContent documentContent;
        try {
            documentContent = new DocumentContent(objectMapper.writeValueAsBytes(entity));
        } catch (JsonProcessingException e) {
            throw new BaseException(e);
        }
        return new DSDocument(documentFQN, documentContent, null);
    }

    public void flush() {
        Collection<Map<DocumentFQN, CacheEntry<?>>> values = userContext.getCache().values();
        LOGGER.debug("Flushing cache: " + userContext.getAuth().getUserID() + " Objects in cache: " + values.size());
        for (Map<DocumentFQN, CacheEntry<?>> map : values) {
            Collection<CacheEntry<?>> collection = map.values();
            for (CacheEntry<?> cacheEntry : collection) {
                LOGGER.debug("Cache entry pre flush: " + cacheEntry.getDocFqn());
                if (cacheEntry.isDirty()) {
                    cacheEntry.setDirty(false);
                    LOGGER.debug("Cache entry pre flush : dirty: " + cacheEntry.getDocFqn());
                    if (cacheEntry.getEntry().isPresent()) {
                        LOGGER.debug("Cache entry pre flush : present: " + cacheEntry.getDocFqn());
                        flush(cacheEntry.getDocFqn(), cacheEntry.getEntry().get());
                    } else {
                        LOGGER.debug("Cache entry pre flush : absent. File will be deleted: " + cacheEntry.getDocFqn());
                        documentSafeService.deleteDocument(userContext.getAuth(), cacheEntry.getDocFqn());
                    }
                } else {
                    LOGGER.debug("Cache entry pre flush : clean. No file write : " + cacheEntry.getDocFqn());
                }
            }
        }
        
        Set<DocumentDirectoryFQN> deletedDirCache = userContext.getDeletedDirCache();
        for (DocumentDirectoryFQN documentDirectoryFQN : deletedDirCache) {
            try {
                deleteDirectory(documentDirectoryFQN);
            } catch(Exception e){
                // log Exception
                LOGGER.warn("Error trying to delete dir with name : " + documentDirectoryFQN.getValue(), e);
            }
        }
        LOGGER.debug("Flushed cache: " + userContext.getAuth().getUserID());
    }

    public String userId() {
        return userContext.getAuth().getUserID().getValue();
    }


    public void markDirForDeletion(DocumentDirectoryFQN dirFQN) {
        userContext.getDeletedDirCache().add(dirFQN);
    }   

    public <T> DSDocument readDocument(DocumentFQN docFQN, TypeReference<T> valueType) {
        Optional<CacheEntry<T>> entry = entry(docFQN, valueType);
        if (entry.isPresent()){
            CacheEntry<T> cacheEntry = entry.get();
            Optional<T> entry2 = cacheEntry.getEntry();
            if(entry2.isPresent()){
                return toDSDocument(docFQN, entry2.get());
            }
        } else if(documentSafeService.documentExists(userContext.getAuth(), docFQN)){
            return documentSafeService.readDocument(userContext.getAuth(), docFQN);
        }
        throw new ResourceNotFoundException(valueType.getType().getTypeName(), docFQN.getValue());
    }

//    public void storeDocument(DSDocument dsDocument) {
//        documentSafeService.storeDocument(userContext.getAuth(), dsDocument);
//    }    
}
