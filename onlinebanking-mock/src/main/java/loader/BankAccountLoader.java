package loader;

import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Service;

import domain.BankAccount;
import hbci4java.ExtBankAccount;

@Service
public class BankAccountLoader {

	public void update(Row row, Map<String, List<BankAccount>> mapBankAccountList, Map<String, ExtBankAccount> mapExtBankAccount) {
		// TODO Auto-generated method stub
		
	}

}
