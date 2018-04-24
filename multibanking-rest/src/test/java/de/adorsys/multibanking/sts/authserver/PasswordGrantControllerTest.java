package de.adorsys.multibanking.sts.authserver;

import java.net.URI;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriComponentsBuilder;

import de.adorsys.multibanking.web.base.BaseControllerITTest;
import de.adorsys.multibanking.web.base.PasswordGrantResponse;

@RunWith(SpringRunner.class)
@ActiveProfiles({"InMemory"})
public class PasswordGrantControllerTest extends BaseControllerITTest {

    @Test
	public void testCreateBankaccess201() throws Exception {
    	URI uri = basePath()
		.queryParam("grant_type", "password")
		.queryParam("username", "francis")
		.queryParam("password", "francis123")
		.build().toUri();
    	
    	PasswordGrantResponse resp = testRestTemplate.getForObject(uri, PasswordGrantResponse.class);
    	Assert.assertFalse(StringUtils.isEmpty(resp.getAccessToken()));
    	Assert.assertEquals("Bearer", resp.getTokenType());
    }

    private final UriComponentsBuilder basePath(){
    	return UriComponentsBuilder.fromPath("/token/password-grant");
	}
}
