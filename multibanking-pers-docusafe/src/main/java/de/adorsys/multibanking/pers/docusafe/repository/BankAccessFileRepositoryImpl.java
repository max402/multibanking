package de.adorsys.multibanking.pers.docusafe.repository;

import org.adorsys.docusafe.business.types.complex.DocumentFQN;
import org.springframework.stereotype.Service;

import de.adorsys.multibanking.pers.docusafe.domain.BankAccessFile;

@Service
public class BankAccessFileRepositoryImpl extends BaseRepositoryImpl {
	
	private static final String RESOURCE_FILE = "BankAccesses";
		
	public BankAccessFile load() {
		return userDocumentRepository.read(documentFQN(), BankAccessFile.class);
	}

	public void save(BankAccessFile bankAccessFile) {
		userDocumentRepository.write(documentFQN(), bankAccessFile);
	}

	private DocumentFQN documentFQN() {
		return new DocumentFQN(RESOURCE_FILE);
	}
}
