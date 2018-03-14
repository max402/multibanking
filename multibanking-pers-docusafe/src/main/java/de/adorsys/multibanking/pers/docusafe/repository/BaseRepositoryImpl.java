package de.adorsys.multibanking.pers.docusafe.repository;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import de.adorsys.multibanking.pers.docusafe.common.SystemDocumentRepositoryImpl;
import de.adorsys.multibanking.pers.docusafe.common.UserDocumentRepositoryImpl;
import de.adorsys.multibanking.pers.docusafe.domain.MapKey;

public abstract class BaseRepositoryImpl {
	@Autowired
	protected UserDocumentRepositoryImpl userDocumentRepository;
	@Autowired
	protected SystemDocumentRepositoryImpl systemDocumentRepository;

	protected <T> Map<String, T> loadMap(T[] list, MapKey<T> mapKey){
		Map<String, T> entities = new HashMap<>();
		for (T t : list) {
			entities.put(mapKey.getId(t), t);
		}
		return entities;
	}
}
