package de.adorsys.multibanking.loader;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import de.adorsys.multibanking.hbci4java.ExtBankAccount;
import domain.BankAccount;
import domain.BankAccountBalance;

@Service
public class BankAccountLoader {

	@SuppressWarnings("deprecation")
	public void update(Row row, Map<String, List<BankAccount>> mapBankAccountList,
			Map<String, ExtBankAccount> mapExtBankAccount) {

		ExtBankAccount extBankAccount = new ExtBankAccount();

		Cell cell = row.getCell(0);
		if (cell != null && StringUtils.isNotBlank(cell.getStringCellValue())) {
			extBankAccount.countryHbciAccount(cell.getStringCellValue().trim());
		} else {
			return;
		}

		cell = row.getCell(1);
		if (cell != null && StringUtils.isNotBlank(cell.getStringCellValue())) {
			extBankAccount.blzHbciAccount(cell.getStringCellValue().trim());
		} else {
			return;
		}

		cell = row.getCell(2);
		if (cell != null && StringUtils.isNotBlank(cell.getStringCellValue())) {
			extBankAccount.numberHbciAccount(cell.getStringCellValue().trim());
		} else {
			return;
		}

		cell = row.getCell(3);
		if (cell != null && StringUtils.isNotBlank(cell.getStringCellValue())) {
			extBankAccount.typeHbciAccount(cell.getStringCellValue().trim());
		} else {
			return;
		}

		cell = row.getCell(4);
		if (cell != null && StringUtils.isNotBlank(cell.getStringCellValue())) {
			extBankAccount.currencyHbciAccount(cell.getStringCellValue().trim());
		} else {
			return;
		}

		cell = row.getCell(5);
		if (cell != null && StringUtils.isNotBlank(cell.getStringCellValue())) {
			extBankAccount.nameHbciAccount(cell.getStringCellValue().trim());
		} else {
			return;
		}

		cell = row.getCell(6);
		if (cell != null && StringUtils.isNotBlank(cell.getStringCellValue())) {
			extBankAccount.bicHbciAccount(cell.getStringCellValue().trim());
		} else {
			return;
		}

		cell = row.getCell(7);
		if (cell != null && StringUtils.isNotBlank(cell.getStringCellValue())) {
			extBankAccount.ibanHbciAccount(cell.getStringCellValue().trim());
		} else {
			return;
		}

		cell = row.getCell(8);
		if (cell != null && StringUtils.isNotBlank(cell.getStringCellValue())) {
			extBankAccount.setBankLogin(cell.getStringCellValue().trim());
		} else {
			return;
		}

		cell = row.getCell(9);
		if (cell != null && StringUtils.isNotBlank(cell.getStringCellValue())) {
			extBankAccount.setAccountId(cell.getStringCellValue().trim());
		} else {
			return;
		}

		extBankAccount.bankAccountBalance(new BankAccountBalance());
		cell = row.getCell(10);
		if (cell != null) {
			cell.setCellType(Cell.CELL_TYPE_NUMERIC);
			BigDecimal decimal = new BigDecimal(cell.getNumericCellValue());
			extBankAccount.getBankAccountBalance().readyHbciBalance(decimal);
		} else {
			return;
		}

		cell = row.getCell(11);
		if (cell != null) {
			cell.setCellType(Cell.CELL_TYPE_NUMERIC);
			BigDecimal decimal = new BigDecimal(cell.getNumericCellValue());
			extBankAccount.getBankAccountBalance().unreadyHbciBalance(decimal);
		} else {
			return;
		}

		cell = row.getCell(12);
		if (cell != null) {
			cell.setCellType(Cell.CELL_TYPE_NUMERIC);
			BigDecimal decimal = new BigDecimal(cell.getNumericCellValue());
			extBankAccount.getBankAccountBalance().creditHbciBalance(decimal);
		} else {
			return;
		}

		cell = row.getCell(13);
		if (cell != null) {
			cell.setCellType(Cell.CELL_TYPE_NUMERIC);
			BigDecimal decimal = new BigDecimal(cell.getNumericCellValue());
			extBankAccount.getBankAccountBalance().availableHbciBalance(decimal);
		} else {
			return;
		}

		cell = row.getCell(14);
		if (cell != null) {
			cell.setCellType(Cell.CELL_TYPE_NUMERIC);
			BigDecimal decimal = new BigDecimal(cell.getNumericCellValue());
			extBankAccount.getBankAccountBalance().usedHbciBalance(decimal);
		} else {
			return;
		}

		mapExtBankAccount.put(extBankAccount.getAccountId(), extBankAccount);

		String bankLogin = extBankAccount.getBankLogin();
		List<BankAccount> listBankAccount = mapBankAccountList.get(bankLogin);
		if (CollectionUtils.isEmpty(listBankAccount)) {
			listBankAccount = new ArrayList<>();
			listBankAccount.add(extBankAccount);
			mapBankAccountList.put(bankLogin, listBankAccount);
		} else {
			listBankAccount.add(extBankAccount);
		}

	}

}
