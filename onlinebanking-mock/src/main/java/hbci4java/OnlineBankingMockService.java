package hbci4java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import domain.BankAccess;
import domain.BankAccount;
import domain.Booking;

public class OnlineBankingMockService implements OnlineBankingService {
	
	// Bank accounts by Login
	private Map<String, List<ExtBankAccount>> accounts = new HashMap<>();
	
	private Map<String, ExtBooking> bookings = new HashMap<>();
	
	@Override
	public List<BankAccount> loadBankAccounts(BankAccess bankAccess, String pin) {
		String bankLogin = bankAccess.getBankLogin();
		List<BankAccount> result = new ArrayList<>();
		List<ExtBankAccount> list = accounts.get(bankLogin);
		for (ExtBankAccount extBankAccount : list) {
			
		}
		return result;
	}

	@Override
	public List<Booking> loadBookings(BankAccess bankAccess, BankAccount bankAccount, String pin) {
		return null;
	}

	public void addBankAccounts(List<BankAccount> accounts, BankAccess bankAccess) {
	}

	public void addBookings(List<Booking> bookings, BankAccess bankAccess, BankAccount bankAccount) {
	}
}
