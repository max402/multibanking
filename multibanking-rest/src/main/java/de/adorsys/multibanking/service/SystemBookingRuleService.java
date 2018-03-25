package de.adorsys.multibanking.service;

import java.util.Collections;
import java.util.List;

import org.adorsys.docusafe.business.types.complex.DSDocument;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;

import de.adorsys.multibanking.domain.CategoryEntity;
import de.adorsys.multibanking.domain.RuleEntity;
import de.adorsys.multibanking.service.base.BaseSystemIdService;
import de.adorsys.multibanking.service.helper.RuleUtils;

/**
 * TODO: reset rule provider after update.
 * 		ruleToUpdate.updateSearchIndex();
 * @author fpo
 *
 */
@Service
public class SystemBookingRuleService extends BaseSystemIdService {

    /**
	 * Static rules a stored in the system storage and is accessible to all users.
     * 
     * @return
     */
	public DSDocument getStaticBookingRules() {
    	return loadDocument(userIDAuth(), RuleUtils.bookingRulesFQN);
	}

	/**
	 * Static rules a stored in the system storage and is accessible to all users.
	 * 
	 * @param ruleEntity
	 */
	public void createOrUpdateStaticRule(RuleEntity ruleEntity) {
		createOrUpdateStaticRules(Collections.singletonList(ruleEntity));
	}
	
	public void createOrUpdateStaticRules(List<RuleEntity> ruleEntities) {
		updateList(normalize(ruleEntities), RuleEntity.class, listType(), RuleUtils.bookingRulesFQN, userIDAuth());
	}
	
	public void replceStaticRules(List<RuleEntity> ruleEntities) {
		replaceList(normalize(ruleEntities), RuleEntity.class, RuleUtils.bookingRulesFQN, userIDAuth());
	}
	public boolean deleteStaticRule(String ruleId) {
		return deleteStaticRules(Collections.singletonList(ruleId));
	}

	public boolean deleteStaticRules(List<String> ruleIds) {
		return deleteListById(ruleIds, RuleEntity.class, listType(), RuleUtils.bookingRulesFQN, userIDAuth())>0;
	}

	private static <T extends RuleEntity> List<T> normalize(List<T> list){
		for (T t : list) {
			// Normalize creditor id
			t.setCreditorId(StringUtils.removeAll(t.getCreditorId(), StringUtils.EMPTY));
			t.updateSearchIndex();
		}
		return list;
	}
	
	private static TypeReference<List<RuleEntity>> listType(){
		return new TypeReference<List<RuleEntity>>() {};
	}
}
