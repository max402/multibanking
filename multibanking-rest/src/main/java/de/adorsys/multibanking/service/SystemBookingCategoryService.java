package de.adorsys.multibanking.service;

import java.util.Collections;
import java.util.List;

import org.adorsys.docusafe.business.types.complex.DSDocument;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;

import de.adorsys.multibanking.domain.CategoryEntity;
import de.adorsys.multibanking.service.base.BaseSystemIdService;
import de.adorsys.multibanking.service.helper.CategoryUtils;

/**
 * TODO Reset rules provider after every change.
 * 
 * @author fpo 2018-03-24 04:34
 *
 */
@Service
public class SystemBookingCategoryService extends BaseSystemIdService {

    /**
	 * Static rules a stored in the system storage and is accessible to all users.
     * 
     * @return
     */
	public DSDocument getStaticBookingCategories() {
    	return loadDocument(userIDAuth(), CategoryUtils.bookingCategoriesFQN);
	}

	/**
	 * Static categories a stored in the system storage and is accessible to all users.
	 * 
	 * @param categoryEntity
	 */
	public void createOrUpdateStaticCategory(CategoryEntity categoryEntity) {
		updateList(Collections.singletonList(categoryEntity), CategoryEntity.class, listType(), 
				CategoryUtils.bookingCategoriesFQN, userIDAuth());
	}
	
	public void createOrUpdateStaticCategories(List<CategoryEntity> categoryEntities) {
		updateList(categoryEntities, CategoryEntity.class, listType(), CategoryUtils.bookingCategoriesFQN, userIDAuth());
	}
	
	public void replceStaticCategories(List<CategoryEntity> categoryEntities) {
		replaceList(categoryEntities, CategoryEntity.class, CategoryUtils.bookingCategoriesFQN, userIDAuth());
	}
	public boolean deleteStaticCategory(String categoryId) {
		return deleteStaticCategories(Collections.singletonList(categoryId));
	}
	public boolean deleteStaticCategories(List<String> categoryIds) {
		return deleteListById(categoryIds, CategoryEntity.class, listType(), CategoryUtils.bookingCategoriesFQN, userIDAuth())>0;
	}
	
	private static TypeReference<List<CategoryEntity>> listType(){
		return new TypeReference<List<CategoryEntity>>() {};
	}
}
