package de.adorsys.multibanking.loader;

import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Service;

import de.adorsys.multibanking.hbci4java.ExtBankAccount;
import domain.BankAccount;

@Service
public class BankAccountLoader {

	public void update(Row row, Map<String, List<BankAccount>> mapBankAccountList, Map<String, ExtBankAccount> mapExtBankAccount) {
		// TODO Auto-generated method stub
		
	}

}
