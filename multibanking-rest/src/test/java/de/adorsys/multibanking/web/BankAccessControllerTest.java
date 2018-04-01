package de.adorsys.multibanking.web;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.UriComponentsBuilder;

import de.adorsys.multibanking.domain.BankAccessEntity;
import de.adorsys.multibanking.exception.BankAccessAlreadyExistException;
import de.adorsys.multibanking.service.BankAccessService;
import de.adorsys.multibanking.utils.Ids;
import de.adorsys.multibanking.web.base.BaseControllerUnitTest;
import de.adorsys.multibanking.web.config.WebMvcUnitTest;

@WebMvcUnitTest(controllers = BankAccessController.class)
public class BankAccessControllerTest extends BaseControllerUnitTest {
        
    @InjectMocks
    private BankAccessController bankAccessController;

    @MockBean
    private BankAccessService bankAccessService;
    
    private List<BankAccessEntity> bankAccesses = null;
    private List<BankAccessEntity> emptyBankAccesses = Collections.emptyList();
        
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(bankAccessController).build();
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("BankAccessControllerTest/bank_access.json");
        bankAccesses = Arrays.asList(mapper.readValue(stream, BankAccessEntity[].class));
	}

	@Test
	public void testGetBankAccesses200() throws Exception {
        BDDMockito.when(bankAccessService.getBankAccesses()).thenReturn(bankAccesses);

        String expectedJson = mapper.writeValueAsString(bankAccesses);
        mockMvc.perform(MockMvcRequestBuilders.get(basePath().build().toString()).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.content().json(expectedJson));
	}

	@Test
	public void testGetBankAccesses200EmptyList() throws Exception {
        BDDMockito.when(bankAccessService.getBankAccesses()).thenReturn(emptyBankAccesses);

		String expectedJson = mapper.writeValueAsString(emptyBankAccesses);
        mockMvc.perform(MockMvcRequestBuilders.get(basePath().build().toString()).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.content().json(expectedJson));
	}
	
	@Test
	public void testGetBankAccess200() throws Exception {
		String access_id = "5a998c0c7077e800014ca672";
		Optional<BankAccessEntity> bankAccess = Optional.of(bankAccesses.get(0));
        BDDMockito.when(bankAccessService.loadbankAccess(access_id)).thenReturn(bankAccess);

        String expectedJson = mapper.writeValueAsString(bankAccess.get());

        mockMvc.perform(MockMvcRequestBuilders.get(idPath().build().toString(), access_id).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.content().json(expectedJson));
	}

	@Test
	public void testGetBankAccess404NotFound() throws Exception {
		String access_id = "5a998c0c7077e800014ca672";
		Optional<BankAccessEntity> bankAccess = Optional.empty();
        BDDMockito.when(bankAccessService.loadbankAccess(access_id)).thenReturn(bankAccess);

        mockMvc.perform(MockMvcRequestBuilders.get(idPath().build().toString(), access_id).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andExpect(MockMvcResultMatchers.status().isNotFound());
	}
	
	@Test
	public void testCreateBankaccess201() throws Exception {
    	BankAccessEntity newBankAccess = newBankAccess();
        newBankAccess.setId(Ids.uuid());

        BDDMockito.when(bankAccessService.createBankAccess(newBankAccess)).thenReturn(newBankAccess);
        String uuid = Ids.uuid();
        // Set user context.
        auth(uuid, Ids.uuid());

        String newBankAccessJson = mapper.writeValueAsString(newBankAccess);

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(basePath().build().toString())
					.contentType(MediaType.APPLICATION_JSON).content(newBankAccessJson)
                .accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(MockMvcResultMatchers.status().isCreated())
        		.andReturn();
		
		String locationHeader = mvcResult.getResponse().getHeader("Location");
		Assert.assertTrue(StringUtils.endsWith(locationHeader, idPath().build(newBankAccess.getId()).toString()));
	}

	@Test
	public void testCreateBankaccess409() throws Exception {
    	BankAccessEntity newBankAccess = newBankAccess();
        newBankAccess.setId(Ids.uuid());

        BDDMockito.when(bankAccessService.createBankAccess(newBankAccess)).thenThrow(new BankAccessAlreadyExistException(newBankAccess.getId()));
        String newBankAccessJson = mapper.writeValueAsString(newBankAccess);

		mockMvc.perform(MockMvcRequestBuilders.post(basePath().build().toString()).contentType(MediaType.APPLICATION_JSON).content(newBankAccessJson)
                .accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(MockMvcResultMatchers.status().isConflict());
	}
	
	@Test
	public void testDeleteBankAccess204() throws Exception {
        String accessId = Ids.uuid();
		BDDMockito.when(bankAccessService.deleteBankAccess(accessId)).thenReturn(true);
		mockMvc.perform(MockMvcRequestBuilders.delete(idPath().build(accessId).toString())
                .accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(MockMvcResultMatchers.status().isNoContent());
	}

	@Test
	public void testDeleteBankAccess410() throws Exception {
        String accessId = Ids.uuid();
		BDDMockito.when(bankAccessService.deleteBankAccess(accessId)).thenReturn(false);
		mockMvc.perform(MockMvcRequestBuilders.delete(idPath().build(accessId).toString())
                .accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(MockMvcResultMatchers.status().isGone());
	}
	
	private BankAccessEntity newBankAccess(){
    	BankAccessEntity newBankAccess = new BankAccessEntity();
        newBankAccess.setBankCode("19999999");
        newBankAccess.setBankLogin("adsfdsfad");
        newBankAccess.setPin("12345");
        newBankAccess.setBankName("Mock");
        return newBankAccess;
	}
	
	/* *******  URLS *******/
    private static final UriComponentsBuilder basePath(){
    	return UriComponentsBuilder.fromPath(BankAccessController.BASE_PATH);
    }
    private static final UriComponentsBuilder idPath(){
    	return basePath().path("/{accessId}");
    }
}
