package de.adorsys.multibanking.web;

import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.adorsys.docusafe.business.types.complex.DSDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.adorsys.multibanking.domain.CategoryEntity;
import de.adorsys.multibanking.domain.CustomCategoryEntity;
import de.adorsys.multibanking.exception.InvalidCategoriesException;
import de.adorsys.multibanking.service.BookingCategoryService;
import de.adorsys.multibanking.web.common.BaseController;

/**
 * @author fpo 2018-03-20 11:07
 */
@UserResource
@RestController
@RequestMapping(path = "api/v1/analytics/categories")
public class BookingCategoryController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(BookingCategoryController.class);
    private static final ObjectMapper YAML_OBJECT_MAPPER = yamlObjectMapper();

    @Autowired
    private BookingCategoryService bookingCategoryService;
    
    @PostConstruct

    @RequestMapping(method = RequestMethod.POST)
    public HttpEntity<Void> createCategory(@RequestBody CustomCategoryEntity categoryEntity) {
        bookingCategoryService.createOrUpdateCustomCategory(categoryEntity);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/custom", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<ByteArrayResource> getCustomCategories() {
        DSDocument dsDocument = bookingCategoryService.getCustomBookingCategories();
    	return loadBytesForWeb(dsDocument);
    }

    @RequestMapping(value = "/static", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<ByteArrayResource> getStaticCategories() {
        DSDocument dsDocument = bookingCategoryService.getStaticBookingCategories();
    	return loadBytesForWeb(dsDocument);
    }

    @RequestMapping(value = "/custom/{categoryId}", method = RequestMethod.PUT)
    public HttpEntity<Void> updateCustomCategory(@PathVariable String categoryId, @RequestBody CustomCategoryEntity categoryEntity) {
    	categoryEntity.setId(categoryId);
    	bookingCategoryService.createOrUpdateCustomCategory(categoryEntity);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/static/{categoryId}", method = RequestMethod.PUT)
    public HttpEntity<Void> updateCategory(@PathVariable String categoryId, @RequestBody CategoryEntity categoryEntity) {
    	categoryEntity.setId(categoryId);
    	bookingCategoryService.createOrUpdateStaticCategory(categoryEntity);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(path = "/custom", method = RequestMethod.PUT)
    public HttpEntity<Void> createOrUpdateCustomCategories(@RequestBody List<CustomCategoryEntity> categoryEntities) {
    	bookingCategoryService.createOrUpdateCustomCategories(categoryEntities);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(path = "/static", method = RequestMethod.PUT)
    public HttpEntity<Void> createOrUpdateStaticCategories(@RequestBody List<CategoryEntity> categoryEntities) {
    	bookingCategoryService.createOrUpdateStaticCategories(categoryEntities);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    @RequestMapping(path = "/custom/upload", method = RequestMethod.PUT, consumes=MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public HttpEntity<?> uploadReplaceCustomCategories(@RequestParam MultipartFile catogoriesFile) {
    	
        if (!catogoriesFile.isEmpty())return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("File is empty");

        try {
            List<CustomCategoryEntity> categoryEntities = YAML_OBJECT_MAPPER.readValue(catogoriesFile.getInputStream(), new TypeReference<List<CustomCategoryEntity>>() {});
            bookingCategoryService.replceCustomCategories(categoryEntities);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (IOException e) {
            throw new InvalidCategoriesException(e.getMessage());
        }
    }

    @RequestMapping(path = "/static/upload", method = RequestMethod.PUT, consumes=MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public HttpEntity<?> uploadReplaceStaticCategories(@RequestParam MultipartFile categoriesFile) {
    	
        if (!categoriesFile.isEmpty())return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("File is empty");

        try {
            List<CategoryEntity> rulesEntities = YAML_OBJECT_MAPPER.readValue(categoriesFile.getInputStream(), new TypeReference<List<CategoryEntity>>() {});
            bookingCategoryService.replceStaticCategories(rulesEntities);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (IOException e) {
            throw new InvalidCategoriesException(e.getMessage());
        }
    }

    @RequestMapping(value = "/custom/{categoryId}", method = RequestMethod.DELETE)
    public HttpEntity<Void> deleteCustomCategory(@PathVariable String categoryId) {
        bookingCategoryService.deleteCustomCategory(categoryId);
        log.info("Category [{}] deleted.", categoryId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @RequestMapping(value = "/static/{categoryId}", method = RequestMethod.DELETE)
    public HttpEntity<Void> deleteStaticCategory(@PathVariable String categoryId) {
        bookingCategoryService.deleteStaticCategory(categoryId);
        log.info("Category [{}] deleted.", categoryId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/custom", method = RequestMethod.DELETE)
    public HttpEntity<Void> deleteCustomCategories(@PathVariable List<String> categoryIds) {
        bookingCategoryService.deleteCustomCategories(categoryIds);
        log.info("Category [{}] deleted.", categoryIds);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @RequestMapping(value = "/static", method = RequestMethod.DELETE)
    public HttpEntity<Void> deleteStaticCategories(@PathVariable List<String> categoryIds) {
        bookingCategoryService.deleteStaticCategories(categoryIds);
        log.info("Category [{}] deleted.", categoryIds);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
