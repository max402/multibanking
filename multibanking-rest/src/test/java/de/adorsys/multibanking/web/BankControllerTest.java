package de.adorsys.multibanking.web;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.adorsys.docusafe.business.types.complex.DSDocument;
import org.adorsys.docusafe.service.types.DocumentContent;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.UriComponentsBuilder;

import de.adorsys.multibanking.domain.BankEntity;
import de.adorsys.multibanking.service.BankService;
import de.adorsys.multibanking.utils.FQNUtils;
import de.adorsys.multibanking.web.base.BaseControllerUnitTest;
import de.adorsys.multibanking.web.config.WebMvcUnitTest;
import domain.Bank;

@WebMvcUnitTest(controllers = BankController.class)
public class BankControllerTest extends BaseControllerUnitTest {
    @InjectMocks
    private BankController bankController;
    @MockBean
	BankService bankService;

    private String banksStr;
    private DSDocument dsDocument;
    private List<Bank> banks;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(bankController).build();
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("BankControllerTest/mock_bank.json");
        banksStr = IOUtils.toString(stream, Charset.forName("UTF-8"));
        DocumentContent documentContent = new DocumentContent(banksStr.getBytes("UTF-8"));
		dsDocument = new DSDocument(FQNUtils.banksFQN(), documentContent, null);
		
		banks = Arrays.asList(mapper.readValue(banksStr, Bank[].class));
		
	}

	@Test
	public void testGetBank() throws Exception {
		BankEntity bankEntity = new BankEntity();
		BeanUtils.copyProperties(banks.get(0), bankEntity);
		BDDMockito.when(bankService.findByBankCode("19999999")).thenReturn(Optional.of(bankEntity));
		String expected = mapper.writeValueAsString(bankEntity);
        mockMvc.perform(MockMvcRequestBuilders.get(bankCodePath().build("19999999").toString())
                .accept(MediaType.APPLICATION_JSON_VALUE)).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.content().json(expected));
	}

	@Test
	public void testSearchBank() throws Exception {
		BDDMockito.when(bankService.search("1999")).thenReturn(dsDocument);
		String query = basePath().queryParam("query","1999").build().toString();
        mockMvc.perform(MockMvcRequestBuilders.get(query)
                .accept(MediaType.APPLICATION_JSON_VALUE))
        		.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.content().json(banksStr));
	}
	
	/* *******  URLS *******/
    private static final UriComponentsBuilder basePath(){
    	return UriComponentsBuilder.fromPath(BankController.BASE_PATH);
    }
    private static final UriComponentsBuilder bankCodePath(){
    	return basePath().path("/{bankCode}");
    }
}
