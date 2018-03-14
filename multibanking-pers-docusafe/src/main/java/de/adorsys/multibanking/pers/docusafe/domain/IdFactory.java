package de.adorsys.multibanking.pers.docusafe.domain;

import java.util.UUID;

public class IdFactory {
	public static final String uuid(){
		return UUID.randomUUID().toString();
	}
}
