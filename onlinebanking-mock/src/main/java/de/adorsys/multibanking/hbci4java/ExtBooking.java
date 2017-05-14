package de.adorsys.multibanking.hbci4java;

import domain.Booking;

public class ExtBooking extends Booking {
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
}
