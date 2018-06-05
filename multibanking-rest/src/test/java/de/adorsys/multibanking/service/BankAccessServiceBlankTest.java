package de.adorsys.multibanking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.util.Assert.isInstanceOf;

import java.util.Arrays;
import java.util.Optional;

import org.adorsys.docusafe.business.DocumentSafeService;
import org.adorsys.docusafe.business.types.UserID;
import org.adorsys.docusafe.business.types.complex.DocumentFQN;
import org.adorsys.docusafe.business.types.complex.UserIDAuth;
import org.adorsys.encobject.domain.ReadKeyPassword;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.adorsys.multibanking.auth.UserContext;
import de.adorsys.multibanking.auth.UserObjectPersistenceService;
import de.adorsys.multibanking.domain.BankAccessData;
import de.adorsys.multibanking.domain.BankAccessEntity;
import de.adorsys.multibanking.domain.BankAccountEntity;
import de.adorsys.multibanking.domain.BankEntity;
import de.adorsys.multibanking.domain.UserData;
import de.adorsys.multibanking.exception.InvalidBankAccessException;
import de.adorsys.multibanking.exception.InvalidPinException;
import de.adorsys.multibanking.exception.ResourceNotFoundException;
import de.adorsys.multibanking.exception.UserNotFoundException;
import de.adorsys.multibanking.service.old.TestUtil;
import de.adorsys.multibanking.service.producer.OnlineBankingServiceProducer;
import de.adorsys.multibanking.utils.FQNUtils;
import de.adorsys.multibanking.utils.Ids;
import de.adorsys.onlinebanking.mock.MockBanking;
import domain.BankApi;

@RunWith(SpringRunner.class)
//@ActiveProfiles({"InMemory"})
public class BankAccessServiceBlankTest {
    
    private BankDataService bds;
    
    @MockBean
    private BankService bankService;
    @MockBean
    private BankAccessCredentialService credentialService; 
    @MockBean
    private OnlineBankingServiceProducer bankingServiceProducer;
    @MockBean
    private SynchBankAccountsService synchBankAccountService;
    
    @MockBean
    protected MockBanking mockBanking;    

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private DocumentSafeService documentSafeService;

    @MockBean
    private UserContext userContext;
    
//    private UserObjectPersistenceService uos;
    
    @Before
    public void before(){
        bds = new BankDataService(userContext, bankService, credentialService, bankingServiceProducer, synchBankAccountService, objectMapper , documentSafeService);
        when(bankingServiceProducer.getBankingService(any(BankApi.class))).thenReturn(mockBanking);
        when(bankingServiceProducer.getBankingService(anyString())).thenReturn(mockBanking);
//        uos = new UserObjectPersistenceService(userContext,objectMapper,documentSafeService);
    }
    
    private void mockUserContext(String userId){
        UserContext uc = new UserContext();
        UserIDAuth userIDAuth = new UserIDAuth(new UserID(userId), new ReadKeyPassword("readKeyPassword"));
        when(userContext.getAuth()).thenReturn(userIDAuth);
        when(userContext.getCache()).thenReturn(uc.getCache());
        when(userContext.getDeletedDirCache()).thenReturn(uc.getDeletedDirCache());
        when(userContext.getRequestCounter()).thenReturn(uc.getRequestCounter());
    }
    
