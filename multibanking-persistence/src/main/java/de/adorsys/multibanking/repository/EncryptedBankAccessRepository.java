package de.adorsys.multibanking.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import de.adorsys.multibanking.domain.EncryptedBankAccessEntity;

/**
 * 
 * @author fpo
 *
 */
@Repository
public interface EncryptedBankAccessRepository extends MongoRepository<EncryptedBankAccessEntity, String> {

    Optional<EncryptedBankAccessEntity> findById(String id);

    List<EncryptedBankAccessEntity> findByUserId(String userId);
}
