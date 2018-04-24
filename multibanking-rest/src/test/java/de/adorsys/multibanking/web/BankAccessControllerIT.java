package de.adorsys.multibanking.web;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import de.adorsys.multibanking.utils.Ids;
import de.adorsys.multibanking.web.base.BaseLoggedInControllerIT;
import de.adorsys.multibanking.web.base.PasswordGrantResponse;

@RunWith(SpringRunner.class)
public class BankAccessControllerIT extends BaseLoggedInControllerIT {

    @Before
    public void setup() {
    	super.setup();
    	PasswordGrantResponse resp = auth(Ids.uuid(), Ids.uuid());
    	Assume.assumeFalse(StringUtils.isEmpty(resp.getAccessToken()));
    	Assume.assumeTrue("Bearer".equals(resp.getTokenType()));
    }
    
	@Test
	public void testCreateBankaccess201() throws Exception {
		// TODO continue here.
	}
    
}
