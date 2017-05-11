package de.adorsys.multibanking.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import de.adorsys.multibanking.domain.EncryptedAccountAnalyticsEntity;

/**
 * 
 * @author fpo
 *
 */
@Repository
public interface EncryptedAnalyticsRepository extends MongoRepository<EncryptedAccountAnalyticsEntity, String> {

    Optional<EncryptedAccountAnalyticsEntity> findLastByAccountId(String bankAccountId);

}
