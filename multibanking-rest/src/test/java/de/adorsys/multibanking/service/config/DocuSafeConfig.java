package de.adorsys.multibanking.service.config;

import org.adorsys.docusafe.business.DocumentSafeService;
import org.adorsys.docusafe.business.impl.DocumentSafeServiceImpl;
import org.adorsys.encobject.filesystem.FileSystemExtendedStorageConnection;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.adorsys.multibanking.service.base.ExceptionHandlingDocumentSafeService;

/**
 * Sample config for the docusafe. Beware of the wrapping for exception handling.
 * @author fpo
 *
 */
@Configuration
public class DocuSafeConfig {
	
	@Bean
	public DocumentSafeService docusafe(){
		FileSystemExtendedStorageConnection extendedStorageConnection = new FileSystemExtendedStorageConnection("target/"+RandomStringUtils.randomAlphanumeric(8));
		return new ExceptionHandlingDocumentSafeService(new DocumentSafeServiceImpl(extendedStorageConnection));
	}
}
