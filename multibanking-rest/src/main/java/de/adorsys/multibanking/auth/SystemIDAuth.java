package de.adorsys.multibanking.auth;

import org.adorsys.docusafe.business.types.complex.UserIDAuth;

public class SystemIDAuth {
	
	private UserIDAuth userIDAuth;

	public SystemIDAuth(UserIDAuth userIDAuth) {
		this.userIDAuth = userIDAuth;
	}

	public UserIDAuth getUserIDAuth() {
		return this.userIDAuth;
	}
}
