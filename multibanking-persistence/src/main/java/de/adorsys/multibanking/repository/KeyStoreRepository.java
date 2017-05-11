package de.adorsys.multibanking.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import de.adorsys.multibanking.domain.KeyStoreEntity;

/**
 * Stores keystore in a mongo db.
 * 
 * @author fpo
 *
 */
@Repository
public interface KeyStoreRepository extends MongoRepository<KeyStoreEntity, String> {
}
