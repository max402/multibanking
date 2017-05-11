package de.adorsys.multibanking.domain;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

/**
 * 
 * @author fpo
 *
 */
@Data
@Document
public class EncryptedAccountAnalyticsEntity extends EncData {
    @Indexed
    private String accountId;
}
