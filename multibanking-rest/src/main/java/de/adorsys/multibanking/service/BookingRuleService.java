package de.adorsys.multibanking.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.adorsys.docusafe.business.types.complex.DSDocument;
import org.adorsys.docusafe.business.types.complex.DocumentFQN;
import org.adorsys.docusafe.business.types.complex.UserIDAuth;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;

import de.adorsys.multibanking.domain.CustomRuleEntity;
import de.adorsys.multibanking.domain.RuleEntity;
import de.adorsys.multibanking.service.base.BaseService;
import de.adorsys.multibanking.utils.FQNUtils;
import de.adorsys.multibanking.utils.IdFactory;

@Service
public class BookingRuleService extends BaseService {
	/*
	 * We use this for the static and custom rules. Caller must pass the correct userIdAuth
	 */
	private static final DocumentFQN bookingRulesFQN = FQNUtils.bookingRulesFQN();

    public DSDocument getCustomBookingRules() {
    	return loadDocument(userIDAuth, bookingRulesFQN);
    }

	public void createOrUpdateCustomRule(CustomRuleEntity ruleEntity) {
		List<CustomRuleEntity> rules = loadPersistentRules(userIDAuth, CustomRuleEntity.class);
		createOrUpdateRuleInternal(rules, ruleEntity, new CustomRuleEntity());		
		store(userIDAuth, bookingRulesFQN, rules);
		// TODO Reset rules provider
        // TODO fire rules change event. A local reset will not help the distributed environment.
	}
	
	public void createOrUpdateCustomRules(List<CustomRuleEntity> ruleEntities) {
		List<CustomRuleEntity> persistent = loadPersistentRules(userIDAuth, CustomRuleEntity.class);
		ruleEntities.stream().forEach(r -> createOrUpdateRuleInternal(persistent, r, new CustomRuleEntity()));
		store(userIDAuth, bookingRulesFQN, persistent);
	}
	
	public void replceCustomRules(List<CustomRuleEntity> ruleEntities) {
		List<CustomRuleEntity> persistent = new ArrayList<>();
		ruleEntities.stream().forEach(r -> createOrUpdateRuleInternal(persistent, r, new CustomRuleEntity()));
		store(userIDAuth, bookingRulesFQN, persistent);
	}

	public void deleteCustomRule(String ruleId) {
		deleteRule(userIDAuth, ruleId, CustomRuleEntity.class);
	}
	public void deleteCustomRules(List<String> ruleIds) {
		deleteRules(userIDAuth, ruleIds, CustomRuleEntity.class);
	}

    /**
	 * Static rules a stored in the system storage and is accessible to all users.
     * 
     * @return
     */
	public DSDocument getStaticBookingRules() {
    	return loadDocument(systemIDAuth.getUserIDAuth(), bookingRulesFQN);
	}

	/**
	 * Static rules a stored in the system storage and is accessible to all users.
	 * 
	 * @param ruleEntity
	 */
	public void createOrUpdateStaticRule(RuleEntity ruleEntity) {
		List<RuleEntity> rules = loadPersistentRules(systemIDAuth.getUserIDAuth(), RuleEntity.class);
		createOrUpdateRuleInternal(rules, ruleEntity, new RuleEntity());		
		store(systemIDAuth.getUserIDAuth(), bookingRulesFQN, rules);
	}
	
	public void createOrUpdateStaticRules(List<RuleEntity> ruleEntities) {
		List<RuleEntity> rules = loadPersistentRules(userIDAuth, RuleEntity.class);
		ruleEntities.stream().forEach(r -> createOrUpdateRuleInternal(rules, r, new RuleEntity()));
		store(systemIDAuth.getUserIDAuth(), bookingRulesFQN, rules);
	}
	
	public void replceStaticRules(List<RuleEntity> ruleEntities) {
		List<RuleEntity> persistent = new ArrayList<>();
		ruleEntities.stream().forEach(r -> createOrUpdateRuleInternal(persistent, r, new RuleEntity()));
		store(userIDAuth, bookingRulesFQN, persistent);
	}
	public void deleteStaticRule(String ruleId) {
		deleteRule(systemIDAuth.getUserIDAuth(), ruleId, RuleEntity.class);
	}

	public void deleteStaticRules(List<String> ruleIds) {
		deleteRules(systemIDAuth.getUserIDAuth(), ruleIds, RuleEntity.class);
	}
	
	/*
	 * Creating or updating a rule, independently on whether Custom or static. T is the rule type
	 *  
	 * @param persistentList : list from the storage. Final.
	 * @param ruleToUpdate
	 * @param newInstance :  instance type to be created. BeanUtils copy will only take field provided by the class.
	 * Ignoring additional field. Like when we use a custom rule to create a static rule.
	 */
	private <T extends RuleEntity> void createOrUpdateRuleInternal(final List<T> persistentList, T ruleToUpdate, T newInstance){
		// Find the rule or return the new instance
		T found = persistentList.stream().filter(r -> StringUtils.equals(r.getId(), ruleToUpdate.getId())).findFirst().orElse(newInstance);		

		// Object identity to new instance means Rules was not found. 
		// we add this to the collection
		if(found==newInstance)persistentList.add(found);
		
		// Update the rule
		BeanUtils.copyProperties(ruleToUpdate, found);
		// Normalize creditor id
		ruleToUpdate.setCreditorId(StringUtils.removeAll(ruleToUpdate.getCreditorId(), StringUtils.EMPTY));
		ruleToUpdate.updateSearchIndex();

		// If ruleToUpdate had no Id, then set one.
		if(StringUtils.isBlank(ruleToUpdate.getId()))ruleToUpdate.setId(IdFactory.uuid());
	}
	
	private <T extends RuleEntity> List<T> loadPersistentRules(UserIDAuth auth, Class<T> klass){
		// Load persistent collection.
		List<T> rules = load(auth, bookingRulesFQN, new TypeReference<List<T>>() {});
		if(rules==null)rules=Collections.emptyList();
		return rules;
	}

	private <T extends RuleEntity> void deleteRules(UserIDAuth user, List<String> ruleIds, Class<T> ruleKlass) {
		List<T> persistentRules = loadPersistentRules(user, ruleKlass);
		int size = persistentRules.size();
		ruleIds.stream().forEach(ruleId -> deleteRuleInternal(persistentRules, ruleId, ruleKlass));
		if(persistentRules.size()!=size)// At least one was deleted
			store(user, bookingRulesFQN, persistentRules);
	}
	
	private <T extends RuleEntity> void deleteRule(UserIDAuth user, String ruleId, Class<T> ruleKlass) {
		List<T> persistentRules = loadPersistentRules(user, ruleKlass);
		if(deleteRuleInternal(persistentRules, ruleId, ruleKlass))store(user, bookingRulesFQN, persistentRules);
	}

	private <T extends RuleEntity> boolean deleteRuleInternal(final List<T> persistentRules, String ruleId, Class<T> ruleKlass) {
		T ruleEntity = persistentRules.stream().filter(r -> StringUtils.equalsAnyIgnoreCase(ruleId, r.getId())).findFirst().orElse(null);
		if(ruleEntity!=null){
			return persistentRules.remove(ruleEntity);
		}
		return false;
	}
}
