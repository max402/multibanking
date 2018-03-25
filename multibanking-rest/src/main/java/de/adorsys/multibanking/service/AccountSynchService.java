package de.adorsys.multibanking.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import de.adorsys.multibanking.domain.AccountSynchPref;
import de.adorsys.multibanking.domain.AccountSynchResult;
import de.adorsys.multibanking.service.base.BaseUserIdService;
import de.adorsys.multibanking.utils.FQNUtils;
import domain.BankAccount.SyncStatus;

/**
 * Service to manage account synch preferences.
 * 
 * @author fpo
 *
 */
@Service
public class AccountSynchService extends BaseUserIdService {
	
	public AccountSynchPref loadAccountLevelSynchPref(String accessId, String accountId){
		return load(userIDAuth, FQNUtils.accountLevelSynchPrefFQN(accessId, accountId), AccountSynchPref.class)
				.orElse(new AccountSynchPref());
	}	
	public void storeAccountLevelSynchPref(String accessId, String accountId, AccountSynchPref pref){
		store(userIDAuth, FQNUtils.accountLevelSynchPrefFQN(accessId, accountId), pref);
	}

	public AccountSynchPref loadAccessLevelSynchPref(String accessId){
		return load(userIDAuth, FQNUtils.accessLevelSynchPrefFQN(accessId), AccountSynchPref.class)
				.orElse(new AccountSynchPref());
	}	
	public void storeAccessLevelSynchPref(String accessId, AccountSynchPref pref){
		store(userIDAuth, FQNUtils.accessLevelSynchPrefFQN(accessId), pref);
	}

	public AccountSynchPref loadUserLevelSynchPref(){
		return load(userIDAuth, FQNUtils.userLevelSynchPrefFQN(), AccountSynchPref.class)
				.orElse(new AccountSynchPref());
	}	
	public void storeUserLevelSynchPref(AccountSynchPref pref){
		store(userIDAuth, FQNUtils.userLevelSynchPrefFQN(), pref);
	}
	
	public AccountSynchResult loadAccountSynchResult(String accessId, String accountId) {
		return load(userIDAuth, FQNUtils.accountSynchResultFQN(accessId, accountId), AccountSynchResult.class)
			.orElse(new AccountSynchResult());
	}
	public void storeAccountSynchResult(String accessId, String accountId, AccountSynchResult currentResult) {
		store(userIDAuth, FQNUtils.accountSynchResultFQN(accessId, accountId), currentResult);
	}
	public void updateSyncStatus(String accessId, String accountId, SyncStatus syncStatus) {
		AccountSynchResult synchResult = loadAccountSynchResult(accessId, accountId);
		synchResult.setSyncStatus(syncStatus);
		synchResult.setStatusTime(LocalDateTime.now());
		storeAccountSynchResult(accessId, accountId, synchResult);
	}
	
	/**
	 * Search the neares account synch preference for the given account
	 * @param id
	 * @param id2
	 * @return 
	 */
	public AccountSynchPref findAccountSynchPref(String accessId, String accountId) {
		if(documentExists(userIDAuth, FQNUtils.accountLevelSynchPrefFQN(accessId, accountId)))
			return loadAccountLevelSynchPref(accessId, accountId);
		
		if(documentExists(userIDAuth, FQNUtils.accessLevelSynchPrefFQN(accessId)))
			return loadAccountLevelSynchPref(accessId, accountId);

		if(documentExists(userIDAuth, FQNUtils.userLevelSynchPrefFQN()))
			return loadAccountLevelSynchPref(accessId, accountId);
		
		return new AccountSynchPref();
	}

}
