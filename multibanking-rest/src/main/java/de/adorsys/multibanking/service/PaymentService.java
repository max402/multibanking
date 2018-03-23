package de.adorsys.multibanking.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;

import de.adorsys.multibanking.domain.BankAccessEntity;
import de.adorsys.multibanking.domain.BankAccountEntity;
import de.adorsys.multibanking.domain.BankEntity;
import de.adorsys.multibanking.domain.PaymentEntity;
import de.adorsys.multibanking.exception.ResourceNotFoundException;
import de.adorsys.multibanking.exception.domain.MissingPinException;
import de.adorsys.multibanking.pers.spi.repository.PaymentRepositoryIf;
import de.adorsys.multibanking.service.base.BaseService;
import de.adorsys.multibanking.utils.FQNUtils;
import domain.BankApiUser;
import domain.Payment;
import exception.PaymentException;
import spi.OnlineBankingService;

/**
 * @author alexg on 20.10.17.
 * @author fpo 2018-03-23 03:53
 * 
 */
@Service
public class PaymentService extends BaseService {

    @Autowired
    private OnlineBankingServiceProducer bankingServiceProducer;
    @Autowired
    private UserService userService;
    @Autowired
    private BankService bankService;
    @Autowired
    private PaymentRepositoryIf paymentRepository;

    public PaymentEntity createPayment(BankAccessEntity bankAccess, BankAccountEntity bankAccount, String pin, Payment payment) {
        OnlineBankingService bankingService = bankingServiceProducer.getBankingService(bankAccess.getBankCode());

        BankApiUser bankApiUser = userService.checkApiRegistration(bankingService.bankApi(), bankAccess.getBankCode());

        pin = pin == null ? bankAccess.getPin() : pin;
        if (pin == null) {
            throw new MissingPinException();
        }

        String mappedBlz = bankService.findByBankCode(bankAccess.getBankCode())
                .orElseThrow(() -> new ResourceNotFoundException(BankEntity.class, bankAccess.getBankCode())).getBlzHbci();

        try {
            bankingService.createPayment(bankApiUser, bankAccess, mappedBlz, bankAccount, pin, payment);
        } catch (PaymentException e) {
            throw new de.adorsys.multibanking.exception.PaymentException(e.getMessage());
        }

        PaymentEntity target = new PaymentEntity();
        BeanUtils.copyProperties(payment, target);
        target.setUserId(bankAccess.getUserId());
        target.setCreatedDateTime(new Date());

        paymentRepository.save(target);
        return target;
    }

    public void submitPayment(PaymentEntity paymentEntity, String bankCode, String tan) {
        OnlineBankingService bankingService = bankingServiceProducer.getBankingService(bankCode);

        try {
            bankingService.submitPayment(paymentEntity, tan);
        } catch (PaymentException e) {
            throw new de.adorsys.multibanking.exception.PaymentException(e.getMessage());
        }

        paymentRepository.delete(paymentEntity.getId());
    }
    
    public List<PaymentEntity> loadPayments(String accessId, String accountId){
    	return load(userIDAuth, FQNUtils.paymentsFQN(accessId, accountId), new TypeReference<List<PaymentEntity>>() {});
    }
    
    public Optional<PaymentEntity> findPayments(String accessId, String accountId, String paymentId){
    	List<PaymentEntity> payments = load(userIDAuth, FQNUtils.paymentsFQN(accessId, accountId), new TypeReference<List<PaymentEntity>>() {});
    	return payments.stream().filter(p -> StringUtils.equalsAnyIgnoreCase(paymentId, p.getId())).findFirst();
    }
}
