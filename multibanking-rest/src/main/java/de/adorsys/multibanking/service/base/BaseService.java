package de.adorsys.multibanking.service.base;

import java.io.IOException;
import java.util.Optional;

import org.adorsys.cryptoutils.exceptions.BaseException;
import org.adorsys.docusafe.business.DocumentSafeService;
import org.adorsys.docusafe.business.types.complex.DSDocument;
import org.adorsys.docusafe.business.types.complex.DocumentDirectoryFQN;
import org.adorsys.docusafe.business.types.complex.DocumentFQN;
import org.adorsys.docusafe.business.types.complex.UserIDAuth;
import org.adorsys.docusafe.service.types.DocumentContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.adorsys.multibanking.auth.SystemIDAuth;
import de.adorsys.multibanking.exception.ResourceNotFoundException;

@Service
public class BaseService {
	private final static Logger LOGGER = LoggerFactory.getLogger(BaseService.class);

	@Autowired
	protected DocumentSafeService documentSafeService;

	@Autowired
	protected ObjectMapper objectMapper;
	
	@Autowired
	protected UserIDAuth userIDAuth;	
	
	@Autowired
	protected SystemIDAuth systemIDAuth;

	protected <T> T load(UserIDAuth userIDAuth, DocumentFQN documentFQN, Class<T> klass) {
		DSDocument dsDocument = loadInternal(userIDAuth, documentFQN);
		if(dsDocument==null) return null;
		try {
			return objectMapper.readValue(dsDocument.getDocumentContent().getValue(), klass);
		} catch (IOException e) {
			throw new BaseException(e);
		}
	}
	protected <T> Optional<T> loadOptional(UserIDAuth userIDAuth, DocumentFQN documentFQN, Class<T> klass) {
		return Optional.ofNullable(load(userIDAuth, documentFQN, klass));
	}
	
	
	protected <T> T load(UserIDAuth userIDAuth, DocumentFQN documentFQN, TypeReference<T> valueType) {
		DSDocument dsDocument = loadInternal(userIDAuth, documentFQN);
		// TODO Peter Document does not exist. Return null; We might think of a not found exception here.
		if(dsDocument==null) return null;

		try {
			return objectMapper.readValue(dsDocument.getDocumentContent().getValue(), valueType);
		} catch (IOException e) {
			throw new BaseException(e);
		}
	}
	protected <T> Optional<T> loadOptional(UserIDAuth userIDAuth, DocumentFQN documentFQN, TypeReference<T> valueType) {
		return Optional.ofNullable(load(userIDAuth, documentFQN, valueType));
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
	
	protected DSDocument loadDocument(UserIDAuth userIDAuth, DocumentFQN documentFQN) {
		return documentSafeService.readDocument(userIDAuth, documentFQN);
	}

	protected void storeDocument(UserIDAuth userIDAuth, DocumentFQN documentFQN, byte[] data) {
		DocumentContent documentContent = new DocumentContent(data);
		DSDocument dsDocument = new DSDocument(documentFQN, documentContent, null);
		documentSafeService.storeDocument(userIDAuth, dsDocument);
	}
	
	protected void deleteDirectory(UserIDAuth userIDAuth, DocumentDirectoryFQN dirFQN){
		// TODO Peter implement
//		documentSafeService.deleteDir(userIDAuth, dirFQN);
	}
	

	protected byte[] toByte(Object object) {
		try {
			return objectMapper.writeValueAsBytes(object);
		} catch (JsonProcessingException e) {
			throw new BaseException(e);
		}
	}

	protected <T> T fromBytes(byte[] data, Class<T> klass) {
		try {
			return objectMapper.readValue(data, klass);
		} catch (IOException e) {
			throw new BaseException(e);
		}
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
