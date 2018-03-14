package de.adorsys.multibanking.pers.docusafe.domain;

import java.util.HashMap;
import java.util.Map;

public class BankAccessFile {

	private Map<String, BankAccessRecord> bankAccesses = new HashMap<>();

	public Map<String, BankAccessRecord> getBankAccesses() {
		return bankAccesses;
	}

	public void setBankAccesses(Map<String, BankAccessRecord> bankAccesses) {
		this.bankAccesses = bankAccesses;
	}
}
