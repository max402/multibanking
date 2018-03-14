package de.adorsys.multibanking.pers.docusafe.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import de.adorsys.multibanking.domain.BankAccessEntity;
import de.adorsys.multibanking.pers.docusafe.domain.BankAccessFile;
import de.adorsys.multibanking.pers.docusafe.domain.BankAccessRecord;
import de.adorsys.multibanking.pers.spi.repository.BankAccessRepositoryIf;

public class BankAccessRepositoryImpl implements BankAccessRepositoryIf {

	@Autowired
	private BankAccessFileRepositoryImpl bankAccessFileRepositoryImpl;
	
	@Autowired
	private BookingRepositoryImpl bookingRepositoryImpl;	
	
	@Override
	public BankAccessEntity findOne(String id) {
		Optional<BankAccessEntity> found = findByUserIdAndId(null, id);
		return found.isPresent()?found.get():null;
	}

	@Override
	public Optional<BankAccessEntity> findByUserIdAndId(String userId, String id) {
		BankAccessFile file = bankAccessFileRepositoryImpl.load();
		BankAccessRecord record = record(file, id);
		return Optional.ofNullable(record==null?null:record.getBankAccess()); 
	}

	@Override
	public List<BankAccessEntity> findByUserId(String userId) {
		BankAccessFile file = bankAccessFileRepositoryImpl.load();
		if(file==null)return Collections.emptyList();
		
		Collection<BankAccessRecord> values = file.getBankAccesses().values();
		ArrayList<BankAccessEntity> result = new ArrayList<>();
		for (BankAccessRecord bankAccessRecord : values) {
			result.add(bankAccessRecord.getBankAccess());
		}
		return result;
		
	}

	@Override
	public BankAccessEntity save(BankAccessEntity bankAccess) {
		BankAccessFile bankAccessFile = bankAccessFileRepositoryImpl.load();
		if(bankAccessFile==null){
			bankAccessFile = new BankAccessFile();
		}
		Map<String, BankAccessRecord> bankAccesses = bankAccessFile.getBankAccesses();
		
		// id
		if(StringUtils.isBlank(bankAccess.getId())){
			bankAccess.setId(UUID.randomUUID().toString());
		}
		BankAccessRecord bankAccessRecord = bankAccesses.get(bankAccess.getId());
		if(bankAccessRecord==null){
			bankAccessRecord = new BankAccessRecord();
			bankAccesses.put(bankAccess.getId(), bankAccessRecord);
		}
		bankAccessRecord.setBankAccess(bankAccess);
		
		bankAccessFileRepositoryImpl.save(bankAccessFile);
		return bankAccess;
	}

	@Override
	public String getBankCode(String id) {
		BankAccessEntity bankAccess = findOne(id);
		return bankAccess==null?null:bankAccess.getBankCode();
	}

	@Override
	public boolean exists(String accessId) {
		return findOne(accessId)!=null;
	}

	@Override
	public boolean deleteByUserIdAndBankAccessId(String userId, String bankAccessId) {
		BankAccessFile bankAccessFile = bankAccessFileRepositoryImpl.load();
		if(bankAccessFile==null) return false;
		Map<String, BankAccessRecord> bankAccesses = bankAccessFile.getBankAccesses();
		BankAccessRecord record = bankAccesses.remove(bankAccessId);
		if(record!=null){
			bankAccessFileRepositoryImpl.save(bankAccessFile);
			// TODO Delete bookings
			Set<String> accountIds = record.getBankAccounts().keySet();
			for (String accountId : accountIds) {
				bookingRepositoryImpl.deleteByAccountId(accountId);
			}
			return true;
		}
		return false;
	}
	
	private BankAccessRecord record(BankAccessFile file, String bankAccessId){
		return file==null?null:file.getBankAccesses().get(bankAccessId);
	}
}
