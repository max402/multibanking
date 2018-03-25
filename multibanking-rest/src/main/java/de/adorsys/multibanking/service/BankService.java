package de.adorsys.multibanking.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import de.adorsys.multibanking.domain.BankEntity;
import de.adorsys.multibanking.service.base.BaseSystemIdService;
import de.adorsys.multibanking.utils.FQNUtils;

@Service
public class BankService extends BaseSystemIdService {

	private final YAMLFactory ymlFactory = new YAMLFactory();
	private final ObjectMapper ymlObjectMapper = new ObjectMapper(ymlFactory);

	public Optional<BankEntity> findByBankCode(String bankCode) {
		return find(bankCode, BankEntity.class, listType(), FQNUtils.banksFQN(), userIDAuth());
	}
	
	public List<BankEntity> load(){
		return load(userIDAuth(), FQNUtils.banksFQN(), listType())
				.orElse(Collections.emptyList());
	}

	public List<BankEntity> search(String string) {
		return null;
	}
	
    public void importBanks(InputStream inputStream) throws IOException {
    	// InputStream inputStream = this.getClass().getClassLoader().getResource("catalogue/banks/bank-catalogue.yml").openStream()
    	List<BankEntity> banks = ymlObjectMapper.readValue(inputStream, new TypeReference<List<BankEntity>>() {});
    	// Copy bank code to id.
    	banks.stream().forEach(b -> { b.setId(b.getBankCode());});
    	store(userIDAuth(), FQNUtils.banksFQN(), banks);
    }

	private static TypeReference<List<BankEntity>> listType(){
		return new TypeReference<List<BankEntity>>() {};
	}
}
