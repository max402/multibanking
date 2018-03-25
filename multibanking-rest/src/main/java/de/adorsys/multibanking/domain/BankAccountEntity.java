package de.adorsys.multibanking.domain;

import de.adorsys.multibanking.domain.common.IdentityIf;
import domain.BankAccount;
import lombok.Data;

/**
 * Created by alexg on 07.02.17.
 */
@Data
public class BankAccountEntity extends BankAccount implements IdentityIf {
    private String id;
    private String bankAccessId;
    private String userId;

    public String getId() {
        return id;
    }

    public BankAccountEntity id(String id) {
        this.id = id;
        return this;
    }

    public BankAccountEntity bankAccessId(String bankAccessId) {
        this.bankAccessId = bankAccessId;
        return this;
    }
}
