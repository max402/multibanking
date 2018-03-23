package de.adorsys.multibanking.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;

import de.adorsys.multibanking.domain.StandingOrderEntity;
import de.adorsys.multibanking.service.base.BaseService;
import de.adorsys.multibanking.utils.FQNUtils;

@Service
public class StandingOrderService extends BaseService {
	
	public List<StandingOrderEntity> load(String accessId, String accountId){
		return load(userIDAuth, FQNUtils.standingOrdersFQN(accessId, accountId), new TypeReference<List<StandingOrderEntity>>(){});
	}
}
