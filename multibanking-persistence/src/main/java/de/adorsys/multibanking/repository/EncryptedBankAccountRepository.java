package de.adorsys.multibanking.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import de.adorsys.multibanking.domain.EncryptedBankAccountEntity;

/**
 * 
 * @author fpo
 *
 */
@Repository
public interface EncryptedBankAccountRepository extends MongoRepository<EncryptedBankAccountEntity, String> {

    List<EncryptedBankAccountEntity> findByBankAccessId(String bankAccessId);

    Optional<EncryptedBankAccountEntity> findById(String id);
}
