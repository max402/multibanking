package de.adorsys.multibanking.service;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;

import de.adorsys.multibanking.domain.StandingOrderEntity;
import de.adorsys.multibanking.service.base.BaseUserIdService;
import de.adorsys.multibanking.utils.FQNUtils;

@Service
public class StandingOrderService extends BaseUserIdService {
	
	public List<StandingOrderEntity> load(String accessId, String accountId){
		return load(userIDAuth, FQNUtils.standingOrdersFQN(accessId, accountId), listType())
				.orElse(Collections.emptyList());
	}

	private static TypeReference<List<StandingOrderEntity>> listType(){
		return new TypeReference<List<StandingOrderEntity>>() {};
	}
}
