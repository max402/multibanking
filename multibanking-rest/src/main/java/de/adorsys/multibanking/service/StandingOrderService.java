package de.adorsys.multibanking.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.adorsys.docusafe.business.types.complex.DocumentFQN;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;

import de.adorsys.multibanking.domain.BankAccountEntity;
import de.adorsys.multibanking.domain.StandingOrderEntity;
import de.adorsys.multibanking.service.base.BaseUserIdService;
import de.adorsys.multibanking.utils.FQNUtils;
import domain.StandingOrder;

@Service
public class StandingOrderService extends BaseUserIdService {
	
	public List<StandingOrderEntity> load(String accessId, String accountId){
		return load(FQNUtils.standingOrdersFQN(accessId, accountId), listType())
				.orElse(Collections.emptyList());
	}

    public void saveStandingOrders(BankAccountEntity bankAccount, List<StandingOrder> standingOrders) {
        List<StandingOrderEntity> standingOrderEntities = standingOrders.stream()
                .map(booking -> {
                    StandingOrderEntity target = new StandingOrderEntity();
                    BeanUtils.copyProperties(booking, target);
                    target.setAccountId(bankAccount.getId());
                    target.setUserId(bankAccount.getUserId());
                    return target;
                })
                .collect(Collectors.toList());
        DocumentFQN standingOrdersFQN = FQNUtils.standingOrdersFQN(bankAccount.getBankAccessId(), bankAccount.getId());
        store(standingOrdersFQN, listType(), standingOrderEntities);
    }

	private static TypeReference<List<StandingOrderEntity>> listType(){
		return new TypeReference<List<StandingOrderEntity>>() {};
	}
}
