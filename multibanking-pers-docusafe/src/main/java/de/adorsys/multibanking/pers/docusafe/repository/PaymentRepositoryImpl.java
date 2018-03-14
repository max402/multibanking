package de.adorsys.multibanking.pers.docusafe.repository;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

import org.adorsys.docusafe.business.types.complex.DocumentFQN;
import org.apache.commons.lang3.StringUtils;

import de.adorsys.multibanking.domain.PaymentEntity;
import de.adorsys.multibanking.pers.docusafe.domain.IdFactory;
import de.adorsys.multibanking.pers.docusafe.domain.MapKey;
import de.adorsys.multibanking.pers.spi.repository.PaymentRepositoryIf;

public class PaymentRepositoryImpl extends BaseRepositoryImpl implements PaymentRepositoryIf {
	private static final String RESOURCE_FILE = "Payments";

	@Override
	public Optional<PaymentEntity> findByUserIdAndId(String userId, String id) {
		return Optional.ofNullable(loadMap().get(id));
	}

	@Override
	public void save(PaymentEntity paymentEntity) {
		Map<String, PaymentEntity> map = loadMap();
		if(StringUtils.isBlank(paymentEntity.getId()))paymentEntity.setId(IdFactory.uuid());
		PaymentEntity removed = map.put(paymentEntity.getId(), paymentEntity);
		if(removed!=paymentEntity)storeMap(map);
	}

	@Override
	public void delete(String id) {
		Map<String, PaymentEntity> map = loadMap();
		PaymentEntity removed = map.remove(id);
		if(removed!=null)storeMap(map);
	}

	private DocumentFQN documentFQN() {
		return new DocumentFQN(RESOURCE_FILE);
	}

	private PaymentEntity[] load() {
		return userDocumentRepository.read(documentFQN(),PaymentEntity[].class);
	}

	private Map<String, PaymentEntity> loadMap() {
		return loadMap(load(), mapKey);
	}

	private void storeMap(Map<String, PaymentEntity> entityMap) {
		ArrayList<PaymentEntity> arrayList = new ArrayList<>();
		arrayList.addAll(entityMap.values());
		userDocumentRepository.write(documentFQN(), arrayList.toArray(new PaymentEntity[arrayList.size()]));
	}

	public static final MapKey<PaymentEntity> mapKey = new MapKey<PaymentEntity>() {
		@Override
		public String getId(PaymentEntity value) {
			return value.getId();
		}
	};
}
