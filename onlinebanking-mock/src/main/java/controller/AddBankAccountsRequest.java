package controller;

import java.util.List;

import domain.BankAccess;
import domain.BankAccount;

public class AddBankAccountsRequest {
	private List<BankAccount> accounts;
	private BankAccess bankAccess;
	public List<BankAccount> getAccounts() {
		return accounts;
	}
	public void setAccounts(List<BankAccount> accounts) {
		this.accounts = accounts;
	}
	public BankAccess getBankAccess() {
		return bankAccess;
	}
	public void setBankAccess(BankAccess bankAccess) {
		this.bankAccess = bankAccess;
	}
}
