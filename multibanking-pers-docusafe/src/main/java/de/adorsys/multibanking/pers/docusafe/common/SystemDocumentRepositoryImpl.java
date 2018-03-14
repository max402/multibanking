package de.adorsys.multibanking.pers.docusafe.common;

import java.util.Optional;

import org.adorsys.docusafe.business.types.complex.DocumentFQN;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.adorsys.multibanking.pers.docusafe.domain.SystemIDAuth;

@Service
public class SystemDocumentRepositoryImpl extends BaseDocumentRepositoryImpl {
	@Autowired
	protected SystemIDAuth systemIdAuth;

	public <T> T read(DocumentFQN documentFQN, Class<T> klass) {
		Optional<T> entity = read(systemIdAuth.getUserIDAuth(), documentFQN, klass);
		if (!entity.isPresent())return null;
		return entity.get();
	}

	public <T> void write(DocumentFQN documentFQN, T entity) {
		write(systemIdAuth.getUserIDAuth(), documentFQN, entity);
	}

	public boolean exists(DocumentFQN documentFQN) {
		return documentSafeService.documentExists(systemIdAuth.getUserIDAuth(), documentFQN);
	}

	public void delete(DocumentFQN documentFQN) {
		documentSafeService.deleteDocument(systemIdAuth.getUserIDAuth(), documentFQN);
	}
}
