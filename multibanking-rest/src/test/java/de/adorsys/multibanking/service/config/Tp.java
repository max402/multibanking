package de.adorsys.multibanking.service.config;

public class Tp {
	public static final String p1 = "sts.audience_name=multibanking";
	public static final String p2 = "sts.audience_name=multibanking";
	public static final String p3 = "sts.secret_claim_property_key=user-secret";

	public static final String p4 = "sts.keymanagement.keystore.password=FEDCBA9876543210";
	public static final String p5 = "sts.keymanagement.keystore.name=multibanking-keystore";
	public static final String p6 = "sts.keymanagement.keystore.alias_prefix=multibanking-";
	public static final String p7 = "sts.keymanagement.keystore.type=UBER";
	public static final String p8 = "sts.keymanagement.keystore.keys.encKeyPairs.initialCount=1";
	public static final String p9 = "sts.keymanagement.keystore.keys.encKeyPairs.algo=RSA";
	public static final String p10 = "sts.keymanagement.keystore.keys.encKeyPairs.sigAlgo=SHA256withRSA";
	public static final String p11 = "sts.keymanagement.keystore.keys.encKeyPairs.size=2048";
	public static final String p12 = "sts.keymanagement.keystore.keys.encKeyPairs.name=STS Multibanking";
	public static final String p13 = "sts.keymanagement.keystore.keys.encKeyPairs.validityInterval=3600000";
	public static final String p14 = "sts.keymanagement.keystore.keys.encKeyPairs.legacyInterval=86400000";

	public static final String p15 = "sts.keymanagement.keystore.keys.signKeyPairs.initialCount=1";
	public static final String p16 = "sts.keymanagement.keystore.keys.signKeyPairs.algo=RSA";
	public static final String p17 = "sts.keymanagement.keystore.keys.signKeyPairs.sigAlgo=SHA256withRSA";
	public static final String p18 = "sts.keymanagement.keystore.keys.signKeyPairs.size=2048";
	public static final String p19 = "sts.keymanagement.keystore.keys.signKeyPairs.name=STS Multibanking";
	public static final String p20 = "sts.keymanagement.keystore.keys.signKeyPairs.validityInterval=3600000";
	public static final String p21 = "sts.keymanagement.keystore.keys.signKeyPairs.legacyInterval=86400000";

	public static final String p22 = "sts.keymanagement.keystore.keys.secretKeys.initialCount=1";
	public static final String p23 = "sts.keymanagement.keystore.keys.secretKeys.algo=AES";
	public static final String p24 = "sts.keymanagement.keystore.keys.secretKeys.size=256";
	public static final String p25 = "sts.keymanagement.keystore.keys.secretKeys.validityInterval=3600000";
	public static final String p26 = "sts.keymanagement.keystore.keys.secretKeys.legacyInterval=86400000";

	public static final String p27 = "sts.keymanagement.rotation.secretKeys.enabled=false";
	public static final String p28 = "sts.keymanagement.rotation.signKeyPairs.enabled=false";
	public static final String p29 = "sts.keymanagement.rotation.encKeyPairs.enabled=false";
}
