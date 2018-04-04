package de.adorsys.multibanking.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.adorsys.docusafe.business.types.complex.DocumentFQN;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;

import de.adorsys.multibanking.domain.UserEntity;
import de.adorsys.multibanking.service.base.BaseSystemIdService;
import de.adorsys.multibanking.service.base.StorageUserService;
import de.adorsys.multibanking.utils.DayFormat;
import de.adorsys.multibanking.utils.FQNUtils;
import de.adorsys.multibanking.utils.Ids;

/**
 * @author fpo 2018-03-24 02:02
 */
@Service
public class DeleteExpiredUsersService extends BaseSystemIdService {

    @Autowired
    StorageUserService storageUserService;
    @Autowired
    BankAccessService bankAccessService;

//    private static final Logger LOG = LoggerFactory.getLogger(DeleteExpiredUsersService.class);

	/**
	 * TODO: navigate to former days. Still have to be done.
	 * 
	 * @return
	 */
	public List<UserEntity> findExpiredUser() {
		Date now = new Date();
		DocumentFQN expireFileFQN = FQNUtils.expireDayFileFQN(DayFormat.printDay(new Date()));
		if(!documentExists(expireFileFQN, listType())) return Collections.emptyList();
		
		List<UserEntity> entities = load(expireFileFQN, listType())
				.orElse(Collections.emptyList());
		return entities.stream().filter(e -> e.getExpireUser().before(now)).collect(Collectors.toList());
	}
	
	/**
	 * Add or update expire information for this user.
	 * 
	 * @param userEntity
	 */
	public void scheduleExpiry(UserEntity userEntity){
		if(userEntity.getExpireUser()==null) return;
		
		// Compute file name
		DocumentFQN expireFileFQN = expireDayFileFQN(userEntity.getExpireUser());

		List<UserEntity> entities = load(expireFileFQN, listType())
				.orElse(Collections.emptyList());
		UserEntity persistent = entities.stream().filter(e -> Ids.eq(e.getId(), userEntity.getId())).findFirst()
			.orElse(userEntity);
		entities = new ArrayList<>(entities);
		if(persistent==userEntity){// intentional identity
			entities.add(userEntity);
		} else {
			BeanUtils.copyProperties(userEntity, persistent);
		}
		store(expireFileFQN, listType(), entities);
	}
	
	private static DocumentFQN expireDayFileFQN(Date date){
		return FQNUtils.expireDayFileFQN(DayFormat.printDay(date));
	}

	private static TypeReference<List<UserEntity>> listType(){
		return new TypeReference<List<UserEntity>>() {};
	}
}
