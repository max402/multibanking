package de.adorsys.multibanking.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;

import de.adorsys.multibanking.auth.SystemIDAuth;
import de.adorsys.multibanking.domain.BankEntity;
import de.adorsys.multibanking.service.base.BaseService;
import de.adorsys.multibanking.utils.FQNUtils;

@Service
public class BankService extends BaseService {
	@Autowired
	private SystemIDAuth systemIDAuth;

	public Optional<BankEntity> findByBankCode(String bankCode) {
		return load().stream().filter(b -> StringUtils.equalsAnyIgnoreCase(bankCode, b.getBankCode())).findFirst();
	}
	
	public List<BankEntity> load(){
		return load(systemIDAuth.getUserIDAuth(), FQNUtils.banksFQN(), new TypeReference<List<BankEntity>>() {});
	}
	
}
