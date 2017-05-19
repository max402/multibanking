package de.adorsys.multibanking.loader;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.adorsys.multibanking.hbci4java.ExtBankAccount;
import de.adorsys.multibanking.hbci4java.OnlineBankingMockService;
import domain.BankAccess;
import domain.BankAccount;
import domain.Booking;

@Service
public class DataSheetLoader {

	@Autowired
	private BankAccesLoader bankAccesLoader;
	@Autowired
	private BankAccountLoader bankAccountLoader;
	@Autowired
	private BookingLoader bookingLoader;
	@Autowired
	private OnlineBankingMockService onlineBankingMockService;

	// bankLogin, List BankAccount
	Map<String, List<BankAccount>> mapBankAccountList = new HashMap<>();
	// accountId, List Booking
	Map<String, List<Booking>> mapBookingList = new HashMap<>();

	// bankLogin, bankAcces
	Map<String, BankAccess> mapBankAccess = new HashMap();
	// accountId, extBankAccount
	Map<String, ExtBankAccount> mapExtBankAccount = new HashMap<>();

	@PostConstruct
	public void init() {
		InputStream loadFile = loadFile();
		loadDataSheet(loadFile);
		addBankAccounts();
		addBooking();
	}

	public InputStream loadFile() {

		String dataSheetFile = "onlineBankingMock.xls";
		InputStream stream = DataSheetLoader.class.getResourceAsStream("/" + dataSheetFile);
		return stream;
	}

	public void loadDataSheet(InputStream dataStream) {

		try {
			HSSFWorkbook workbook = new HSSFWorkbook(dataStream);
			updateBankAccess(workbook);
			updateBankAccount(workbook);
			updateBooking(workbook);

			IOUtils.closeQuietly(dataStream);

		} catch (IOException e) {
			throw new IllegalStateException(e);
		} finally {
			IOUtils.closeQuietly(dataStream);
		}

	}

	public void addBankAccounts() {
		mapBankAccountList.forEach((bankLogin, listAccount) -> {
			BankAccess bankAccess = mapBankAccess.get(bankLogin);
			onlineBankingMockService.addBankAccounts(listAccount, bankAccess);
		});
	}

	public void addBooking() {
		mapBookingList.forEach((accountId, listBooking) -> {
			ExtBankAccount extbankAccount = mapExtBankAccount.get(accountId);
			BankAccess bankAccess = mapBankAccess.get(extbankAccount.getBankLogin());
			onlineBankingMockService.addBookings(listBooking, bankAccess, extbankAccount);
		});
	}

	public void updateBankAccess(HSSFWorkbook workbook) {
		HSSFSheet sheet = workbook.getSheet("BankAccess");
		if (sheet == null)
			return;

		Iterator<Row> rowIterator = sheet.rowIterator();
		rowIterator.next();

		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			bankAccesLoader.update(row, mapBankAccess);
		}
	}

	public void updateBankAccount(HSSFWorkbook workbook) {
		HSSFSheet sheet = workbook.getSheet("BankAccount");
		if (sheet == null)
			return;

		Iterator<Row> rowIterator = sheet.rowIterator();
		rowIterator.next();

		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			bankAccountLoader.update(row, mapBankAccountList, mapExtBankAccount);
		}
	}

	public void updateBooking(HSSFWorkbook workbook) {
		HSSFSheet sheet = workbook.getSheet("Booking");
		if (sheet == null)
			return;

		Iterator<Row> rowIterator = sheet.rowIterator();
		rowIterator.next();

		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			bookingLoader.update(row, mapBookingList);
		}
	}

}
