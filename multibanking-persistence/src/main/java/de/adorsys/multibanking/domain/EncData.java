package de.adorsys.multibanking.domain;

import org.springframework.data.annotation.Id;

import lombok.Data;

@Data
public class EncData {
    /*Access indexes*/
    @Id
    private String id;
	
	/*The encrypted enc data*/
    private byte[] encData;
}
