package org.adorsys.multibanking.onlinebanking.mapper.api.domain;

import domain.BankApiUser;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by alexg on 07.02.17.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UserEntity extends AbstractId {
    private String id;
    private Date expireUser;

    private List<BankApiUser> apiUser = new ArrayList<>();

    public UserEntity id(String id) {
        this.id = id;
        return this;
    }
}
