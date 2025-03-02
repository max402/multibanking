package de.adorsys.multibanking.jpa.entity;

import de.adorsys.multibanking.domain.AbstractScaTransaction;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "payment_sepa")
@Data
@EqualsAndHashCode(callSuper = false)
public class RawSepaTransactionJpaEntity extends PaymentCommonJpaEntity {

    @Id
    @GeneratedValue
    private Long id;
    @Column(length = 5000)
    private String painXml;
    private String service;
    private AbstractScaTransaction.TransactionType sepaTransactionType;

}
