package de.adorsys.multibanking.domain;

import java.util.Date;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

/**
 * Encrypted Booking Entity
 * 
 * @author fpo
 *
 */
@Data
@Document
@CompoundIndexes({
        @CompoundIndex(name = "booking_unique_index", def = "{'externalId': 1, 'accountId': 1}", unique = true)
})
public class EncryptedBookingEntity extends EncData {
    @Indexed
    private String accountId;
    
    @Indexed
    private String externalId;
    
    /* Used to sort booking by booking date*/
    @Indexed
    private Date bookingDate;
    
    /* Used to sort booking by value date*/
    @Indexed
    private Date valutaDate;
}
