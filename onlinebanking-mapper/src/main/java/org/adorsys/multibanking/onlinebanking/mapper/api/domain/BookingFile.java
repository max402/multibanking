package org.adorsys.multibanking.onlinebanking.mapper.api.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.Comparator;

/**
 * Hold descriptive information on a booking file.
 * 
 * @author fpo
 *
 */
@Data
public class BookingFile implements Comparator<BookingFile> {
	private String period;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime lastUpdate;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime lastAnalytics;
	private String lastAnalyticsVersion;
	private String lastAnalyticsTool;
	private int numberOfRecords;
	@Override
	public int compare(BookingFile bf1, BookingFile bf2) {
		return StringUtils.compare(bf1.period,bf2.period);
	}
}
