package de.adorsys.multibanking.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.adorsys.multibanking.encrypt.Encrypted;
import domain.BankAccess;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by alexg on 07.02.17.
 */
@Data
@Document
@Encrypted(exclude = {"_id", "userId"})
public class BankAccessEntity extends BankAccess {

    @Id
    private String id;
    @Indexed
    private String userId;
    @JsonIgnore // avoid pins to be serialized into JSON
    private String pin;
    @JsonIgnore // avoid pins to be serialized into JSON
    private String pin2;
    private boolean temporary;
    private boolean storePin;
    private boolean storeBookings;
    private boolean categorizeBookings;
    private boolean storeAnalytics;
    private boolean storeAnonymizedBookings;

    public BankAccessEntity id(String id) {
        this.id = id;
        return this;
    }

    @Override
    @JsonIgnore // avoid pins to be serialized into JSON
    public String getHbciPassportState() {
        return super.getHbciPassportState();
    }
}
