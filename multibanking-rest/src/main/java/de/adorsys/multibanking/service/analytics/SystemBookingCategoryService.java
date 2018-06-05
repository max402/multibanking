package de.adorsys.multibanking.service.analytics;

import java.util.List;

import org.adorsys.docusafe.business.DocumentSafeService;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.adorsys.multibanking.auth.SystemContext;
import de.adorsys.multibanking.auth.UserObjectPersistenceService;
import de.adorsys.multibanking.domain.CategoryEntity;
import de.adorsys.multibanking.service.helper.BookingCategoryServiceTemplate;

/**
 * TODO Reset rules provider after every change.
 * 
 * @author fpo 2018-03-24 04:34
 *
 */
@Service
public class SystemBookingCategoryService extends BookingCategoryServiceTemplate<CategoryEntity>{
	private UserObjectPersistenceService sos;
	public SystemBookingCategoryService(ObjectMapper objectMapper, SystemContext systemContext, DocumentSafeService documentSafeService) {
        this.sos = new UserObjectPersistenceService(systemContext.getUser(), objectMapper, documentSafeService);
    }

    @Override
	protected UserObjectPersistenceService cbs() {
		return sos;
	}

	@Override
	protected TypeReference<List<CategoryEntity>> listType() {
		return new TypeReference<List<CategoryEntity>>() {};
	}
}
