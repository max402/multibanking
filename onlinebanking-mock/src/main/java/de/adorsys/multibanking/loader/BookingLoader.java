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

import de.adorsys.multibanking.hbci4java.ExtBooking;
import domain.Booking;

@Service
public class BookingLoader {

	public void update(Row row, Map<String, List<Booking>> mapBookingList) {

		ExtBooking extBooking = new ExtBooking();

		Cell cell = row.getCell(0);
		if (cell != null && StringUtils.isNotBlank(cell.getStringCellValue())) {
			extBooking.externalId(cell.getStringCellValue().trim());
		} else {
			return;
		}

		cell = row.getCell(1);
		if (cell != null) {
			extBooking.valutaDate(cell.getDateCellValue());
		} else {
			return;
		}

		cell = row.getCell(2);
		if (cell != null) {
			extBooking.bookingDate(cell.getDateCellValue());
		} else {
			return;
		}

		cell = row.getCell(3);
		if (cell != null) {
			cell.setCellType(Cell.CELL_TYPE_NUMERIC);
			BigDecimal decimal = new BigDecimal(cell.getNumericCellValue());
			extBooking.amount(decimal);
		} else {
			return;
		}

		cell = row.getCell(4);
		if (cell != null) {
			cell.setCellType(Cell.CELL_TYPE_BOOLEAN);
			extBooking.reversal(cell.getBooleanCellValue());
		} else {
			return;
		}

		cell = row.getCell(5);
		if (cell != null) {
			cell.setCellType(Cell.CELL_TYPE_NUMERIC);
			BigDecimal decimal = new BigDecimal(cell.getNumericCellValue());
			extBooking.balance(decimal);
		} else {
			return;
		}

		cell = row.getCell(6);
		if (cell != null && StringUtils.isNotBlank(cell.getStringCellValue())) {
			extBooking.customerRef(cell.getStringCellValue().trim());
		} else {
			return;
		}

		cell = row.getCell(7);
		if (cell != null && StringUtils.isNotBlank(cell.getStringCellValue())) {
			extBooking.instRef(cell.getStringCellValue().trim());
		} else {
			return;
		}

		cell = row.getCell(8);
		if (cell != null) {
			cell.setCellType(Cell.CELL_TYPE_NUMERIC);
			BigDecimal decimal = new BigDecimal(cell.getNumericCellValue());
			extBooking.origValue(decimal);
		} else {
			return;
		}

		cell = row.getCell(9);
		if (cell != null) {
			cell.setCellType(Cell.CELL_TYPE_NUMERIC);
			BigDecimal decimal = new BigDecimal(cell.getNumericCellValue());
			extBooking.chargeValue(decimal);
		} else {
			return;
		}

		cell = row.getCell(10);
		if (cell != null && StringUtils.isNotBlank(cell.getStringCellValue())) {
			extBooking.additional(cell.getStringCellValue().trim());
		} else {
			return;
		}

		cell = row.getCell(11);
		if (cell != null && StringUtils.isNotBlank(cell.getStringCellValue())) {
			extBooking.text(cell.getStringCellValue().trim());
		} else {
			return;
		}

		cell = row.getCell(12);
		if (cell != null && StringUtils.isNotBlank(cell.getStringCellValue())) {
			extBooking.primanota(cell.getStringCellValue().trim());
		} else {
			return;
		}

		cell = row.getCell(13);
		if (cell != null && StringUtils.isNotBlank(cell.getStringCellValue())) {
			extBooking.usage(cell.getStringCellValue().trim());
		} else {
			return;
		}

		cell = row.getCell(14);
		if (cell != null && StringUtils.isNotBlank(cell.getStringCellValue())) {
			extBooking.addkey(cell.getStringCellValue().trim());
		} else {
			return;
		}

		cell = row.getCell(15);
		if (cell != null) {
			cell.setCellType(Cell.CELL_TYPE_BOOLEAN);
			extBooking.sepa(cell.getBooleanCellValue());
		} else {
			return;
		}

		cell = row.getCell(16);
		if (cell != null && StringUtils.isNotBlank(cell.getStringCellValue())) {
			extBooking.setAccountId(cell.getStringCellValue().trim());
		} else {
			return;
		}

		cell = row.getCell(17);
		if (cell != null && StringUtils.isNotBlank(cell.getStringCellValue())) {
			extBooking.setBankLogin(cell.getStringCellValue().trim());
		} else {
			return;
		}

		String accountId = extBooking.getAccountId();
		List<Booking> listBooking = mapBookingList.get(accountId);
		if (CollectionUtils.isEmpty(listBooking)) {
			listBooking = new ArrayList<>();
			listBooking.add(extBooking);
			mapBookingList.put(accountId, listBooking);
		} else {
			listBooking.add(extBooking);
		}

	}

}
