package de.adorsys.multibanking.service;

import java.util.Collections;
import java.util.List;

import org.adorsys.docusafe.business.types.complex.DSDocument;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;

import de.adorsys.multibanking.domain.CustomRuleEntity;
import de.adorsys.multibanking.service.base.BaseUserIdService;
import de.adorsys.multibanking.service.helper.RuleUtils;

/**
 * TODO: reset rule provider after update.
 * 		ruleToUpdate.updateSearchIndex();
 * 
 * @author fpo 2018-03-24 01:43
 *
 */
@Service
public class CustomBookingRuleService extends BaseUserIdService {

    public DSDocument getCustomBookingRules() {
    	return loadDocument(userIDAuth, RuleUtils.bookingRulesFQN);
    }

	public void createOrUpdateCustomRule(CustomRuleEntity ruleEntity) {
		createOrUpdateCustomRules(Collections.singletonList(ruleEntity));
	}
	
	public void createOrUpdateCustomRules(List<CustomRuleEntity> ruleEntities) {
		updateList(RuleUtils.normalize(ruleEntities), CustomRuleEntity.class, listType(), RuleUtils.bookingRulesFQN, userIDAuth);
	}
	
	public void replceCustomRules(List<CustomRuleEntity> ruleEntities) {
		replaceList(RuleUtils.normalize(ruleEntities), CustomRuleEntity.class, RuleUtils.bookingRulesFQN, userIDAuth);
	}

	public boolean deleteCustomRule(String ruleId) {
		return deleteCustomRules(Collections.singletonList(ruleId));
	}
	public boolean deleteCustomRules(List<String> ruleIds) {
		return deleteListById(ruleIds, CustomRuleEntity.class, listType(), RuleUtils.bookingRulesFQN, userIDAuth)>0;
	}


	private static TypeReference<List<CustomRuleEntity>> listType(){
		return new TypeReference<List<CustomRuleEntity>>() {};
	}
}
