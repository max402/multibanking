package de.adorsys.multibanking.pers.docusafe.repository;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

import org.adorsys.docusafe.business.types.complex.DocumentFQN;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import de.adorsys.multibanking.domain.AccountAnalyticsEntity;
import de.adorsys.multibanking.pers.docusafe.domain.IdFactory;
import de.adorsys.multibanking.pers.docusafe.domain.MapKey;
import de.adorsys.multibanking.pers.spi.repository.AnalyticsRepositoryIf;

/**
 * Store Analytics for all user accounts in a single file, keyed by their
 * account id.
 * 
 * The file containing analytics is a list. Nevertheless we load it as a Map to
 * put or remove the target entry.
 * 
 * @author fpo
 *
 */
@Service
public class AnalyticsRepositoryImpl extends BaseRepositoryImpl implements AnalyticsRepositoryIf {
	private static final String RESOURCE_FILE = "AccountAnalytics";

	/**
	 * We ignore the user id parameter. userid is allways read from the context.
	 * 
	 */
	@Override
	public Optional<AccountAnalyticsEntity> findLastByUserIdAndAccountId(String userId, String bankAccountId) {
		return Optional.of(loadMap().get(bankAccountId));
	}

	@Override
	public void save(AccountAnalyticsEntity accountAnalyticsEntity) {
		if (StringUtils.isBlank(accountAnalyticsEntity.getId()))
			accountAnalyticsEntity.setId(IdFactory.uuid());

		Map<String, AccountAnalyticsEntity> map = loadMap();
		map.put(accountAnalyticsEntity.getAccountId(), accountAnalyticsEntity);
		storeMap(map);
	}

	@Override
	public void deleteByAccountId(String id) {
		Map<String, AccountAnalyticsEntity> map = loadMap();
		AccountAnalyticsEntity entity = map.remove(id);
		if (entity != null)
			storeMap(map);
	}

	private DocumentFQN documentFQN() {
		return new DocumentFQN(RESOURCE_FILE);
	}

	private AccountAnalyticsEntity[] load() {
		return userDocumentRepository.read(documentFQN(),AccountAnalyticsEntity[].class);
	}

	private Map<String, AccountAnalyticsEntity> loadMap() {
		return loadMap(load(), mapKey);
	}

	private void storeMap(Map<String, AccountAnalyticsEntity> entityMap) {
		ArrayList<AccountAnalyticsEntity> arrayList = new ArrayList<>();
		arrayList.addAll(entityMap.values());
		userDocumentRepository.write(documentFQN(), arrayList.toArray(new AccountAnalyticsEntity[arrayList.size()]));
	}

	public static final MapKey<AccountAnalyticsEntity> mapKey = new MapKey<AccountAnalyticsEntity>() {
		@Override
		public String getId(AccountAnalyticsEntity value) {
			return value.getAccountId();
		}
	};
}
