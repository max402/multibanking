package de.adorsys.multibanking.domain;

import de.adorsys.multibanking.domain.common.IdentityIf;
import domain.BankAccess;
import lombok.Data;

/**
 * Created by alexg on 07.02.17.
 */
@Data
public class BankAccessEntity extends BankAccess implements IdentityIf {

    private String id;
    private String userId;
    private String pin;
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
}
