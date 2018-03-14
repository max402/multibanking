package de.adorsys.multibanking.pers.docusafe.domain;

import org.adorsys.docusafe.business.types.complex.UserIDAuth;

public class SystemIDAuth {
	
	private final UserIDAuth userIDAuth;

	public SystemIDAuth(UserIDAuth userIDAuth) {
		super();
		this.userIDAuth = userIDAuth;
	}

	public UserIDAuth getUserIDAuth() {
		return userIDAuth;
	}
}
