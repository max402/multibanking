package de.adorsys.multibanking.pers.docusafe.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.adorsys.docusafe.business.types.complex.DocumentFQN;
import org.apache.commons.lang3.StringUtils;

import de.adorsys.multibanking.domain.ContractEntity;
import de.adorsys.multibanking.pers.docusafe.domain.MapKey;
import de.adorsys.multibanking.pers.spi.repository.ContractRepositoryIf;

public class ContractRepositoryImpl extends BaseRepositoryImpl implements ContractRepositoryIf {
	private static final String RESOURCE_FILE = "Contracts";

	@Override
	public List<ContractEntity> findByUserIdAndAccountId(String userId, String accountId) {
		List<ContractEntity> result = new ArrayList<>();
		ContractEntity[] entities = load();
		for (ContractEntity contractEntity : entities) {
			if(StringUtils.equalsAnyIgnoreCase(contractEntity.getAccountId(), accountId))
				result.add(contractEntity);
		}
		return result;
	}

	@Override
	public void save(List<ContractEntity> contractEntities) {
		Map<String, ContractEntity> map = loadMap();
		boolean shallSave = false;
		for (ContractEntity contractEntity : contractEntities) {
			ContractEntity removed = map.put(contractEntity.getId(), contractEntity);
			shallSave |= (removed!=contractEntity);
		}
		if(shallSave)storeMap(map);
	}

	@Override
	public void deleteByAccountId(String accountId) {
		ContractEntity[] entities = load();
		Map<String, ContractEntity> map = loadMap(entities, mapKey);
		boolean shallSave = false;
		for (ContractEntity contractEntity : entities) {
			map.remove(contractEntity.getId());
			shallSave|=true;
		}
		if(shallSave)storeMap(map);
	}

	private DocumentFQN documentFQN() {
		return new DocumentFQN(RESOURCE_FILE);
	}

	private ContractEntity[] load() {
		return userDocumentRepository.read(documentFQN(),ContractEntity[].class);
	}

	private Map<String, ContractEntity> loadMap() {
		return loadMap(load(), mapKey);
	}

	private void storeMap(Map<String, ContractEntity> entityMap) {
		ArrayList<ContractEntity> arrayList = new ArrayList<>();
		arrayList.addAll(entityMap.values());
		userDocumentRepository.write(documentFQN(), arrayList.toArray(new ContractEntity[arrayList.size()]));
	}

	public static final MapKey<ContractEntity> mapKey = new MapKey<ContractEntity>() {
		@Override
		public String getId(ContractEntity value) {
			return value.getId();
		}
	};
}
