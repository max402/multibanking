package de.adorsys.multibanking.utils;

import java.util.UUID;

public class IdFactory {
	public static final String uuid(){
		return UUID.randomUUID().toString();
	}
}
