package de.adorsys.multibanking.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import de.adorsys.multibanking.domain.AnonymizedBookingEntity;
import de.adorsys.multibanking.domain.BookingEntity;
import de.adorsys.multibanking.service.base.BaseUserIdService;

/**
 * Created by alexg on 01.12.17.
 */
@Service
public class AnonymizationService extends BaseUserIdService {

    @Async
    public void anonymizeAndStoreBookingsAsync(String accessId, String accountId, List<BookingEntity> bookingEntities) {
//    	
//    	
//    	
//        List<BookingEntity> uncategorizedBookings = bookingEntities.stream()
//                .filter(bookingEntity -> bookingEntity.getBookingCategory() == null && bookingEntity.getCreditorId() != null)
//                .collect(Collectors.toList());
//
//        List<AnonymizedBookingEntity> anonymizedBookings = uncategorizedBookings.stream()
//                .map(bookingEntity -> anonymizeBooking(bookingEntity))
//                .collect(Collectors.toList());
//
//        try {
//        	load(userIDAuth, FQNUtils.anonymizedBookingFQN(accessId, accountId), new TypeReference<List<BookingEntity>>(){});
//            anonymizedBookingRepository.save(anonymizedBookings);
//        } catch (DuplicateKeyException e){
//            //ignore it
//        }
    }

    private AnonymizedBookingEntity anonymizeBooking(BookingEntity bookingEntity) {
        AnonymizedBookingEntity anonymizedBookingEntity = new AnonymizedBookingEntity();
        if (bookingEntity.getAmount().compareTo(BigDecimal.ZERO) == 1) {
            anonymizedBookingEntity.setAmount(new BigDecimal(1));
        } else {
            anonymizedBookingEntity.setAmount(new BigDecimal(-1));
        }
        anonymizedBookingEntity.setCreditorId(bookingEntity.getCreditorId());
        anonymizedBookingEntity.setPurpose(bookingEntity.getUsage());

        return anonymizedBookingEntity;
    }
}
