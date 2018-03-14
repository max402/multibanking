package de.adorsys.multibanking.pers.docusafe.api;

import java.util.Optional;

import org.adorsys.docusafe.business.types.complex.DocumentFQN;
import org.adorsys.docusafe.business.types.complex.UserIDAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserDocumentRepositoryImpl extends BaseDocumentRepositoryImpl {
	@Autowired
	protected UserIDAuth userIDAuth;
	
	public <T> T read(DocumentFQN documentFQN, Class<T> klass) {
		Optional<T> entity = read(userIDAuth, documentFQN, klass);
		if (!entity.isPresent())return null;
		return entity.get();
		
	}
	
	public <T> void write(DocumentFQN documentFQN, T entity) {
		write(userIDAuth, documentFQN, entity);
	}

	public boolean exists(DocumentFQN documentFQN){
		return documentSafeService.documentExists(userIDAuth, documentFQN);
	}
	
	public void delete(DocumentFQN documentFQN){
		documentSafeService.deleteDocument(userIDAuth, documentFQN);
	}
	
	public void removeUser(){
		documentSafeService.destroyUser(userIDAuth);
	}
}
