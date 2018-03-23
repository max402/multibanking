package de.adorsys.multibanking.domain;

import java.time.LocalDateTime;
import java.util.Comparator;

import lombok.Data;

@Data
public class BookingFile  implements Comparator<BookingFile>{
	private String fileExt;
	private LocalDateTime lastAnalytics;
	private String lastAnalyticsVersion;
	private String lastAnalyticsTool;
	private int numberOfRecords;
	@Override
	public int compare(BookingFile bf1, BookingFile bf2) {
		return bf1.getFileExt().compareTo(bf2.getFileExt());
	}
}
