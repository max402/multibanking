package de.adorsys.multibanking.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;

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
public class AccountSynchPrefService extends BaseUserIdService {
	
	public AccountSynchPref loadAccountLevelSynchPref(String accessId, String accountId){
		return load(FQNUtils.accountLevelSynchPrefFQN(accessId, accountId), synchPrefType())
				.orElse(new AccountSynchPref());
	}	
	public void storeAccountLevelSynchPref(String accessId, String accountId, AccountSynchPref pref){
		store(FQNUtils.accountLevelSynchPrefFQN(accessId, accountId), synchPrefType(), pref);
	}

	public AccountSynchPref loadAccessLevelSynchPref(String accessId){
		return load(FQNUtils.accessLevelSynchPrefFQN(accessId), synchPrefType())
				.orElse(new AccountSynchPref());
	}	
	public void storeAccessLevelSynchPref(String accessId, AccountSynchPref pref){
		store(FQNUtils.accessLevelSynchPrefFQN(accessId), synchPrefType(), pref);
	}

	public AccountSynchPref loadUserLevelSynchPref(){
		return load(FQNUtils.userLevelSynchPrefFQN(), synchPrefType())
				.orElse(new AccountSynchPref());
	}	
	public void storeUserLevelSynchPref(AccountSynchPref pref){
		store(FQNUtils.userLevelSynchPrefFQN(), synchPrefType(), pref);
	}
	
	public AccountSynchResult loadAccountSynchResult(String accessId, String accountId) {
		return load(FQNUtils.accountSynchResultFQN(accessId, accountId), synchResultType())
			.orElse(new AccountSynchResult());
	}
	public void storeAccountSynchResult(String accessId, String accountId, AccountSynchResult currentResult) {
		store(FQNUtils.accountSynchResultFQN(accessId, accountId), synchResultType(), currentResult);
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
		if(documentExists(FQNUtils.accountLevelSynchPrefFQN(accessId, accountId)))
			return loadAccountLevelSynchPref(accessId, accountId);
		
		if(documentExists(FQNUtils.accessLevelSynchPrefFQN(accessId)))
			return loadAccountLevelSynchPref(accessId, accountId);

		if(documentExists(FQNUtils.userLevelSynchPrefFQN()))
			return loadAccountLevelSynchPref(accessId, accountId);
		
		return new AccountSynchPref();
	}

	private static TypeReference<AccountSynchPref> synchPrefType(){
		return new TypeReference<AccountSynchPref>() {};
	}
	
	private static TypeReference<AccountSynchResult> synchResultType(){
		return new TypeReference<AccountSynchResult>() {};
	}
}
