package de.adorsys.multibanking.utils;

import org.adorsys.docusafe.business.types.complex.DocumentDirectoryFQN;
import org.adorsys.docusafe.business.types.complex.DocumentFQN;

public class FQNUtils {

	public static final DocumentFQN banksFQN(){
		return new DocumentFQN("banks");
	}
	
	public static final DocumentFQN userDataFQN(){
		return new DocumentFQN("userData.aes");
	}
//
//	public static final DocumentFQN userFQN(){
//		return new DocumentFQN("user.aes");
//	}
	
	private static final DocumentDirectoryFQN bankAccessesDirFQN() {
		return new DocumentDirectoryFQN("bankaccesses");
	}

//	public static final DocumentFQN bankAccessListFQN() {
//		return bankAccessesDirFQN().addName("bankaccesses.aes");
//	}

	public static final DocumentDirectoryFQN bankAccessDirFQN(String bankAccessId) {
		return bankAccessesDirFQN().addDirectory(bankAccessId);
	}

	public static final DocumentFQN credentialFQN(String bankAccessId) {
    	return bankAccessDirFQN(bankAccessId).addName("credentials.aes");
	}
	
	public static final DocumentDirectoryFQN bankAccountsDirFQN(String bankAccessId) {
    	return bankAccessDirFQN(bankAccessId).addDirectory("accounts");
	}

//	public static final DocumentFQN bankAccountsFileFQN(String bankAccessId) {
//    	return bankAccountsDirFQN(bankAccessId).addName("accounts.aes");
//	}
	private static final DocumentDirectoryFQN bankAccountDirFQN(String bankAccessId, String accountId) {
    	return bankAccountsDirFQN(bankAccessId).addDirectory(accountId);
	}

	private static final DocumentDirectoryFQN bankAccountPeriodFQN(String bankAccessId, String accountId, String period) {
    	return bankAccountDirFQN(bankAccessId, accountId).addDirectory(period);
	}
	
	public static DocumentFQN bookingFQN(String accessId, String accountId, String period) {
    	return bankAccountPeriodFQN(accessId, accountId, period).addName("bookings.aes");
	}

	public static DocumentFQN analyticsFQN(String accessId, String accountId) {
    	return bankAccountDirFQN(accessId, accountId).addName("analytics.aes");
	}
	
	public static DocumentFQN contractsFQN(String accessId, String accountId) {
    	return bankAccountDirFQN(accessId, accountId).addName("contracts.aes");
	}
	
	public static DocumentFQN standingOrdersFQN(String accessId, String accountId) {
    	return bankAccountDirFQN(accessId, accountId).addName("standingOrders.aes");
	}
	
//	public static DocumentFQN accountSynchResultFQN(String accessId, String accountId) {
//    	return bankAccountDirFQN(accessId, accountId).addName("synchResult.aes");
//	}
	
//	public static DocumentFQN accountLevelSynchPrefFQN(String accessId, String accountId) {
//    	return bankAccountDirFQN(accessId, accountId).addName("synchPref.aes");
//	}
	
//	public static DocumentFQN accessLevelSynchPrefFQN(String accessId) {
//    	return bankAccessDirFQN(accessId).addName("synchPref.aes");
//	}

//	public static DocumentFQN userLevelSynchPrefFQN() {
//		return new DocumentFQN("synchPref.aes");
//	}

	public static DocumentFQN bookingRulesFQN() {
		return new DocumentFQN("bookingRules.aes");
	}
	public static DocumentFQN bookingCategoriesFQN() {
		return new DocumentFQN("bookingCategories.aes");
	}

	private static final DocumentDirectoryFQN imagesDirFQN() {
		return new DocumentDirectoryFQN("images");
	}

	public static DocumentFQN imageFQN(String imageName) {
		return imagesDirFQN().addName(imageName);
	}

	public static DocumentFQN paymentsFQN(String accessId, String accountId) {
    	return bankAccountDirFQN(accessId, accountId).addName("payments.aes");
	}

	public static DocumentFQN anonymizedBookingFQN(String accessId, String accountId) {
    	return bankAccountDirFQN(accessId, accountId).addName("anonymizedBookings.aes");
	}

	public static DocumentDirectoryFQN expireDirFQN() {
		return new DocumentDirectoryFQN("users").addDirectory("expiry");
	}
	public static DocumentFQN expireDayFileFQN(String dayDirName) {
		return expireDirFQN().addName(dayDirName);
	}
}
