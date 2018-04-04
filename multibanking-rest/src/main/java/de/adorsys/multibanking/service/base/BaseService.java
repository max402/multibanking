package de.adorsys.multibanking.service.base;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.adorsys.cryptoutils.exceptions.BaseException;
import org.adorsys.docusafe.business.DocumentSafeService;
import org.adorsys.docusafe.business.types.complex.DSDocument;
import org.adorsys.docusafe.business.types.complex.DocumentDirectoryFQN;
import org.adorsys.docusafe.business.types.complex.DocumentFQN;
import org.adorsys.docusafe.business.types.complex.UserIDAuth;
import org.adorsys.docusafe.service.types.DocumentContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.adorsys.multibanking.auth.CacheEntry;
import de.adorsys.multibanking.auth.UserContext;
import de.adorsys.multibanking.auth.UserContextCache;
import de.adorsys.multibanking.domain.common.IdentityIf;
import de.adorsys.multibanking.exception.ResourceNotFoundException;
import de.adorsys.multibanking.utils.Ids;

@Service
public abstract class BaseService {
	private final static Logger LOGGER = LoggerFactory.getLogger(BaseService.class);

	@Autowired
	private DocumentSafeService documentSafeService;

	@Autowired
	protected ObjectMapper objectMapper;

	protected abstract UserContext user();

	protected abstract UserIDAuth auth();

	private UserContextCache userContextCache;

	@PostConstruct
	public void postConstruct() {
		userContextCache = new UserContextCache(user());
	}

	protected UserContextCache userContextCache() {
		return userContextCache;
	}

	/**
	 * Load file from location documentFQN and parse using valueType. Check and
	 * return from cache is available. Caches result it not yet done.
	 *
	 * @param documentFQN
	 * @param valueType
	 * @return
	 */
	protected <T> Optional<T> load(DocumentFQN documentFQN, TypeReference<T> valueType) {
		user().getRequestCounter().load(documentFQN);
		Optional<CacheEntry<T>> cacheHit = userContextCache().cacheHit(documentFQN, valueType);
		if (cacheHit.isPresent()) {
			user().getRequestCounter().cacheHit(documentFQN);
			return cacheHit.get().getEntry();
		}

		Optional<T> ot = Optional.ofNullable(loadFunction(documentFQN, valueType));
		userContextCache().cacheHit(documentFQN, valueType, ot, false);
		return ot;
	}

	protected <T> boolean documentExists(DocumentFQN documentFQN, TypeReference<T> valueType) {
		// Check existence in user context cache.
		user().getRequestCounter().load(documentFQN);

		if (userContextCache().isCached(documentFQN, valueType))
			return true;

		return documentSafeService.documentExists(auth(), documentFQN);
	}

	/**
	 * Store the file in cache. If cache not supported, flush document.
	 *
	 * @param documentFQN
	 * @param valueType
	 * @param entity
	 */
	protected <T> void store(DocumentFQN documentFQN, TypeReference<T> valueType, T entity) {
		user().getRequestCounter().store(documentFQN);
		boolean cacheHit = userContextCache().cacheHit(documentFQN, valueType, Optional.ofNullable(entity), true);
		if (!cacheHit)
			flush(documentFQN, entity);
	}

	/**
	 * Store an instance of this object in the list.
	 *
	 * @param t
	 * @return false if object was found and updated, false if object was
	 *         created.
	 */
	protected <T extends IdentityIf> boolean addToList(T t, TypeReference<List<T>> listType,
			DocumentFQN documentListFQN) {
		List<T> list = load(documentListFQN, listType).orElse(Collections.emptyList());
		Optional<T> persistent = list.stream().filter(p -> Ids.eq(p.getId(), t.getId())).findFirst();
		boolean created = true;
		if (persistent.isPresent()) {
			// Object exists. Override properties
			BeanUtils.copyProperties(t, persistent);
			// Object is already in the list.
			created = false;
		} else {
			// Object is not yet in the list.
			list.add(t);
		}
		store(documentListFQN, listType, list);
		LOGGER.info("Object [{}] added to list.", t.getId());

		return created;
	}

	/**
	 * Find and return the object with id from klass fron document from user.
	 *
	 * @param id
	 * @param klass
	 * @param documentListFQN
	 * @param userIDAuth
	 * @return
	 */
	protected <T extends IdentityIf> Optional<T> find(String id, Class<T> klass, TypeReference<List<T>> listType,
			DocumentFQN documentListFQN) {
		Optional<List<T>> listResult = load(documentListFQN, listType);

		if (!listResult.isPresent())
			return Optional.empty();
		List<T> list = listResult.get();
		return list.stream().filter(t -> Ids.eq(id, t.getId())).findFirst();
	}

	/**
	 * Apply the function fnct on object with id-> id from class -> klass at
	 * location -> documentListFQN of user -> user
	 *
	 * @param fnct
	 * @param id
	 * @param klass
	 * @param documentListFQN
	 * @param user
	 * @return
	 */
	protected <T extends IdentityIf, R> R apply(Function<T, R> fnct, String id, Class<T> klass,
			TypeReference<List<T>> listType, DocumentFQN documentListFQN) {
		List<T> list = load(documentListFQN, listType).orElse(Collections.emptyList());
		// Load
		T t = list.stream().filter(p -> Ids.eq(id, p.getId())).findFirst()
				.orElseThrow(() -> resourceNotFound(klass, id));

		R r = fnct.apply(t);// Apply the function.

		store(documentListFQN, listType, list);// Store
		return r;
	}

	protected <T extends IdentityIf> void replaceList(List<T> inputList, Class<T> klass,
			TypeReference<List<T>> listType, DocumentFQN documentListFQN) {
		List<T> persList = Collections.emptyList();
		// Add new
		inputList.stream().forEach(n -> {
			Ids.id(n);
			persList.add(n);
		});
		store(documentListFQN, listType, persList);// Store
	}

