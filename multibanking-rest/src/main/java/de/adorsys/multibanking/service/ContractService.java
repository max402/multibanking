package de.adorsys.multibanking.service;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import de.adorsys.multibanking.domain.ContractEntity;
import de.adorsys.multibanking.service.base.BaseService;
import de.adorsys.multibanking.utils.FQNUtils;

public class ContractService extends BaseService {

	public List<ContractEntity> getContracts(String accessId, String accountId) {
		return load(userIDAuth, FQNUtils.contractsFQN(accessId, accountId), new TypeReference<List<ContractEntity>>() {});
	}
}
