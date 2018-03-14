package de.adorsys.multibanking.pers.docusafe.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.adorsys.docusafe.business.types.complex.DocumentFQN;

import de.adorsys.multibanking.domain.BankEntity;
import de.adorsys.multibanking.pers.docusafe.domain.MapKey;
import de.adorsys.multibanking.pers.docusafe.repository.BaseRepositoryImpl;
import de.adorsys.multibanking.pers.spi.repository.BankRepositoryIf;

public class BankRepositoryImpl extends BaseRepositoryImpl implements BankRepositoryIf {
	private static final String RESOURCE_FILE = "Banks";

	@Override
	public Optional<BankEntity> findByBankCode(String bankCode) {
		return Optional.ofNullable(loadMap().get(bankCode));
	}

	@Override
	public void save(BankEntity bank) {
		Map<String, BankEntity> map = loadMap();
		map.put(bank.getBankCode(), bank);
		store(map);
	}

	@Override
	public List<BankEntity> search(String terms) {
		throw new UnsupportedOperationException();
	}

	private DocumentFQN documentFQN() {
		return new DocumentFQN(RESOURCE_FILE);
	}

	private BankEntity[] load(){
		return systemDocumentRepository.read(documentFQN(), BankEntity[].class);
	}
	
	private Map<String, BankEntity> loadMap(){
		BankEntity[] entities = load();
		return loadMap(entities, mapKey);
	}
	
	private void store(Map<String, BankEntity> map){
		ArrayList<BankEntity> arrayList = new ArrayList<>();
		arrayList.addAll(map.values());
		userDocumentRepository.write(documentFQN(), arrayList.toArray(new BankEntity[arrayList.size()]));
	}

	private static final MapKey<BankEntity> mapKey = new MapKey<BankEntity>() {
		@Override
		public String getId(BankEntity value) {
			return value.getBankCode();
		}
	};
}
