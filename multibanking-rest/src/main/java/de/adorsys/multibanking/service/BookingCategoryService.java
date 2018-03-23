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

import de.adorsys.multibanking.domain.CategoryEntity;
import de.adorsys.multibanking.domain.CustomCategoryEntity;
import de.adorsys.multibanking.service.base.BaseService;
import de.adorsys.multibanking.utils.FQNUtils;
import de.adorsys.multibanking.utils.IdFactory;

@Service
public class BookingCategoryService extends BaseService {
	/*
	 * We use this for the static and custom categories. Caller must pass the correct userIdAuth
	 */
	private static final DocumentFQN bookingCategoriesFQN = FQNUtils.bookingCategoriesFQN();

    public DSDocument getCustomBookingCategories() {
    	return loadDocument(userIDAuth, bookingCategoriesFQN);
    }

	public void createOrUpdateCustomCategory(CustomCategoryEntity categoryEntity) {
		List<CustomCategoryEntity> categories = loadPersistentCategories(userIDAuth, CustomCategoryEntity.class);
		createOrUpdateCategoriesInternal(categories, categoryEntity, new CustomCategoryEntity());		
		store(userIDAuth, bookingCategoriesFQN, categories);
		// TODO Reset rules provider
	}
	
	public void createOrUpdateCustomCategories(List<CustomCategoryEntity> categoryEntities) {
		List<CustomCategoryEntity> persistent = loadPersistentCategories(userIDAuth, CustomCategoryEntity.class);
		categoryEntities.stream().forEach(r -> createOrUpdateCategoriesInternal(persistent, r, new CustomCategoryEntity()));
		store(userIDAuth, bookingCategoriesFQN, persistent);
	}
	
	public void replceCustomCategories(List<CustomCategoryEntity> categoryEntities) {
		List<CustomCategoryEntity> persistent = new ArrayList<>();
		categoryEntities.stream().forEach(r -> createOrUpdateCategoriesInternal(persistent, r, new CustomCategoryEntity()));
		store(userIDAuth, bookingCategoriesFQN, persistent);
	}
	
	public void deleteCustomCategory(String categoryId) {
		deleteCategory(userIDAuth, categoryId, CustomCategoryEntity.class);
	}
	public void deleteCustomCategories(List<String> categoryIds) {
		deleteCategories(userIDAuth, categoryIds, CustomCategoryEntity.class);
	}

    /**
	 * Static rules a stored in the system storage and is accessible to all users.
     * 
     * @return
     */
	public DSDocument getStaticBookingCategories() {
    	return loadDocument(systemIDAuth.getUserIDAuth(), bookingCategoriesFQN);
	}

	/**
	 * Static categories a stored in the system storage and is accessible to all users.
	 * 
	 * @param categoryEntity
	 */
	public void createOrUpdateStaticCategory(CategoryEntity categoryEntity) {
		List<CategoryEntity> categories = loadPersistentCategories(systemIDAuth.getUserIDAuth(), CategoryEntity.class);
		createOrUpdateCategoriesInternal(categories, categoryEntity, new CategoryEntity());		
		store(systemIDAuth.getUserIDAuth(), bookingCategoriesFQN, categories);
	}
	
	public void createOrUpdateStaticCategories(List<CategoryEntity> categoryEntities) {
		List<CategoryEntity> categories = loadPersistentCategories(userIDAuth, CategoryEntity.class);
		categoryEntities.stream().forEach(r -> createOrUpdateCategoriesInternal(categories, r, new CategoryEntity()));
		store(systemIDAuth.getUserIDAuth(), bookingCategoriesFQN, categories);
	}
	
	public void replceStaticCategories(List<CategoryEntity> categoryEntities) {
		List<CategoryEntity> persistent = new ArrayList<>();
		categoryEntities.stream().forEach(r -> createOrUpdateCategoriesInternal(persistent, r, new CategoryEntity()));
		store(userIDAuth, bookingCategoriesFQN, persistent);
	}
	public void deleteStaticCategory(String categoryId) {
		deleteCategory(systemIDAuth.getUserIDAuth(), categoryId, CategoryEntity.class);
	}
	public void deleteStaticCategories(List<String> categoryIds) {
		deleteCategories(systemIDAuth.getUserIDAuth(), categoryIds, CategoryEntity.class);
	}
	
	
	/*
	 * Creating or updating a category, independently on whether Custom or static. T is the category type
	 *  
	 * @param persistentList : list from the storage. Final.
	 * @param categoryToUpdate
	 * @param newInstance :  instance type to be created. BeanUtils copy will only take field provided by the class.
	 * Ignoring additional field. Like when we use a custom category to create a static category.
	 */
	private <T extends CategoryEntity> void createOrUpdateCategoriesInternal(final List<T> persistentList, T categoryUpdate, T newInstance){
		// Find the category or return the new instance
		T found = persistentList.stream().filter(r -> StringUtils.equals(r.getId(), categoryUpdate.getId())).findFirst().orElse(newInstance);		

		// Object identity to new instance means category was not found. 
		// we add this to the collection
		if(found==newInstance)persistentList.add(found);
		
		// Update the category
		BeanUtils.copyProperties(categoryUpdate, found);

		// If categoryToUpdate had no Id, then set one.
		if(StringUtils.isBlank(categoryUpdate.getId()))categoryUpdate.setId(IdFactory.uuid());
	}
	
	private <T extends CategoryEntity> List<T> loadPersistentCategories(UserIDAuth auth, Class<T> klass){
		// Load persistent collection.
		List<T> categories = load(auth, bookingCategoriesFQN, new TypeReference<List<T>>() {});
		if(categories==null)categories=Collections.emptyList();
		return categories;
	}

	private <T extends CategoryEntity> void deleteCategories(UserIDAuth user, List<String> categoryIds, Class<T> categoryKlass) {
		List<T> persistentCategories = loadPersistentCategories(user, categoryKlass);
		int size = persistentCategories.size();
		categoryIds.stream().forEach(categoryId -> deleteRuleInternal(persistentCategories, categoryId, categoryKlass));
		if(persistentCategories.size()!=size)// At least one was deleted
			store(user, bookingCategoriesFQN, persistentCategories);
	}
	
	private <T extends CategoryEntity> void deleteCategory(UserIDAuth user, String categoryId, Class<T> categoryKlass) {
		List<T> persistentCategories = loadPersistentCategories(user, categoryKlass);
		if(deleteRuleInternal(persistentCategories, categoryId, categoryKlass))store(user, bookingCategoriesFQN, persistentCategories);
	}

	private <T extends CategoryEntity> boolean deleteRuleInternal(final List<T> persistentCategories, String categoryId, Class<T> categoryKlass) {
		T categoryEntity = persistentCategories.stream().filter(r -> StringUtils.equalsAnyIgnoreCase(categoryId, r.getId())).findFirst().orElse(null);
		if(categoryEntity!=null){
			return persistentCategories.remove(categoryEntity);
		}
		return false;
	}
	
}
