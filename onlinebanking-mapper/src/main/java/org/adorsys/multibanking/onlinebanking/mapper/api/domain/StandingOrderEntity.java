package org.adorsys.multibanking.onlinebanking.mapper.api.domain;

import domain.StandingOrder;
import lombok.Data;

/**
 * Created by alexg on 05.09.17.
 */
@Data
public class StandingOrderEntity extends StandingOrder implements IdentityIf {
    private String id;
    private String accountId;
    private String userId;

}
