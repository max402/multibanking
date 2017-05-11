package de.adorsys.multibanking.domain;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

/**
 * A document holding keys in a keystore.
 * 
 * @author fpo
 *
 */
@Data
@Document
public class KeyStoreEntity extends EncData {
}
