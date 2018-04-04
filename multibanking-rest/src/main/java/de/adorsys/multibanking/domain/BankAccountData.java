package de.adorsys.multibanking.domain;

import lombok.Data;

/**
 * Holds data associated with a bank account.
 * 
 * @author fpo
 *
 */
@Data
public class BankAccountData {
	
	private BankAccountEntity bankAccount;
	
	private AccountSynchResult synchResult = new AccountSynchResult();

	private AccountSynchPref accountSynchPref;
}
