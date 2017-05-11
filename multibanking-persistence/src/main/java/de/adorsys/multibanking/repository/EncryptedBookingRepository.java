package de.adorsys.multibanking.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import de.adorsys.multibanking.domain.EncryptedBookingEntity;

/**
 * 
 * @author fpo
 *
 */
@Repository
public interface EncryptedBookingRepository extends MongoRepository<EncryptedBookingEntity, String> {

    List<EncryptedBookingEntity> findByAccountId(String bankAccountId);

    Optional<EncryptedBookingEntity> findById(String bookingId);


}
