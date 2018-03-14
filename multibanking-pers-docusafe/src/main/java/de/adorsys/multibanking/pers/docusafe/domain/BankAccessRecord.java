package de.adorsys.multibanking.pers.docusafe.domain;

import java.util.HashMap;
import java.util.Map;

import de.adorsys.multibanking.domain.BankAccessEntity;
import de.adorsys.multibanking.domain.BankAccountEntity;

public class BankAccessRecord {
	private BankAccessEntity bankAccess;
	private Map<String, BankAccountEntity> bankAccounts = new HashMap<>();

	public BankAccessEntity getBankAccess() {
		return bankAccess;
	}

	public void setBankAccess(BankAccessEntity bankAccess) {
		this.bankAccess = bankAccess;
	}

	public Map<String, BankAccountEntity> getBankAccounts() {
		return bankAccounts;
	}

	public void setBankAccounts(Map<String, BankAccountEntity> bankAccounts) {
		this.bankAccounts = bankAccounts;
	}
}