	protected <T extends IdentityIf> void updateList(List<T> inputList, Class<T> klass, TypeReference<List<T>> listType,
			DocumentFQN documentListFQN) {
		List<T> persList = load(documentListFQN, listType).orElse(Collections.emptyList());
		// Existing elements.
		List<? extends IdentityIf> foundElements = inputList.stream().filter(i -> persList.contains(i))
				.collect(Collectors.toList());
		ArrayList<T> newList = new ArrayList<>(inputList);
		newList.removeAll(foundElements);

		// Override existing.
		foundElements.stream().forEach(e -> {
			int indexOf = persList.indexOf(e);
			IdentityIf pers = persList.get(indexOf);
			BeanUtils.copyProperties(e, pers);
		});

		// Add new
		List<T> finalList = new ArrayList<>(persList);
		newList.stream().forEach(n -> {
			Ids.id(n);
			finalList.add(n);
		});

		store(documentListFQN, listType, finalList);// Store
	}

	protected <T extends IdentityIf> List<T> updateList(List<T> inputList, List<T> persList) {
		// Existing elements.
		List<? extends IdentityIf> foundElements = inputList.stream().filter(i -> persList.contains(i))
				.collect(Collectors.toList());
		ArrayList<T> newList = new ArrayList<>(inputList);
		newList.removeAll(foundElements);

		// Override existing.
		foundElements.stream().forEach(e -> {
			int indexOf = persList.indexOf(e);
			IdentityIf pers = persList.get(indexOf);
			BeanUtils.copyProperties(e, pers);
		});

		// Add new
		List<T> finalList = new ArrayList<>(persList);
		newList.stream().forEach(n -> {
			Ids.id(n);
			finalList.add(n);
		});

		return finalList;// Store
	}

	protected <T extends IdentityIf> void deleteList(List<T> inputList, Class<T> klass, TypeReference<List<T>> listType,
			DocumentFQN documentListFQN) {
		List<T> persList = load(documentListFQN, listType).orElse(Collections.emptyList());
		persList.removeAll(inputList);
		store(documentListFQN, listType, persList);// Store
	}

	/**
	 * Returns the number of record deleted.
	 *
	 * @param inputIdList
	 * @param klass
	 * @param documentListFQN
	 * @param user
	 * @return
	 */
	protected <T extends IdentityIf> int deleteListById(List<String> inputIdList, Class<T> klass,
			TypeReference<List<T>> listType, DocumentFQN documentListFQN) {
		List<T> persList = load(documentListFQN, listType).orElse(Collections.emptyList());
		List<T> inputList = persList.stream().filter(e -> inputIdList.contains(e.getId())).collect(Collectors.toList());
		if (!inputList.isEmpty()) {
			persList.removeAll(inputList);
			store(documentListFQN, listType, persList);// Store
		}
		return inputList.size();
	}

	protected DSDocument loadDocument(DocumentFQN documentFQN) {
		return documentSafeService.readDocument(auth(), documentFQN);
	}

	protected void storeDocument(DocumentFQN documentFQN, byte[] data) {
		DocumentContent documentContent = new DocumentContent(data);
		DSDocument dsDocument = new DSDocument(documentFQN, documentContent, null);
		documentSafeService.storeDocument(auth(), dsDocument);
	}

	protected void deleteDirectory(DocumentDirectoryFQN dirFQN) {
		documentSafeService.deleteFolder(auth(), dirFQN);
	}

	protected ResourceNotFoundException resourceNotFound(Class<?> klass, String id) {
		return new ResourceNotFoundException(klass, id);
	}

	private <T> T loadFunction(DocumentFQN documentFQN, TypeReference<T> valueType) {
		DSDocument dsDocument = loadInternal(documentFQN);
		if (dsDocument == null)
			return null;
		try {
			return objectMapper.readValue(dsDocument.getDocumentContent().getValue(), valueType);
		} catch (IOException e) {
			throw new BaseException(e);
		}
	}

	private DSDocument loadInternal(DocumentFQN documentFQN) {
		if (documentSafeService.documentExists(auth(), documentFQN)) {
			return documentSafeService.readDocument(auth(), documentFQN);
		}
		return null;
	}

	protected <T> void flush(DocumentFQN documentFQN, T entity) {
		user().getRequestCounter().flush(documentFQN);
		DocumentContent documentContent;
		try {
			documentContent = new DocumentContent(objectMapper.writeValueAsBytes(entity));
		} catch (JsonProcessingException e) {
			throw new BaseException(e);
		}
		DSDocument dsDocument = new DSDocument(documentFQN, documentContent, null);
		documentSafeService.storeDocument(auth(), dsDocument);
	}

	protected <T> void delete(DocumentFQN documentFQN) {
		user().getRequestCounter().delete(documentFQN);
		documentSafeService.deleteDocument(auth(), documentFQN);
	}

	protected void enableCaching() {
		user().setCacheEnabled(true);
	}

	protected void flush() {
		if (!user().isCacheEnabled())
			return;
		Collection<Map<DocumentFQN, CacheEntry<?>>> values = user().getCache().values();
		for (Map<DocumentFQN, CacheEntry<?>> map : values) {
			Collection<CacheEntry<?>> collection = map.values();
			for (CacheEntry<?> cacheEntry : collection) {
				if (cacheEntry.isDirty()) {
					if (cacheEntry.getEntry().isPresent()) {
						flush(cacheEntry.getDocFqn(), cacheEntry.getEntry().get());
					} else {
						delete(cacheEntry.getDocFqn());
					}
				}
			}
		}
	}

}
