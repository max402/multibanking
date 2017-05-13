package hbci4java;

import domain.Booking;

public class ExtBooking extends Booking {
	private String accountId;

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	
}
