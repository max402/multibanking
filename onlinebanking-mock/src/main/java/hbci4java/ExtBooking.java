package hbci4java;

import domain.Booking;

public class ExtBooking extends Booking {
	private String bankLogin;
	private String accountId;
	private String id;

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBankLogin() {
		return bankLogin;
	}

	public void setBankLogin(String bankLogin) {
		this.bankLogin = bankLogin;
	}
}
