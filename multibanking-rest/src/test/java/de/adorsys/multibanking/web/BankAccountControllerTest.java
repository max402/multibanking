package de.adorsys.multibanking.web;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
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

import de.adorsys.multibanking.domain.BankAccessEntity;
import de.adorsys.multibanking.domain.BankAccountEntity;
import de.adorsys.multibanking.service.BankAccessService;
import de.adorsys.multibanking.service.BankAccountService;
import de.adorsys.multibanking.service.BookingService;
import de.adorsys.multibanking.web.base.BaseControllerUnitTest;
import de.adorsys.multibanking.web.config.WebMvcUnitTest;
import domain.BankAccount;

@WebMvcUnitTest(controllers = BankAccountController.class)
public class BankAccountControllerTest extends BaseControllerUnitTest {

    @InjectMocks
    private BankAccountController bankAccountController;
    @MockBean
    private BankAccountService bankAccountService;
    @MockBean
    private BankAccessService bankAccessService;
    @MockBean
    private BookingService bookingService;
    
    private List<BankAccessEntity> bankAccesses = null;
    private List<BankAccountEntity> bankAccounts = null;
    private String accountListStr;
    
	private String bankAccessId = "5a998c0c7077e800014ca672";
	private String accountId = "5a998c0c7077e800014ca673";


    @Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(bankAccountController).build();
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("BankAccountControllerTest/bank_accounts.json");
        accountListStr = IOUtils.toString(stream, Charset.forName("UTF-8"));
        bankAccounts = Arrays.asList(mapper.readValue(accountListStr, BankAccountEntity[].class));
        
        stream = this.getClass().getClassLoader().getResourceAsStream("BankAccountControllerTest/bank_access.json");
        bankAccesses = Arrays.asList(mapper.readValue(stream, BankAccessEntity[].class));
	}

	@Test
	public void testGetBankAccounts200() throws Exception {
		BDDMockito.when(bankAccountService.loadForBankAccess(bankAccessId)).thenReturn(bankAccounts);
		BDDMockito.when(bankAccessService.exists(bankAccessId)).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.get(basePath().build(bankAccessId).toString()).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.content().json(accountListStr));
	}

	@Test
	public void testGetBankAccount200() throws Exception {
		BDDMockito.when(bankAccessService.exists(bankAccessId)).thenReturn(true);
		Optional<BankAccountEntity> bankAccount = Optional.of(bankAccounts.get(0));
		BDDMockito.when(bankAccountService.loadBankAccount(bankAccessId, accountId)).thenReturn(bankAccount);

        String expectedJson = mapper.writeValueAsString(bankAccount.get());

        mockMvc.perform(MockMvcRequestBuilders.get(idPath().build().toString(), bankAccessId, accountId).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.content().json(expectedJson));
	}

	@Test
	public void testGetBankAccount404() throws Exception {
		BDDMockito.when(bankAccessService.exists(bankAccessId)).thenReturn(true);
		BDDMockito.when(bankAccountService.loadBankAccount(bankAccessId, accountId)).thenReturn(Optional.empty());
        mockMvc.perform(MockMvcRequestBuilders.get(idPath().build().toString(), bankAccessId, accountId).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}
	
	@Test
	public void testSyncBookings400BadBankAccess() throws Exception {
		BDDMockito.when(bankAccessService.loadbankAccess(bankAccessId)).thenReturn(Optional.empty());
        mockMvc.perform(MockMvcRequestBuilders.put(idPath().path("/sync").build().toString(), bankAccessId, accountId)
        		.contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}
	
	@Test
	public void testSyncBookings400BadBankAccount() throws Exception {
		BDDMockito.when(bankAccessService.loadbankAccess(bankAccessId)).thenReturn(Optional.of(bankAccesses.get(0)));
		BDDMockito.when(bankAccountService.loadBankAccount(bankAccessId, accountId)).thenReturn(Optional.empty());
        mockMvc.perform(MockMvcRequestBuilders.put(idPath().path("/sync").build().toString(), bankAccessId, accountId)
        		.contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}
	
	@Test
	public void testSyncBookingsOngoingSynch() throws Exception {
		BDDMockito.when(bankAccessService.loadbankAccess(bankAccessId)).thenReturn(Optional.of(bankAccesses.get(0)));
		BDDMockito.when(bankAccountService.loadBankAccount(bankAccessId, accountId)).thenReturn(Optional.of(bankAccounts.get(0)));
		BDDMockito.when(bankAccountService.getSyncStatus(bankAccessId, accountId)).thenReturn(BankAccount.SyncStatus.SYNC);
        mockMvc.perform(MockMvcRequestBuilders.put(idPath().path("/sync").build().toString(), bankAccessId, accountId)
        		.contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(MockMvcResultMatchers.status().isProcessing());
	}
	
	@Test
	public void testSyncBookings200() throws Exception {
		BDDMockito.when(bankAccessService.loadbankAccess(bankAccessId)).thenReturn(Optional.of(bankAccesses.get(0)));
		BDDMockito.when(bankAccountService.loadBankAccount(bankAccessId, accountId)).thenReturn(Optional.of(bankAccounts.get(0)));
		BDDMockito.when(bankAccountService.getSyncStatus(bankAccessId, accountId)).thenReturn(BankAccount.SyncStatus.READY);
//		BDDMockito.when(bookingService.syncBookings(bankAccesses.get(0), bankAccounts.get(0), null, null).thenReturn();
        mockMvc.perform(MockMvcRequestBuilders.put(idPath().path("/sync").build().toString(), bankAccessId, accountId)
        		.contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(MockMvcResultMatchers.status().isNoContent());
	}

	/* *******  URLS *******/
    private static final UriComponentsBuilder basePath(){
    	return UriComponentsBuilder.fromPath(BankAccountController.BASE_PATH);
    }
    private static final UriComponentsBuilder idPath(){
    	return basePath().path("/{accountId}");
    }
}
