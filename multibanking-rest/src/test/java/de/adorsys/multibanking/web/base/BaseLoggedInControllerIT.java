package de.adorsys.multibanking.web.base;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assume;
import org.junit.Before;

import de.adorsys.multibanking.utils.Ids;

public abstract class BaseLoggedInControllerIT extends BaseControllerITTest {

    @Before
    public void setup() throws Exception {
    	super.setup();
    	PasswordGrantResponse resp = auth(Ids.uuid(), Ids.uuid());
    	Assume.assumeFalse(StringUtils.isEmpty(resp.getAccessToken()));
    	Assume.assumeTrue("Bearer".equals(resp.getTokenType()));
    }
}
