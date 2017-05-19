package de.adorsys.multibanking.hbci4java;

import domain.BankAccount;

public class ExtBankAccount extends BankAccount {
	private String accountId;
	private String bankLogin;

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getBankLogin() {
		return bankLogin;
	}

	public void setBankLogin(String bankLogin) {
		this.bankLogin = bankLogin;
	}
}
