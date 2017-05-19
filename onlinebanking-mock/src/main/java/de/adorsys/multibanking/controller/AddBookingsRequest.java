package de.adorsys.multibanking.controller;

import java.util.List;

import domain.BankAccess;
import domain.BankAccount;
import domain.Booking;

public class AddBookingsRequest {
	private List<Booking> bookings;
	private BankAccess bankAccess;
	private BankAccount bankAccount;
	public List<Booking> getBookings() {
		return bookings;
	}
	public void setBookings(List<Booking> bookings) {
		this.bookings = bookings;
	}
	public BankAccess getBankAccess() {
		return bankAccess;
	}
	public void setBankAccess(BankAccess bankAccess) {
		this.bankAccess = bankAccess;
	}
	public BankAccount getBankAccount() {
		return bankAccount;
	}
	public void setBankAccount(BankAccount bankAccount) {
		this.bankAccount = bankAccount;
	}
}
