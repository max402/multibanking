package de.adorsys.multibanking.web;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
    
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(bankAccessController).build();
        try {
            InputStream stream = this.getClass().getClassLoader().getResourceAsStream("BankAccessControllerTest/bank_access.json");
            bankAccesses = Arrays.asList(mapper.readValue(stream, BankAccessEntity[].class));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        BDDMockito.when(bankAccessService.getBanAccesses()).thenReturn(bankAccesses);
	}

	@Test
	public void testGetBankAccesses200() throws Exception {
        String expectedJson = mapper.writeValueAsString(bankAccesses);
        mockMvc.perform(MockMvcRequestBuilders.get(BankAccessController.BASE_PATH).contentType(MediaType.APPLICATION_JSON)
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

        String uri = UriComponentsBuilder.fromPath(BankAccessController.BASE_PATH).path("/{accessId}").build().toString();
        mockMvc.perform(MockMvcRequestBuilders.get(uri, access_id).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.content().json(expectedJson));
	}

	@Test
	public void testCreateBankaccess201() {
    	BankAccessEntity newBankAccess = new BankAccessEntity();
        newBankAccess.setBankCode("19999999");
        newBankAccess.setBankLogin("adsfdsfad");
        newBankAccess.setPin("12345");
        newBankAccess.setBankName("Mock");
        newBankAccess.setUserId(Ids.uuid());
        newBankAccess.setId(Ids.uuid());

        BDDMockito.when(bankAccessService.createBankAccess(newBankAccess)).thenReturn(newBankAccess);

//        String expectedJson = mapper.writeValueAsString(id);
//        String newBankAccessJson = new ObjectMapper().writeValueAsString(newBankAccess);

//        mockMvc.perform(MockMvcRequestBuilders.post("/bankaccesses").contentType(MediaType.APPLICATION_JSON).content(newBankAccessJson)
//                .accept(MediaType.APPLICATION_JSON_VALUE)).andExpect(MockMvcResultMatchers.status().isCreated())
//                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
//                .andExpect(MockMvcResultMatchers.content().json(expectedJson)).andReturn();
	}

	@Test
	public void testDeleteBankAccess() {
//		fail("Not yet implemented");
	}

}
