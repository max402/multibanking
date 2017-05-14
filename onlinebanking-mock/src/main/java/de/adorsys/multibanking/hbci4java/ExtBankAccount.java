package de.adorsys.multibanking.hbci4java;

import domain.BankAccount;

public class ExtBankAccount extends BankAccount {
	private String accountId;

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
}
