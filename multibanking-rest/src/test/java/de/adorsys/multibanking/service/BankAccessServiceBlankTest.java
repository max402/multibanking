package de.adorsys.multibanking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.util.Assert.isTrue;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import de.adorsys.multibanking.domain.BankAccessEntity;
import de.adorsys.multibanking.domain.BankAccountEntity;
import de.adorsys.multibanking.exception.InvalidBankAccessException;
import de.adorsys.multibanking.exception.InvalidPinException;
import de.adorsys.multibanking.service.config.BaseServiceTest;
import de.adorsys.multibanking.service.old.TestConstants;
import de.adorsys.multibanking.service.old.TestUtil;
import de.adorsys.onlinebanking.mock.MockBanking;
import figo.FigoBanking;

@RunWith(SpringRunner.class)
public class BankAccessServiceBlankTest extends BaseServiceTest {

    @MockBean
    protected FigoBanking figoBanking;
    @MockBean
    protected MockBanking mockBanking;
    @MockBean
    protected OnlineBankingServiceProducer bankingServiceProducer;
    @Autowired
    private BankAccountService bankAccountService;
    @Autowired
    private BankAccessService bankAccessService;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @BeforeClass
    public static void beforeClass() {
    	TestConstants.setup();
    }

    @Before
    public void beforeTest() throws Exception {
    	MockitoAnnotations.initMocks(this);
        when(bankingServiceProducer.getBankingService(anyString())).thenReturn(mockBanking);
    	randomAuthAndUser();
    	importBanks();
    }
    
    @After
    public void after() throws Exception{
    	if(userContext!=null)
    		rcMap.put(userContext.getAuth().getUserID().getValue()+ ":"+testName.getMethodName(), userContext.getRequestCounter());
    	if(systemContext!=null)
    		rcMap.put(systemContext.getUser().getAuth().getUserID().getValue()+ ":"+testName.getMethodName(), systemContext.getUser().getRequestCounter());
    }
    

    /**
     * Creates a bank access with a non existing bank code.
     * 
     */
    @Test
    public void create_bank_access_not_supported() {
        when(mockBanking.bankSupported(anyString())).thenReturn(false);
        thrown.expect(InvalidBankAccessException.class);
        
        BankAccessEntity bankAccessEntity = TestUtil.getBankAccessEntity(userId(), 
        		randomAccessId(), "unsupported", "0000");
        bankAccessEntity.setBankCode("unsupported");
        // "testUserId", 
        bankAccessService.createBankAccess(bankAccessEntity);
    }

    @Test
    public void create_bank_access_no_accounts() {
//        when(bankProvider.getBank(anyString())).thenReturn(new BankEntity());
        when(mockBanking.bankSupported(anyString())).thenReturn(true);
        thrown.expect(InvalidBankAccessException.class);

        BankAccessEntity bankAccessEntity = TestUtil.getBankAccessEntity(userId(), randomAccessId(), "29999999", "0000");
        // "testUserId", 
        bankAccessService.createBankAccess(bankAccessEntity);
    }

    @Test
    public void create_bank_access_invalid_pin() {

    	// Mock bank access
        BankAccessEntity bankAccessEntity = TestUtil.getBankAccessEntity(userId(), randomAccessId(), "29999999", "0000");

        when(mockBanking.bankSupported(anyString())).thenReturn(true);
        when(mockBanking.loadBankAccounts(any(), any(), anyString(), anyString(), anyBoolean()))
                .thenThrow(new InvalidPinException(bankAccessEntity.getId()));
        thrown.expect(InvalidPinException.class);

        // "testUserId", 
        bankAccessService.createBankAccess(bankAccessEntity);
    }

    @Test
    public void create_bank_access_ok() {

        BankAccessEntity bankAccessEntity = TestUtil.getBankAccessEntity(userId(), randomAccessId(), "29999999", "0000");
        BankAccountEntity bankAccountEntity = TestUtil.getBankAccountEntity(bankAccessEntity, randomAccountId());
        
        when(mockBanking.bankSupported(anyString())).thenReturn(true);
        when(mockBanking.loadBankAccounts(any(), any(), anyString(), anyString(), anyBoolean()))
                .thenReturn(Arrays.asList(bankAccountEntity));

        bankAccessService.createBankAccess(bankAccessEntity);

        isTrue(bankAccessService.loadbankAccess(bankAccessEntity.getId()).isPresent(), "bankaccess not exists");
        isTrue(bankAccountService.loadBankAccount(bankAccessEntity.getId(), bankAccountEntity.getId()).isPresent(), "bankaccount not exists");
    }

    @Test
    public void when_delete_bankAcces_user_exist_should_return_false() {
        // userId, 
        boolean deleteBankAccess = bankAccessService.deleteBankAccess("access");
        assertThat(deleteBankAccess).isEqualTo(false);
    }

    @Test
    public void when_delete_bankAcces_user_exist_should_return_true() {
        BankAccessEntity bankAccessEntity = TestUtil.getBankAccessEntity(userId(), randomAccessId(), "29999999", "0000");
        BankAccountEntity bankAccountEntity = TestUtil.getBankAccountEntity(bankAccessEntity, randomAccountId());
        
        when(mockBanking.bankSupported(anyString())).thenReturn(true);
        when(mockBanking.loadBankAccounts(any(), any(), anyString(), anyString(), anyBoolean()))
                .thenReturn(Arrays.asList(bankAccountEntity));

    	bankAccessService.createBankAccess(bankAccessEntity);
        boolean deleteBankAccess = bankAccessService.deleteBankAccess(bankAccessEntity.getId());
        assertThat(deleteBankAccess).isEqualTo(true);

        isTrue(!bankAccessService.loadbankAccess(bankAccessEntity.getId()).isPresent(), "bankaccess shall not exists");
    }

    @Test
    public void get_bank_code() {
        BankAccessEntity bankAccessEntity = TestUtil.getBankAccessEntity(userId(), randomAccessId(), "49999999", "0000");
        BankAccountEntity bankAccountEntity = TestUtil.getBankAccountEntity(bankAccessEntity, randomAccountId());
        
        when(mockBanking.bankSupported(anyString())).thenReturn(true);
        when(mockBanking.loadBankAccounts(any(), any(), anyString(), anyString(), anyBoolean()))
                .thenReturn(Arrays.asList(bankAccountEntity));

    	bankAccessService.createBankAccess(bankAccessEntity);

        String bankCode = bankAccessService.loadbankAccess(bankAccessEntity.getId())
        		.orElse(null).getBankCode();
        assertThat(bankCode).isEqualTo("49999999");
    }
}
