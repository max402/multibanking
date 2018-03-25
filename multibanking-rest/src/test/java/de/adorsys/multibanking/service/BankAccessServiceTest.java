package de.adorsys.multibanking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.util.Assert.isTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
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
import de.adorsys.multibanking.exception.ResourceNotFoundException;
import de.adorsys.multibanking.service.config.BaseServiceTest;
import de.adorsys.multibanking.service.old.TestConstants;
import de.adorsys.multibanking.service.old.TestUtil;
import de.adorsys.onlinebanking.mock.MockBanking;
import figo.FigoBanking;

@RunWith(SpringRunner.class)
public class BankAccessServiceTest extends BaseServiceTest {

    @MockBean
    private FigoBanking figoBanking;
    @MockBean
    private MockBanking mockBanking;
    @MockBean
    private OnlineBankingServiceProducer bankingServiceProducer;

    @Autowired
    private BookingService bookingService;
    @Autowired
    private UserService userService;
    @Autowired
    private BankAccountService bankAccountService;
    @Autowired
    private BankAccessService bankAccessService;
    @Autowired
    private DeleteExpiredUsersScheduled userScheduler;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @BeforeClass
    public static void beforeClass() {
    	TestConstants.setup();
    }

    @Before
    public void beforeTest() throws IOException {

    	importBanks();
        
    	MockitoAnnotations.initMocks(this);
        when(bankingServiceProducer.getBankingService(anyString())).thenReturn(mockBanking);
    }

    /**
     * Creates a bank access with a non existing bank code.
     * 
     */
    @Test
    public void create_bank_access_not_supported() {
        when(mockBanking.bankSupported(anyString())).thenReturn(false);
        thrown.expect(InvalidBankAccessException.class);

    	randomAuthAndUser();
        
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

    	randomAuthAndUser();
        BankAccessEntity bankAccessEntity = TestUtil.getBankAccessEntity(userId(), randomAccessId(), "29999999", "0000");
        // "testUserId", 
        bankAccessService.createBankAccess(bankAccessEntity);
    }

    @Test
    public void create_bank_access_invalid_pin() {
    	// Init user
    	randomAuthAndUser();
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
    	// Mocks
    	randomAuthAndUser();
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
    public void when_delete_bankAccesd_user_notExist_should_throw_exception() {
    	// Inject a user, without creating that user in the storage.
    	auth("fakeUser", "fakePassword");
    	
        thrown.expect(ResourceNotFoundException.class);
        // "badLogin", 
        boolean deleteBankAccess = bankAccessService.deleteBankAccess("badAccess");
        assertThat(deleteBankAccess).isEqualTo(false);
    }

    @Test
    public void when_delete_bankAcces_user_exist_should_return_false() {
    	randomAuthAndUser();

        // userId, 
        boolean deleteBankAccess = bankAccessService.deleteBankAccess("access");
        assertThat(deleteBankAccess).isEqualTo(false);
    }

    @Test
    public void when_delete_bankAcces_user_exist_should_return_true() {
    	randomAuthAndUser();
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
    	randomAuthAndUser();
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

    @Test
    public void cleaup_users_job() {
    	randomAuthAndUser(new Date());

        userScheduler.deleteJob();
        thrown.expect(ResourceNotFoundException.class);
        userService.readUser();
    }

//    @Test
//    public void searchBank() {
//        notEmpty(bankService.search("76090"), "bank not found");
//        isTrue(bankService.search("76090500").size() == 1, "wrong search result");
//        isTrue(bankService.search("12030000").size() == 1, "wrong search result");
//        isTrue(bankService.search("XYZ").size() == 0, "wrong search result");
//        notEmpty(bankService.search("Sparda"), "bank not found");
//        notEmpty(bankService.search("Sparda Bank"), "bank not found");
//    }
//
}
