package de.adorsys.multibanking.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import de.adorsys.multibanking.domain.common.AbstractId;
import domain.BankAccount.SyncStatus;
import lombok.Data;

/**
 * Stores the summary of an account synch. File resides in the directory of the containing account.
 * 
 * @author fpo 2018-03-20 04:13
 *
 */
@Data
public class AccountSynchResult extends AbstractId {
	
	/*
	 * List of booking file info
	 */
	private List<BookingFile> bookingFileExts = new ArrayList<>();
	
	private SyncStatus syncStatus;
	private LocalDateTime statusTime;
	
	private LocalDateTime lastSynch;
	
	public AccountSynchResult update(Collection<BookingFile> newEntries) {
		Map<String, BookingFile> map = bookingPeriodsMap(bookingFileExts);
		map.putAll(bookingPeriodsMap(newEntries));
		bookingFileExts = map.values().stream().sorted().collect(Collectors.toList());
		return this;
	}
	
	private static Map<String, BookingFile> bookingPeriodsMap(Collection<BookingFile> bookingFileExts){
		return bookingFileExts.stream().collect(Collectors.toMap(BookingFile::getPeriod, Function.identity()));
	}

	public Map<String, BookingFile> bookingFileMap() {
		return bookingPeriodsMap(bookingFileExts);
	}
}
