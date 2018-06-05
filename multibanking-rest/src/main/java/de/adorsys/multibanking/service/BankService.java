package de.adorsys.multibanking.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.adorsys.docusafe.business.DocumentSafeService;
import org.adorsys.docusafe.business.types.complex.DSDocument;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import de.adorsys.multibanking.auth.SystemContext;
import de.adorsys.multibanking.auth.UserObjectPersistenceService;
import de.adorsys.multibanking.domain.BankEntity;
import de.adorsys.multibanking.service.base.ListUtils;
import de.adorsys.multibanking.utils.FQNUtils;

@Service
public class BankService {

    private final UserObjectPersistenceService uos;
	private final YAMLFactory ymlFactory = new YAMLFactory();
	private final ObjectMapper ymlObjectMapper = new ObjectMapper(ymlFactory);

    public BankService(ObjectMapper objectMapper, SystemContext systemContext, DocumentSafeService documentSafeService) {
        uos = new UserObjectPersistenceService(systemContext.getUser(), objectMapper, documentSafeService);
    }
	
	public Optional<BankEntity> findByBankCode(String bankCode) {
		return ListUtils.find(bankCode, uos.load(FQNUtils.banksFQN(), listType()).orElse(Collections.emptyList()));
	}
	
	public List<BankEntity> load(){
		return uos.load(FQNUtils.banksFQN(), listType())
				.orElse(Collections.emptyList());
	}

	public DSDocument loadDocument() {
		return uos.readDocument(FQNUtils.banksFQN(), listType());
	}
	
    public void importBanks(InputStream inputStream) throws IOException {
    	List<BankEntity> banks = ymlObjectMapper.readValue(inputStream, new TypeReference<List<BankEntity>>() {});
    	// Copy bank code to id.
    	banks.stream().forEach(b -> { b.setId(b.getBankCode());});
    	uos.store(FQNUtils.banksFQN(), listType(), banks);
    }

	private static TypeReference<List<BankEntity>> listType(){
		return new TypeReference<List<BankEntity>>() {};
	}
}
