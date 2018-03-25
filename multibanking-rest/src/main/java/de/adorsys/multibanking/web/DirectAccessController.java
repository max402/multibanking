package de.adorsys.multibanking.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.adorsys.multibanking.domain.AccountSynchPref;
import de.adorsys.multibanking.domain.AccountSynchResult;
import de.adorsys.multibanking.domain.BankAccessEntity;
import de.adorsys.multibanking.domain.BankAccountEntity;
import de.adorsys.multibanking.domain.BookingEntity;
import de.adorsys.multibanking.domain.BookingFile;
import de.adorsys.multibanking.domain.BookingPeriod;
import de.adorsys.multibanking.exception.ResourceNotFoundException;
import de.adorsys.multibanking.service.AccountSynchService;
import de.adorsys.multibanking.service.BookingService;
import de.adorsys.multibanking.service.UserService;
import de.adorsys.multibanking.web.common.BankAccountBasedController;
import domain.BankAccountBalance;
import domain.BankApi;
import lombok.Data;

/**
 * @author alexg on 27.06.17.
 * @author fpo 2018-03-20 12:46
 */
@UserResource
@RestController
@RequestMapping(path = "api/v1/direct")
public class DirectAccessController extends BankAccountBasedController {
    @Autowired
    private BookingService bookingService;
    @Autowired
    private UserService userService;
    @Autowired
    private AccountSynchService accountSynchService;

    @Value("${threshold_temporaryData:15}")
    private Integer thresholdTemporaryData;

    @RequestMapping(value = "/accounts", method = RequestMethod.PUT)
    public ResponseEntity<List<BankAccountEntity>> loadBankAccounts(@RequestBody BankAccessEntity bankAccess, @RequestParam(required = false) BankApi bankApi) {
        //temporary user, will be deleted after x minutes
    	Date expire = DateUtils.addMinutes(new Date(), thresholdTemporaryData);
        userService.createUser(expire);
        
        bankAccess.setStorePin(false);
        bankAccess.setTemporary(true);
        // Creating a bank access also load accounts.
        bankAccessService.createBankAccess(bankAccess);
        AccountSynchPref pref = accountSynchService.loadAccessLevelSynchPref(bankAccess.getId());
        pref.setBookingPeriod(BookingPeriod.ALL);
        accountSynchService.storeAccessLevelSynchPref(bankAccess.getId(), pref);
        
        return returnDocument(bankAccountService.loadForBankAccess(bankAccess.getId()));
    }

    @RequestMapping(path = "/bookings", method = RequestMethod.PUT)
    public ResponseEntity<LoadBookingsResponse> loadBookings(@RequestBody LoadBookingsRequest loadBookingsRequest, @RequestParam(required = false) BankApi bankApi) {
        BankAccessEntity bankAccessEntity = bankAccessService.loadbankAccess(loadBookingsRequest.getAccessId())
        		.orElseThrow(() -> new ResourceNotFoundException(BankAccessEntity.class, loadBookingsRequest.getAccessId()));

        BankAccountEntity bankAccountEntity = bankAccountService.loadBankAccount(bankAccessEntity.getId(),loadBookingsRequest.getAccountId())
        		.orElseThrow(() -> new ResourceNotFoundException(BankAccountEntity.class, loadBookingsRequest.getAccountId()));

        bookingService.syncBookings(bankAccessEntity, bankAccountEntity, bankApi, loadBookingsRequest.getPin());
        
        AccountSynchResult synchResult = accountSynchService.loadAccountSynchResult(bankAccessEntity.getId(), bankAccountEntity.getId());
        List<BookingFile> bookingFiles = synchResult.getBookingFileExts();
        List<BookingEntity> bookings = new ArrayList<BookingEntity>();
        bookingFiles.forEach(bfe -> {
        	bookings.addAll(bookingService.listBookings(bankAccessEntity.getId(), bankAccountEntity.getId(), bfe.getFileExt()));
        });

        LoadBookingsResponse loadBookingsResponse = new LoadBookingsResponse();
        loadBookingsResponse.setBookings(bookings);
        loadBookingsResponse.setBalance(bankAccountEntity.getBankAccountBalance());

        return returnDocument(loadBookingsResponse);
    }

    @Data
    private static class LoadBookingsRequest {
        String accessId;
        String accountId;
        String pin;
    }

    @Data
    private static class LoadBookingsResponse {
        BankAccountBalance balance;
        List<BookingEntity> bookings;
    }

}
