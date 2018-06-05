package de.adorsys.multibanking.service.analytics;

import java.util.List;

import org.adorsys.docusafe.business.DocumentSafeService;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.adorsys.multibanking.auth.UserContext;
import de.adorsys.multibanking.auth.UserObjectPersistenceService;
import de.adorsys.multibanking.domain.CustomRuleEntity;
import de.adorsys.multibanking.service.helper.BookingRuleServiceTemplate;

/**
 * TODO: reset rule provider after update.
 * 		ruleToUpdate.updateSearchIndex();
 * 
 * @author fpo 2018-03-24 01:43
 *
 */
@Service
public class CustomBookingRuleService extends BookingRuleServiceTemplate<CustomRuleEntity> {
    private UserObjectPersistenceService uos;
    public CustomBookingRuleService(UserContext userContext, ObjectMapper objectMapper, DocumentSafeService documentSafeService) {
        this.uos = new UserObjectPersistenceService(userContext, objectMapper, documentSafeService);
    }

    @Override
    protected UserObjectPersistenceService cbs() {
        return uos;
    }

	@Override
	protected TypeReference<List<CustomRuleEntity>> listType() {
		return new TypeReference<List<CustomRuleEntity>>() {};
	}
}
