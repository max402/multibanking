package de.adorsys.multibanking.pers.docusafe.repository;

import java.util.List;
import java.util.Optional;

import org.adorsys.docusafe.business.types.complex.DocumentFQN;
import org.springframework.stereotype.Service;

import de.adorsys.multibanking.domain.UserEntity;
import de.adorsys.multibanking.pers.spi.repository.UserRepositoryIf;

@Service
public class UserRepositoryImpl extends BaseRepositoryImpl implements UserRepositoryIf{
	
	private static final String RESOURCE_FILE = "User";
			
	@Override
	public Optional<UserEntity> findById(String id) {
		return Optional.ofNullable(userDocumentRepository.read(documentFQN(), UserEntity.class));
	}

	@Override
	public List<String> findExpiredUser() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean exists(String userId) {
		return userDocumentRepository.exists(documentFQN());
	}

	@Override
	public void save(UserEntity userEntity) {
		userDocumentRepository.write(documentFQN(), userEntity);
	}

	@Override
	public void delete(String userId) {
		userDocumentRepository.removeUser();
	}
	
	private DocumentFQN documentFQN(){
		return new DocumentFQN(RESOURCE_FILE);
	}
}
