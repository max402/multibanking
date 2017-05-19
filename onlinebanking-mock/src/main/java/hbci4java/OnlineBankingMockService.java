package hbci4java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import domain.BankAccess;
import domain.BankAccount;
import domain.Booking;

@Service
public class OnlineBankingMockService implements OnlineBankingService {
	
	// Bank accounts by Login
	private Map<String, List<ExtBankAccount>> accounts = new HashMap<>();
	
	// Booking by id
	//private Map<String, ExtBooking> bookings = new HashMap<>();
	
	// Bookings by accountId
	private Map<String, List<ExtBooking>> bookings = new HashMap<>();
	
	@Override
	public List<BankAccount> loadBankAccounts(BankAccess bankAccess, String pin) {
		String bankLogin = bankAccess.getBankLogin();
		List<BankAccount> result = new ArrayList<>();
		List<ExtBankAccount> list = accounts.get(bankLogin);
		if(list==null) throw new IllegalArgumentException("Bank access not found");
		
		list.forEach(extBankAccount -> {
			BankAccount bankAccount = new BankAccount();
			BeanUtils.copyProperties(extBankAccount, bankAccount);
			result.add(bankAccount);
		});
		return result;
	}

	@Override
	public List<Booking> loadBookings(BankAccess bankAccess, BankAccount bankAccount, String pin) {
		String bankLogin = bankAccess.getBankLogin();		
		List<ExtBankAccount> list = accounts.get(bankLogin);
		if(list==null) throw new IllegalArgumentException("Bank access not found");
		
		ExtBankAccount bc = null;
		for (ExtBankAccount extBankAccount : list) {
			if(getAccountUniqueId(extBankAccount).equals(getAccountUniqueId(bankAccount))){
				bc = extBankAccount;
				break;
			}
		}
		if(bc==null) throw new IllegalArgumentException("Bank account not found");
		List<Booking> result = new ArrayList<>();
		List<ExtBooking> extBookingsList = bookings.get(bc.getAccountId());
		
		extBookingsList.forEach(extBooking -> {
			Booking booking = new Booking();
			BeanUtils.copyProperties(extBooking, booking);
			result.add(booking);
		});
		
		return result;
	}

	public void addBankAccounts(List<BankAccount> accountsList, BankAccess bankAccess) {
		List<ExtBankAccount> extAccountsList = new ArrayList<>();
		for (BankAccount bankAccount : accountsList) {
			ExtBankAccount extBankAccount = new ExtBankAccount();
			BeanUtils.copyProperties(bankAccount, extBankAccount);
			extBankAccount.setAccountId(getAccountUniqueId(bankAccount));
			extAccountsList.add(extBankAccount);
		}
		accounts.put(bankAccess.getBankLogin(), extAccountsList);
	}

	public void addBookings(List<Booking> bookingsList, BankAccess bankAccess, BankAccount bankAccount) {
		String bankLogin = bankAccess.getBankLogin();		
		List<ExtBankAccount> list = accounts.get(bankLogin);
		if(list==null) throw new IllegalArgumentException("Bank access not found");
		
		ExtBankAccount bc = null;
		for (ExtBankAccount extBankAccount : list) {
			if(getAccountUniqueId(extBankAccount).equals(getAccountUniqueId(bankAccount))){
				bc = extBankAccount;
				break;
			}
		}
		if(bc==null) throw new IllegalArgumentException("Bank account not found");
		List<ExtBooking> extBookingsList = new ArrayList<>();
		for (Booking booking : bookingsList) {
			ExtBooking extBooking = new ExtBooking();
			BeanUtils.copyProperties(booking, extBooking);
			extBooking.setAccountId(bc.getAccountId());
			extBooking.setId(bc.getAccountId() + "#" + booking.getExternalId());
			extBookingsList.add(extBooking);
		}
		bookings.put(bc.getAccountId(), extBookingsList);
	}
	
	public static String getAccountUniqueId(BankAccount bac){
		return bac.getIbanHbciAccount() + "_" + bac.getNumberHbciAccount();
	}
}
