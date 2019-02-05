package domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class RawSepaPayment extends AbstractScaTransaction {

    private String painXml;

    @Override
    public TransactionType getTransactionType() {
        return TransactionType.RAW_SEPA;
    }

    @Override
    public String getSepaPain() {
        return painXml;
    }
}