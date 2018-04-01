package de.adorsys.multibanking.web;

import static org.junit.Assert.fail;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.UriComponentsBuilder;

import de.adorsys.multibanking.domain.BankAccountEntity;
import de.adorsys.multibanking.service.BankAccessService;
import de.adorsys.multibanking.service.BankAccountService;
import de.adorsys.multibanking.web.base.BaseControllerUnitTest;
import de.adorsys.multibanking.web.config.WebMvcUnitTest;

@WebMvcUnitTest(controllers = BankAccountController.class)
public class BankAccountControllerTest extends BaseControllerUnitTest {

    @InjectMocks
    private BankAccountController bankAccountController;
    @MockBean
    private BankAccountService bankAccountService;
    @MockBean
    private BankAccessService bankAccessService;
    
    private List<BankAccountEntity> bankAccounts = null;
    private List<BankAccountEntity> emptyBankAccounts = Collections.emptyList();

    @Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(bankAccountController).build();
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("BankAccountControllerTest/bank_accounts.json");
        bankAccounts = Arrays.asList(mapper.readValue(stream, BankAccountEntity[].class));
	}

	@Test
	public void testGetBankAccounts() throws Exception {
        String bankAccessId = "5a998c0c7077e800014ca672";
		BDDMockito.when(bankAccountService.loadForBankAccess(bankAccessId)).thenReturn(bankAccounts);
		BDDMockito.when(bankAccessService.exists(bankAccessId)).thenReturn(true);

        String expectedJson = mapper.writeValueAsString(bankAccounts);
        mockMvc.perform(MockMvcRequestBuilders.get(basePath().build(bankAccessId).toString()).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.content().json(expectedJson));
	}

	@Test
	public void testGetBankAccount() {
//		fail("Not yet implemented");
	}

	@Test
	public void testSyncBookings() {
//		fail("Not yet implemented");
	}

	/* *******  URLS *******/
    private static final UriComponentsBuilder basePath(){
    	return UriComponentsBuilder.fromPath(BankAccountController.BASE_PATH);
    }
    private static final UriComponentsBuilder idPath(){
    	return basePath().path("/{accountId}");
    }
}
