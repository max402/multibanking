package de.adorsys.multibanking.pers.docusafe.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.adorsys.docusafe.business.types.complex.DocumentFQN;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import de.adorsys.multibanking.domain.RuleEntity;
import de.adorsys.multibanking.pers.docusafe.domain.IdFactory;
import de.adorsys.multibanking.pers.docusafe.domain.MapKey;
import de.adorsys.multibanking.pers.docusafe.repository.BaseRepositoryImpl;

public class SystemRuleRepositoryImpl extends BaseRepositoryImpl{
	private static final String RULE_RESOURCE_FILE = "BookingRules";

	public List<RuleEntity> findByIncoming(boolean incoming) {
		RuleEntity[] entities = load();
		List<RuleEntity> result = new ArrayList<>();
		for (RuleEntity ruleEntity : entities) {
			if(ruleEntity.isIncoming()==incoming)result.add(ruleEntity);
		}
		return result;
	}

	public Page<? extends RuleEntity> findAllPageable(Pageable pageable) {
		RuleEntity[] entities = load();
		List<RuleEntity> list = Arrays.asList(entities);
		if(pageable.getOffset()>list.size()-1) return new PageImpl<>(Collections.emptyList());
		List<RuleEntity> subList = list.subList(pageable.getOffset(), Math.min(pageable.getOffset()+pageable.getPageSize(), list.size()-1));
		return new PageImpl<>(subList);
	}

	public List<? extends RuleEntity> findAll() {
		return Arrays.asList(load());
	}

	public RuleEntity createOrUpdateRule(RuleEntity ruleEntity) {
		Map<String, RuleEntity> map = loadMap();
		if(StringUtils.isBlank(ruleEntity.getId())) ruleEntity.setId(IdFactory.uuid());
		map.put(ruleEntity.getId(), ruleEntity);
		store(map);
		return ruleEntity;
	}

	public Optional<? extends RuleEntity> getRuleById(String ruleId) {
		return Optional.ofNullable(loadMap().get(ruleId));
	}

	public void deleteRule(String id) {
		Map<String, RuleEntity> map = loadMap();
		RuleEntity removed = map.remove(id);
		if(removed!=null)store(map);
	}

	public void replacesRules(List<? extends RuleEntity> rules) {
		Map<String, RuleEntity> map = loadMap();
		boolean shallSave = false;
		for (RuleEntity ruleEntity : rules) {
			RuleEntity removed = map.put(ruleEntity.getId(), ruleEntity);
			shallSave |= (removed!=ruleEntity);
		}
		if(shallSave)store(map);
	}


	private DocumentFQN documentFQN() {
		return new DocumentFQN(RULE_RESOURCE_FILE);
	}

	private RuleEntity[] load(){
		return systemDocumentRepository.read(documentFQN(), RuleEntity[].class);
	}

	private Map<String, RuleEntity> loadMap(){
		RuleEntity[] entities = load();
		return loadMap(entities, mapKey);
	}
	
	private void store(Map<String, RuleEntity> map){
		ArrayList<RuleEntity> arrayList = new ArrayList<>();
		arrayList.addAll(map.values());
		systemDocumentRepository.write(documentFQN(), arrayList.toArray(new RuleEntity[arrayList.size()]));
	}

	private static final MapKey<RuleEntity> mapKey = new MapKey<RuleEntity>() {
		@Override
		public String getId(RuleEntity value) {
			return value.getId();
		}
	};
}
