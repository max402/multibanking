package de.adorsys.multibanking.pers.docusafe.common;

import java.io.IOException;
import java.util.Optional;

import org.adorsys.cryptoutils.exceptions.BaseException;
import org.adorsys.docusafe.business.DocumentSafeService;
import org.adorsys.docusafe.business.types.complex.DSDocument;
import org.adorsys.docusafe.business.types.complex.DocumentFQN;
import org.adorsys.docusafe.business.types.complex.UserIDAuth;
import org.adorsys.docusafe.service.types.DocumentContent;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class BaseDocumentRepositoryImpl {

	@Autowired
	protected DocumentSafeService documentSafeService;
	@Autowired
	protected ObjectMapper objectMapper;
	
	public <T> Optional<T> read(UserIDAuth userIDAuth, DocumentFQN documentFQN, Class<T> klass) {
		// Ignore user id. Must be provided in the uaserIDAuth
		DSDocument dsDocument = documentSafeService.readDocument(userIDAuth, documentFQN);
		if(dsDocument==null) return Optional.empty();
		try {
			return Optional.of(objectMapper.readValue(dsDocument.getDocumentContent().getValue(), klass));
		} catch (IOException e) {
			throw new BaseException(e);
		}
	}

	public <T> void write(UserIDAuth userIDAuth, DocumentFQN documentFQN, T entity) {
        DocumentContent documentContent;
		try {
			documentContent = new DocumentContent(objectMapper.writeValueAsBytes(entity));
		} catch (JsonProcessingException e) {
			throw new BaseException(e);
		}
        DSDocument dsDocument = new DSDocument(documentFQN, documentContent, null);
		documentSafeService.storeDocument(userIDAuth, dsDocument);
	}
	
	public boolean exists(UserIDAuth userIDAuth, DocumentFQN documentFQN){
		return documentSafeService.documentExists(userIDAuth, documentFQN);
	}
	
	public void delete(UserIDAuth userIDAuth, DocumentFQN documentFQN){
		documentSafeService.deleteDocument(userIDAuth, documentFQN);
	}
}
