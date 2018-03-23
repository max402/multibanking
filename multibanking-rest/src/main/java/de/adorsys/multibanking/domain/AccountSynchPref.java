package de.adorsys.multibanking.domain;

import lombok.Data;

/**
 * Hold preferences on how to synch and split booking lists in to files. This preference information
 * can be stored at many contexts:
 * - user
 * - bank access
 * - bank account
 * 
 * The most specific one always applys.
 * 
 * By default we create one for the bank access level. In order to modify the synch behavior of a single
 * bank account, we can create a synchPreference for that account.
 * 
 * 
 * @author fpo
 *
 */
@Data
public class AccountSynchPref {
	
	/*
	 *  Default preference is per year.
	 */
	private BookingPeriod bookingPeriod = BookingPeriod.YEAR;
	
}
