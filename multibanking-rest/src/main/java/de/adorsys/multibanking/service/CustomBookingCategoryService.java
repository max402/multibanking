package de.adorsys.multibanking.service;

import java.util.Collections;
import java.util.List;

import org.adorsys.docusafe.business.types.complex.DSDocument;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;

import de.adorsys.multibanking.domain.CustomCategoryEntity;
import de.adorsys.multibanking.service.base.BaseUserIdService;
import de.adorsys.multibanking.service.helper.CategoryUtils;

/**
 * TODO Reset rules provider after every change.
 * 
 * @author fpo 2018-03-24 04:34
 *
 */
@Service
public class CustomBookingCategoryService extends BaseUserIdService {

    public DSDocument getCustomBookingCategories() {
    	return loadDocument(userIDAuth, CategoryUtils.bookingCategoriesFQN);
    }

	public void createOrUpdateCustomCategory(CustomCategoryEntity categoryEntity) {
		updateList(Collections.singletonList(categoryEntity), CustomCategoryEntity.class, listType(), 
				CategoryUtils.bookingCategoriesFQN, userIDAuth);
	}
	
	public void createOrUpdateCustomCategories(List<CustomCategoryEntity> categoryEntities) {
		updateList(categoryEntities, CustomCategoryEntity.class, listType(), CategoryUtils.bookingCategoriesFQN, userIDAuth);
	}
	
	public void replceCustomCategories(List<CustomCategoryEntity> categoryEntities) {
		replaceList(categoryEntities, CustomCategoryEntity.class, CategoryUtils.bookingCategoriesFQN, userIDAuth);
	}
	
	public boolean deleteCustomCategory(String categoryId) {
		return deleteCustomCategories(Collections.singletonList(categoryId));
	}
	public boolean deleteCustomCategories(List<String> categoryIds) {
		return deleteListById(categoryIds, CustomCategoryEntity.class, listType(), 
				CategoryUtils.bookingCategoriesFQN, userIDAuth)>0;
	}

	private static TypeReference<List<CustomCategoryEntity>> listType(){
		return new TypeReference<List<CustomCategoryEntity>>() {};
	}
}
