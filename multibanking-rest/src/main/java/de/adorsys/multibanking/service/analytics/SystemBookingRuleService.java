package de.adorsys.multibanking.service.analytics;

import java.util.List;

import org.adorsys.docusafe.business.DocumentSafeService;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.adorsys.multibanking.auth.SystemContext;
import de.adorsys.multibanking.auth.UserObjectPersistenceService;
import de.adorsys.multibanking.domain.RuleEntity;
import de.adorsys.multibanking.service.helper.BookingRuleServiceTemplate;

/**
 * TODO: reset rule provider after update.
 * 		ruleToUpdate.updateSearchIndex();
 * @author fpo
 *
 */
@Service
public class SystemBookingRuleService extends BookingRuleServiceTemplate<RuleEntity>  {
    private UserObjectPersistenceService cbs;
    public SystemBookingRuleService(ObjectMapper objectMapper, SystemContext systemContext, DocumentSafeService documentSafeService) {
        this.cbs = new UserObjectPersistenceService(systemContext.getUser(), objectMapper, documentSafeService);
    }

	@Override
	protected UserObjectPersistenceService cbs() {
		return cbs;
	}

	@Override
	protected TypeReference<List<RuleEntity>> listType() {
		return new TypeReference<List<RuleEntity>>() {};
	}
}
