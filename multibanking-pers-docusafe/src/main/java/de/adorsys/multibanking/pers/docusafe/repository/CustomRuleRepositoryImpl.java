package de.adorsys.multibanking.pers.docusafe.repository;

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

import de.adorsys.multibanking.domain.CustomRuleEntity;
import de.adorsys.multibanking.domain.RuleEntity;
import de.adorsys.multibanking.pers.docusafe.domain.IdFactory;
import de.adorsys.multibanking.pers.docusafe.domain.MapKey;

public class CustomRuleRepositoryImpl extends BaseRepositoryImpl {
	private static final String RULE_RESOURCE_FILE = "CustomBookingRules";

	public List<CustomRuleEntity> findByUserIdAndIncomingCustomRules(String userId, boolean incoming) {
		CustomRuleEntity[] entities = load();
		List<CustomRuleEntity> result = new ArrayList<>();
		for (CustomRuleEntity ruleEntity : entities) {
			if(ruleEntity.isIncoming()==incoming)result.add(ruleEntity);
		}
		return result;
	}

	public Page<CustomRuleEntity> findAllPageable(Pageable pageable) {
		CustomRuleEntity[] entities = load();
		List<CustomRuleEntity> list = Arrays.asList(entities);
		if(pageable.getOffset()>list.size()-1) return new PageImpl<>(Collections.emptyList());
		List<CustomRuleEntity> subList = list.subList(pageable.getOffset(), Math.min(pageable.getOffset()+pageable.getPageSize(), list.size()-1));
		return new PageImpl<>(subList);
	}

	public List<? extends RuleEntity> findAll() {
		return Arrays.asList(load());
	}

	public CustomRuleEntity createOrUpdateCustomRule(CustomRuleEntity ruleEntity) {
		Map<String, CustomRuleEntity> map = loadMap();
		if(StringUtils.isBlank(ruleEntity.getId())) ruleEntity.setId(IdFactory.uuid());
		map.put(ruleEntity.getId(), ruleEntity);
		store(map);
		return ruleEntity;
	}

	public Optional<CustomRuleEntity> getRuleById(String ruleId) {
		return Optional.ofNullable(loadMap().get(ruleId));
	}

	public void deleteCustomRule(String id) {
		Map<String, CustomRuleEntity> map = loadMap();
		CustomRuleEntity removed = map.remove(id);
		if(removed!=null)store(map);
	}

	public void replacesRules(List<? extends RuleEntity> rules) {
		Map<String, CustomRuleEntity> map = loadMap();
		boolean shallSave = false;
		for (RuleEntity ruleEntity : rules) {
			CustomRuleEntity cre = (CustomRuleEntity) ruleEntity;
			RuleEntity removed = map.put(ruleEntity.getId(), cre);
			shallSave |= (removed!=ruleEntity);
		}
		if(shallSave)store(map);
	}


	private DocumentFQN documentFQN() {
		return new DocumentFQN(RULE_RESOURCE_FILE);
	}

	private CustomRuleEntity[] load(){
		return userDocumentRepository.read(documentFQN(), CustomRuleEntity[].class);
	}

	private Map<String, CustomRuleEntity> loadMap(){
		CustomRuleEntity[] entities = load();
		return loadMap(entities, mapKey);
	}
	
	private void store(Map<String, CustomRuleEntity> map){
		ArrayList<CustomRuleEntity> arrayList = new ArrayList<>();
		arrayList.addAll(map.values());
		userDocumentRepository.write(documentFQN(), arrayList.toArray(new CustomRuleEntity[arrayList.size()]));
	}

	private static final MapKey<CustomRuleEntity> mapKey = new MapKey<CustomRuleEntity>() {
		@Override
		public String getId(CustomRuleEntity value) {
			return value.getId();
		}
	};
}
