package de.adorsys.multibanking.domain;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

/**
 * An encrypted bank account record.
 * 
 * @author fpo
 *
 */
@Data
@Document
public class EncryptedBankAccountEntity extends EncData {
    @Indexed
    private String bankAccessId;
}
