package de.adorsys.multibanking.pers.docusafe.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.adorsys.docusafe.business.types.complex.DocumentFQN;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import de.adorsys.multibanking.domain.BookingEntity;
import de.adorsys.multibanking.pers.spi.repository.BookingRepositoryIf;
import domain.BankApi;

@Service
public class BookingRepositoryImpl extends BaseRepositoryImpl implements BookingRepositoryIf {
	private static final String RESOURCE_FILE = "Bookings";

	@Override
	public List<BookingEntity> findByUserIdAndAccountIdAndBankApi(String userId, String bankAccountId,
			BankApi bankApi) {
		BookingEntity[] bookings = load();
		if(bookings==null) return Collections.emptyList();
		List<BookingEntity> result = new ArrayList<>();
		for (BookingEntity bookingEntity : bookings) {
			if(bankApi==bookingEntity.getBankApi() && StringUtils.equalsAnyIgnoreCase(bankAccountId, bookingEntity.getAccountId())){
				result.add(bookingEntity);
			}
		}
		return result;
	}

	@Override
	public Optional<BookingEntity> findByUserIdAndId(String userId, String bookingId) {
		BookingEntity[] bookings = load();
		if(bookings==null) return Optional.empty();
		for (BookingEntity bookingEntity : bookings) {
			if(StringUtils.equalsAnyIgnoreCase(bookingId, bookingEntity.getId())){
				return Optional.of(bookingEntity);
			}
		}
		return Optional.empty();
	}

	@Override
	public List<BookingEntity> save(List<BookingEntity> bookingEntities) {
		Map<String, BookingEntity> bookingMap = loadMap();
		// Splitt the list in booking with id and booking without id
		for (BookingEntity bookingEntity : bookingEntities) {
			if(StringUtils.isBlank(bookingEntity.getId())){
				bookingEntity.setId(UUID.randomUUID().toString());
			}
			bookingMap.put(bookingEntity.getId(), bookingEntity);
		}
		store(bookingMap);
		return bookingEntities;
	}

	@Override
	public void deleteByAccountId(String id) {
		Map<String, BookingEntity> bookingMap = loadMap();
		BookingEntity remove = bookingMap.remove(id);
		if(remove!=null) store(bookingMap);
	}
	
	private BookingEntity[] load(){
		return userDocumentRepository.read(documentFQN(), BookingEntity[].class);
	}
	
	private Map<String, BookingEntity> loadMap(){
		// Index the collection by position in the list.
		BookingEntity[] bookings = load();
		Map<String, BookingEntity> bookingMap = new HashMap<>();
		for (BookingEntity bookingEntity : bookings) {
			bookingMap.put(bookingEntity.getId(), bookingEntity);
		}
		return bookingMap;
	}
	
	private void store(Map<String, BookingEntity> map){
		ArrayList<BookingEntity> arrayList = new ArrayList<>();
		arrayList.addAll(map.values());
		userDocumentRepository.write(documentFQN(), arrayList.toArray(new BookingEntity[arrayList.size()]));
	}

	private DocumentFQN documentFQN(){
		return new DocumentFQN(RESOURCE_FILE);
	}
}
