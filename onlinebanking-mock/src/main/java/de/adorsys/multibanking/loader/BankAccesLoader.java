package de.adorsys.multibanking.loader;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Service;

import domain.BankAccess;

@Service
public class BankAccesLoader {

	public void update(Row row, Map<String, BankAccess> mapBankAccess) {
		
		BankAccess bankAccess = new BankAccess();
		
		Cell cell = row.getCell(0);
		if(cell != null && StringUtils.isNotBlank(cell.getStringCellValue())){	
			bankAccess.bankName(cell.getStringCellValue().trim());
		}else{
			return;
		}
		
		cell = row .getCell(1);
		if(cell != null && StringUtils.isNotBlank(cell.getStringCellValue())){	
			bankAccess.bankLogin(cell.getStringCellValue().trim());
		}else{
			return;
		}
		
		cell = row .getCell(2);
		if(cell != null && StringUtils.isNotBlank(cell.getStringCellValue())){	
			bankAccess.bankCode(cell.getStringCellValue().trim());
		}else{
			return;
		}
		
		cell = row .getCell(3);
		if(cell != null && StringUtils.isNotBlank(cell.getStringCellValue())){	
			bankAccess.passportState(cell.getStringCellValue().trim());
		}else{
			return;
		}
		
		mapBankAccess.put(bankAccess.getBankLogin(), bankAccess);
	}

}
