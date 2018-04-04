package de.adorsys.multibanking.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.adorsys.multibanking.domain.AccountSynchPref;
import de.adorsys.multibanking.domain.AccountSynchResult;
import de.adorsys.multibanking.domain.BankAccessData;
import de.adorsys.multibanking.domain.BankAccountData;
import de.adorsys.multibanking.domain.UserData;
import de.adorsys.multibanking.exception.ResourceNotFoundException;
import de.adorsys.multibanking.service.base.BaseUserIdService;
import domain.BankAccount.SyncStatus;

/**
 * Service to manage account synch preferences.
 * 
 * @author fpo
 *
 */
@Service
public class AccountSynchPrefService extends BaseUserIdService {

	@Autowired
    UserDataService uds;
		
	public AccountSynchPref loadAccountLevelSynchPref(String accessId, String accountId){
		return uds.load().bankAccountData(accessId, accountId).getAccountSynchPref();
	}	
	public void storeAccountLevelSynchPref(String accessId, String accountId, AccountSynchPref pref){
		UserData userData = uds.load();
		userData.bankAccountData(accessId, accountId).setAccountSynchPref(pref);
		uds.store(userData);
	}

	public AccountSynchPref loadAccessLevelSynchPref(String accessId){
		return uds.load().bankAccessData(accessId).getAccountSynchPref();
	}	
	public void storeAccessLevelSynchPref(String accessId, AccountSynchPref pref){
		UserData userData = uds.load();
		userData.bankAccessData(accessId).setAccountSynchPref(pref);
		uds.store(userData);
	}

	public AccountSynchPref loadUserLevelSynchPref(){
		return uds.load().getAccountSynchPref();
	}	
	public void storeUserLevelSynchPref(AccountSynchPref pref){
		UserData userData = uds.load();
		userData.setAccountSynchPref(pref);
		uds.store(userData);
	}
	
	public AccountSynchResult loadAccountSynchResult(String accessId, String accountId) {
		return uds.load().bankAccountData(accessId, accountId).getSynchResult();
	}
	public void storeAccountSynchResult(String accessId, String accountId, AccountSynchResult currentResult) {
		UserData userData = uds.load();
		userData.bankAccountData(accessId, accountId).setSynchResult(currentResult);
		uds.store(userData);
	}
	
	public void updateSyncStatus(String accessId, String accountId, SyncStatus syncStatus) {
		UserData userData = uds.load();
		AccountSynchResult synchResult = userData.bankAccountData(accessId, accountId).getSynchResult();
		synchResult.setSyncStatus(syncStatus);
		synchResult.setStatusTime(LocalDateTime.now());
		uds.store(userData);
	}
	
	/**
	 * Search the neares account synch preference for the given account
	 * @param id
	 * @param id2
	 * @return 
	 */
	public AccountSynchPref findAccountSynchPref(String accessId, String accountId) {
		AccountSynchPref synchPref = loadAccountLevelSynchPref(accessId, accountId);
		if(synchPref==null) 
			synchPref = loadAccessLevelSynchPref(accessId);
		if(synchPref==null) 
			synchPref = loadUserLevelSynchPref();
		if(synchPref==null) 
			synchPref = new AccountSynchPref();
		
		return synchPref;
	}
}
