package de.adorsys.multibanking.service.base;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

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

	protected <T> Optional<T> load(UserIDAuth userIDAuth, DocumentFQN documentFQN, Class<T> klass) {
		DSDocument dsDocument = loadInternal(userIDAuth, documentFQN);
		if(dsDocument==null) return Optional.empty();
		try {
			return Optional.of(objectMapper.readValue(dsDocument.getDocumentContent().getValue(), klass));
		} catch (IOException e) {
			throw new BaseException(e);
		}
	}

	protected <T> Optional<T> load(UserIDAuth userIDAuth, DocumentFQN documentFQN, TypeReference<T> valueType) {
		DSDocument dsDocument = loadInternal(userIDAuth, documentFQN);
		// TODO Peter Document does not exist. Return null; We might think of a not found exception here.
		if(dsDocument==null) return Optional.empty();

		try {
			return Optional.of(objectMapper.readValue(dsDocument.getDocumentContent().getValue(), valueType));
		} catch (IOException e) {
			throw new BaseException(e);
		}
	}

	protected <T> void store(UserIDAuth userIDAuth, DocumentFQN documentFQN, T entity) {
        DocumentContent documentContent;
		try {
			documentContent = new DocumentContent(objectMapper.writeValueAsBytes(entity));
		} catch (JsonProcessingException e) {
			throw new BaseException(e);
		}
        DSDocument dsDocument = new DSDocument(documentFQN, documentContent, null);
		documentSafeService.storeDocument(userIDAuth, dsDocument);
	}
	
	/**
	 * Store an instance of this object in the list.
	 * 
	 * @param t
	 * @return false if object was found and updated, false if object was created.
	 * 
	 */
	protected <T extends IdentityIf> boolean addToList(UserIDAuth user, T t, TypeReference<List<T>> listType, DocumentFQN documentListFQN) {
		List<T> list = load(user, documentListFQN, listType).orElse(Collections.emptyList());
		Optional<T> persistent = list.stream().filter(p -> Ids.eq(p.getId(), t.getId())).findFirst();
		boolean created = true;
		if(persistent.isPresent()){
			// Object exists. Override properties
			BeanUtils.copyProperties(t, persistent);
			// Object is already in the list.
			created = false;
		} else {
			// Object is not yet in the list.
			list.add(t);
		}
		store(user, documentListFQN, list);
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
			DocumentFQN documentListFQN, UserIDAuth userIDAuth) {
		Optional<List<T>> listResult = load(userIDAuth, documentListFQN, listType);
		
		if(!listResult.isPresent()) return Optional.empty();
		List<T> list = listResult.get();
		return list.stream().filter(t -> Ids.eq(id, t.getId())).findFirst();
	}

	/**
	 * Apply the function fnct on object with id-> id from class -> klass at location -> documentListFQN of user -> user
	 * @param fnct
	 * @param id
	 * @param klass
	 * @param documentListFQN
	 * @param user
	 * @return
	 */
	protected <T extends IdentityIf, R> R apply(Function<T, R> fnct, String id, Class<T> klass, TypeReference<List<T>> listType, 
			DocumentFQN documentListFQN, UserIDAuth user) {
		List<T> list = load(user, documentListFQN, listType).orElse(Collections.emptyList());
		// Load
		T t = list.stream().filter(p -> Ids.eq(id, p.getId())).findFirst()
			.orElseThrow(() -> resourceNotFound(klass, id));
		
		R r = fnct.apply(t);// Apply the function.
		
		store(user, documentListFQN, list);// Store		
		return r;
	}

	protected <T extends IdentityIf> void replaceList(List<T> inputList, Class<T> klass, DocumentFQN documentListFQN, UserIDAuth user) {
		List<T> persList = Collections.emptyList();
		// Add new
		inputList.stream().forEach(n -> {
			Ids.id(n);
			persList.add(n);
		});
		store(user, documentListFQN, persList);// Store		
	}
	
	protected <T extends IdentityIf> void updateList(List<T> inputList, Class<T> klass, TypeReference<List<T>> listType, 
			DocumentFQN documentListFQN, UserIDAuth user) {
		List<T> persList = load(user, documentListFQN, listType).orElse(Collections.emptyList());
		// Existing elements.
		List<? extends IdentityIf> foundElements = inputList.stream().filter(i -> persList.contains(i)).collect(Collectors.toList());
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
		
		store(user, documentListFQN, finalList);// Store		
	}

	protected <T extends IdentityIf> void deleteList(List<T> inputList, Class<T> klass, TypeReference<List<T>> listType, 
			DocumentFQN documentListFQN, UserIDAuth user) {
		List<T> persList = load(user, documentListFQN, listType).orElse(Collections.emptyList());
		persList.removeAll(inputList);
		store(user, documentListFQN, persList);// Store		
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
	protected <T extends IdentityIf> int deleteListById(List<String> inputIdList, Class<T> klass, TypeReference<List<T>> listType, 
			DocumentFQN documentListFQN, UserIDAuth user) {
		List<T> persList = load(user, documentListFQN, listType).orElse(Collections.emptyList());
		List<T> inputList = persList.stream().filter(e -> inputIdList.contains(e.getId())).collect(Collectors.toList());
		if(!inputList.isEmpty()) {
			persList.removeAll(inputList);
			store(user, documentListFQN, persList);// Store
		}
		return inputList.size();
	}
	
	protected DSDocument loadDocument(UserIDAuth userIDAuth, DocumentFQN documentFQN) {
		return documentSafeService.readDocument(userIDAuth, documentFQN);
	}

	protected void storeDocument(UserIDAuth userIDAuth, DocumentFQN documentFQN, byte[] data) {
		DocumentContent documentContent = new DocumentContent(data);
		DSDocument dsDocument = new DSDocument(documentFQN, documentContent, null);
		documentSafeService.storeDocument(userIDAuth, dsDocument);
	}
	
	protected void deleteDirectory(UserIDAuth userIDAuth, DocumentDirectoryFQN dirFQN){
		documentSafeService.deleteFolder(userIDAuth, dirFQN);
	}	

	protected ResourceNotFoundException resourceNotFound(Class<?> klass, String id) {
		return new ResourceNotFoundException(klass, id);
	}
	
	protected boolean documentExists(UserIDAuth userIDAuth, DocumentFQN documentFQN){
		return documentSafeService.documentExists(userIDAuth, documentFQN);
	}

	
	private DSDocument loadInternal(UserIDAuth userIDAuth, DocumentFQN documentFQN) {
		try {
			return documentSafeService.readDocument(userIDAuth, documentFQN);
		} catch(final BaseException e){
			// TODO Peter, how do i know a document does not exists.
			
			// If document exist, then we have another problem reading the document.
			if(documentSafeService.documentExists(userIDAuth, documentFQN)) throw e;
			return null;
		}
	}
}
