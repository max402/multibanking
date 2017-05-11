package de.adorsys.multibanking.domain;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

/**
 * Store a bank access encrypted.
 * 
 * @author fpo
 *
 */
@Data
@Document
public class EncryptedBankAccessEntity extends EncData {
    @Indexed
    private String userId;
}
