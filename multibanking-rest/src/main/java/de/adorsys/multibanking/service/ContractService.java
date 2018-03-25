package de.adorsys.multibanking.service;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;

import de.adorsys.multibanking.domain.ContractEntity;
import de.adorsys.multibanking.service.base.BaseUserIdService;
import de.adorsys.multibanking.utils.FQNUtils;

@Service
public class ContractService extends BaseUserIdService {

	public List<ContractEntity> getContracts(String accessId, String accountId) {
		return load(userIDAuth, FQNUtils.contractsFQN(accessId, accountId), listType())
			.orElse(Collections.emptyList());
	}

	private static TypeReference<List<ContractEntity>> listType(){
		return new TypeReference<List<ContractEntity>>() {};
	}
}
