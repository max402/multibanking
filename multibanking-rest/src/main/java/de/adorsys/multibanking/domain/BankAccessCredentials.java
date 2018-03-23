package de.adorsys.multibanking.domain;

import java.util.Date;

import org.springframework.beans.BeanUtils;

import lombok.Data;

@Data
public class BankAccessCredentials {

    private String accessId;
    private String userId;
    private String pin;
    private String pin2;
    private String hbciPassportState;
    
    private Boolean pinValid = true;
    private Date lastValidationDate;

    public static final BankAccessCredentials cloneCredentials(final BankAccessEntity e){
    	BankAccessCredentials b = new BankAccessCredentials();
    	BeanUtils.copyProperties(e, b);    	
    	return b;
    }

    public static final void cleanCredentials(final BankAccessEntity e){
    	e.setPin(null);
    	e.setPin2(null);
    	e.setHbciPassportState(null);
    }
}