    /**
     * Creates a bank access with a non existing bank code.
     * 
     */
    @SuppressWarnings("unchecked")
    @Test
    public void create_bank_access_not_supported() {
        when(mockBanking.bankSupported(anyString())).thenReturn(false);
        thrown.expect(InvalidBankAccessException.class);
        
        String userId = Ids.uuid();
        mockUserContext(userId);
        String accessId = Ids.uuid();
        when(bankService.findByBankCode(anyString())).thenReturn(null);
        BankAccessEntity bankAccessEntity = TestUtil.getBankAccessEntity(userId, accessId, "unsupported", "0000");
        bankAccessEntity.setBankCode("unsupported");
        
        UserData userData = bds.createUser(null);
//        when(uos.load(any(DocumentFQN.class), any(TypeReference.class))).thenReturn(Optional.of(userData));
        when(synchBankAccountService.synchBankAccounts(any(), any(), any())).thenThrow(InvalidBankAccessException.class);
        bds.createBankAccess(bankAccessEntity);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void create_bank_access_invalid_pin() {
    	// Mock bank access
        BankAccessEntity bankAccessEntity = TestUtil.getBankAccessEntity(Ids.uuid(), Ids.uuid(), "29999999", "0000");
        when(mockBanking.bankSupported(anyString())).thenReturn(true);
        when(mockBanking.loadBankAccounts(any(), any(), anyString(), anyString(), anyBoolean()))
                .thenThrow(new InvalidPinException(bankAccessEntity.getId()));
        thrown.expect(InvalidPinException.class);
        
        bds = new BankDataService(userContext, bankService, credentialService, bankingServiceProducer,new SynchBankAccountsService(), objectMapper , documentSafeService);
        
        mockUserContext(Ids.uuid());
//        UserData userData = new UserData();
        UserData userData = bds.createUser(null);        
        BankEntity bankEntity = new BankEntity();
        bankEntity.setBlzHbci("29999999");
        when(bankService.findByBankCode("29999999")).thenReturn(Optional.of(bankEntity));
//        when(uos.load(any(DocumentFQN.class), any(TypeReference.class))).thenReturn(Optional.of(userData));
        // Prevent user creation
//        when(uos.documentExists(any(DocumentFQN.class), any(TypeReference.class))).thenReturn(true);
        bds.createBankAccess(bankAccessEntity);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void create_bank_access_ok() {

        BankAccessEntity bankAccessEntity = TestUtil.getBankAccessEntity(Ids.uuid(), Ids.uuid(), "29999999", "0000");
        BankAccountEntity bankAccountEntity = TestUtil.getBankAccountEntity(bankAccessEntity, Ids.uuid());
        
        when(mockBanking.bankSupported(anyString())).thenReturn(true);
        when(mockBanking.loadBankAccounts(any(), any(), anyString(), anyString(), anyBoolean()))
                .thenReturn(Arrays.asList(bankAccountEntity));
        
        mockUserContext(Ids.uuid());
        UserData userData = bds.createUser(null);        
        BankEntity bankEntity = new BankEntity();
        bankEntity.setBlzHbci("29999999");
        when(bankService.findByBankCode("29999999")).thenReturn(Optional.of(bankEntity));
//        when(uos.load(any(DocumentFQN.class), any(TypeReference.class))).thenReturn(Optional.of(userData));
        
//        when(uos.documentExists(any(DocumentFQN.class), any(TypeReference.class))).thenReturn(true);
        bds.createBankAccess(bankAccessEntity);
        isInstanceOf(BankAccessData.class, bds.load().bankAccessDataOrException(bankAccessEntity.getId()));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void when_delete_bankAcces_user_exist_should_return_false() {
//        UserData userData = new UserData();
//        when(uos.load(any(DocumentFQN.class), any(TypeReference.class))).thenReturn(Optional.of(userData));
        mockUserContext(Ids.uuid());
        thrown.expect(UserNotFoundException.class);
        boolean deleteBankAccess = bds.deleteBankAccess("access");
//        assertThat(deleteBankAccess).isEqualTo(false);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void when_delete_bankAcces_user_exist_should_return_true() {
        BankAccessEntity bankAccessEntity = TestUtil.getBankAccessEntity(Ids.uuid(), Ids.uuid(), "29999999", "0000");
        BankAccountEntity bankAccountEntity = TestUtil.getBankAccountEntity(bankAccessEntity, Ids.uuid());
        
        when(mockBanking.bankSupported(anyString())).thenReturn(true);
        when(mockBanking.loadBankAccounts(any(), any(), anyString(), anyString(), anyBoolean()))
                .thenReturn(Arrays.asList(bankAccountEntity));

        String userId = Ids.uuid();
        mockUserContext(userId);

//        bds.createBankAccess(bankAccessEntity);
        UserData userData = bds.createUser(null);        
//        UserData userData = new UserData();
        BankAccessData bankAccessData = new BankAccessData();
        bankAccessData.setBankAccess(bankAccessEntity);
        userData.getBankAccesses().add(bankAccessData);
//        when(uos.load(any(DocumentFQN.class), any(TypeReference.class))).thenReturn(Optional.of(userData));
        
        boolean deleteBankAccess = bds.deleteBankAccess(bankAccessEntity.getId());
        assertThat(deleteBankAccess).isEqualTo(true);
        thrown.expect(ResourceNotFoundException.class);
        isInstanceOf(BankAccessData.class, bds.load().bankAccessDataOrException(bankAccessEntity.getId()));
    }
}
