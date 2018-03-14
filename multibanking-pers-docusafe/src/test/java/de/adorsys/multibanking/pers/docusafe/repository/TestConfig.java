package de.adorsys.multibanking.pers.docusafe.repository;

import java.lang.reflect.Field;

import org.adorsys.docusafe.business.DocumentSafeService;
import org.adorsys.docusafe.business.impl.DocumentSafeServiceImpl;
import org.adorsys.docusafe.business.types.UserID;
import org.adorsys.docusafe.business.types.complex.UserIDAuth;
import org.adorsys.encobject.domain.ReadKeyPassword;
import org.adorsys.encobject.filesystem.FileSystemExtendedStorageConnection;
import org.adorsys.encobject.service.api.ExtendedStoreConnection;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.adorsys.multibanking.pers.docusafe.common.SystemDocumentRepositoryImpl;
import de.adorsys.multibanking.pers.docusafe.common.UserDocumentRepositoryImpl;
import de.adorsys.multibanking.pers.docusafe.domain.SystemIDAuth;

@Configuration
public class TestConfig {
	
    private static ExtendedStoreConnection extendedStoreConnection;
    private static DocumentSafeService documentSafeService;
    private static ObjectMapper objectMapper;
    
//	private static UserIDAuth userIDAuth;
	private static SystemIDAuth systemIdAuth;

	static {
		String dir = "target/"+RandomStringUtils.randomAlphanumeric(10);
	    extendedStoreConnection = new FileSystemExtendedStorageConnection(dir);
	    documentSafeService = new DocumentSafeServiceImpl(extendedStoreConnection);
	    objectMapper = new ObjectMapper();
	    
//		userIDAuth = new UserIDAuth(new UserID("sampleUser"), new ReadKeyPassword("password4thisSimpleUser"));
		systemIdAuth = new SystemIDAuth(new UserIDAuth(new UserID("systemUser"), new ReadKeyPassword("password4thisSystemUser")));
		
		turnOffEncPolicy();
	}
	
    
//	@Bean
//	public UserIDAuth userIDAuth(){
//		return userIDAuth;
//	}
    
	@Bean
	public SystemIDAuth systemIdAuth() {
		return systemIdAuth;
	}
	
	@Bean
	protected UserDocumentRepositoryImpl userDocumentRepository(){
		return new UserDocumentRepositoryImpl();
	}
	
	@Bean
	protected SystemDocumentRepositoryImpl systemDocumentRepository(){
		return new SystemDocumentRepositoryImpl();
	}
    
	@Bean
	protected DocumentSafeService documentSafeService(){
		return documentSafeService;
	}
	@Bean
	protected ObjectMapper objectMapper(){
		return objectMapper;
	}
	
	@Bean
	public UserRepositoryImpl userRepositoryImpl(){
		return new UserRepositoryImpl();
	}
	
	public static void turnOffEncPolicy() {
		// Warning: do not do this for productive code. Download and install the
		// jce unlimited strength policy file
		// see
		// http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html
		try {
			Field field = Class.forName("javax.crypto.JceSecurity").getDeclaredField("isRestricted");
			field.setAccessible(true);
			field.set(null, java.lang.Boolean.FALSE);
		} catch (ClassNotFoundException | NoSuchFieldException | SecurityException | IllegalArgumentException
				| IllegalAccessException ex) {
			ex.printStackTrace(System.err);
		}
	}
	
}
