package de.adorsys.multibanking.pers.docusafe.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.adorsys.docusafe.business.types.complex.DocumentFQN;
import org.apache.commons.lang3.StringUtils;

import de.adorsys.multibanking.domain.StandingOrderEntity;
import de.adorsys.multibanking.pers.docusafe.domain.MapKey;
import de.adorsys.multibanking.pers.spi.repository.StandingOrderRepositoryIf;

public class StandingOrderRepositoryImpl extends BaseRepositoryImpl implements StandingOrderRepositoryIf {
	private static final String RESOURCE_FILE = "StandingOrders";

	@Override
	public List<StandingOrderEntity> findByUserIdAndAccountId(String userId, String accountId) {
		List<StandingOrderEntity> result = new ArrayList<>();
		StandingOrderEntity[] entities = load();
		for (StandingOrderEntity standingOrderEntity : entities) {
			if(StringUtils.equalsAnyIgnoreCase(standingOrderEntity.getAccountId(), accountId))
				result.add(standingOrderEntity);
		}
		return result;
	}

	@Override
	public void save(List<StandingOrderEntity> standingOrders) {
		Map<String, StandingOrderEntity> map = loadMap();
		boolean shallSave = false;
		for (StandingOrderEntity entity : standingOrders) {
			StandingOrderEntity removed = map.put(entity.getId(), entity);
			shallSave |= (removed!=entity);
		}
		if(shallSave)storeMap(map);
	}

	@Override
	public void deleteByAccountId(String accountId) {
		StandingOrderEntity[] entities = load();
		Map<String, StandingOrderEntity> map = loadMap(entities, mapKey);
		boolean shallSave = false;
		for (StandingOrderEntity entity : entities) {
			map.remove(entity.getId());
			shallSave|=true;
		}
		if(shallSave)storeMap(map);
	}


	private DocumentFQN documentFQN() {
		return new DocumentFQN(RESOURCE_FILE);
	}

	private StandingOrderEntity[] load() {
		return userDocumentRepository.read(documentFQN(),StandingOrderEntity[].class);
	}

	private Map<String, StandingOrderEntity> loadMap() {
		return loadMap(load(), mapKey);
	}

	private void storeMap(Map<String, StandingOrderEntity> entityMap) {
		ArrayList<StandingOrderEntity> arrayList = new ArrayList<>();
		arrayList.addAll(entityMap.values());
		userDocumentRepository.write(documentFQN(), arrayList.toArray(new StandingOrderEntity[arrayList.size()]));
	}

	public static final MapKey<StandingOrderEntity> mapKey = new MapKey<StandingOrderEntity>() {
		@Override
		public String getId(StandingOrderEntity value) {
			return value.getId();
		}
	};
}
