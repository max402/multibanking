package de.adorsys.multibanking.pers.docusafe.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import de.adorsys.multibanking.domain.CustomRuleEntity;
import de.adorsys.multibanking.domain.RuleEntity;
import de.adorsys.multibanking.pers.docusafe.common.SystemRuleRepositoryImpl;
import de.adorsys.multibanking.pers.spi.repository.BookingRuleRepositoryIf;

public class BookingRuleRepositoryImpl implements BookingRuleRepositoryIf {
	
	@Autowired
	private CustomRuleRepositoryImpl customRuleRepository;
	@Autowired
	private SystemRuleRepositoryImpl systemRuleRepository;

	@Override
	public List<RuleEntity> findByIncoming(boolean incoming) {
		return systemRuleRepository.findByIncoming(incoming);
	}

	@Override
	public List<CustomRuleEntity> findByUserIdAndIncomingCustomRules(String userId, boolean incoming) {
		return customRuleRepository.findByUserIdAndIncomingCustomRules(userId, incoming);
	}

	@Override
	public Page<? extends RuleEntity> findAllPageable(Pageable pageable, boolean custom) {
		if(custom){
			return customRuleRepository.findAllPageable(pageable);
		} else {
			return systemRuleRepository.findAllPageable(pageable);
		}
	}

	@Override
	public List<? extends RuleEntity> findAll(boolean custom) {
		if(custom){
			return customRuleRepository.findAll();
		} else {
			return systemRuleRepository.findAll();
		}
	}

	@Override
	public RuleEntity createOrUpdateCustomRule(CustomRuleEntity ruleEntity) {
		return customRuleRepository.createOrUpdateCustomRule(ruleEntity);
	}

	@Override
	public RuleEntity createOrUpdateRule(RuleEntity ruleEntity) {
		return systemRuleRepository.createOrUpdateRule(ruleEntity);
	}

	@Override
	public List<? extends RuleEntity> search(boolean customRules, String query) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Optional<? extends RuleEntity> getRuleById(boolean customRule, String ruleId) {
		if(customRule){
			return customRuleRepository.getRuleById(ruleId);
		} else {
			return systemRuleRepository.getRuleById(ruleId);
		}
	}

	@Override
	public void deleteCustomRule(String id) {
		customRuleRepository.deleteCustomRule(id);
	}

	@Override
	public void deleteRule(String id) {
		systemRuleRepository.deleteRule(id);
	}

	@Override
	public void replacesRules(List<? extends RuleEntity> rules, boolean custom) {
		if(custom){
			customRuleRepository.replacesRules(rules);
		} else {
			systemRuleRepository.replacesRules(rules);
		}
	}
}
