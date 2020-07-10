package de.adorsys.multibanking.service;

import de.adorsys.multibanking.domain.BankAccessEntity;
import de.adorsys.multibanking.domain.BankApiUser;
import de.adorsys.multibanking.domain.BankEntity;
import de.adorsys.multibanking.domain.BulkPaymentEntity;
import de.adorsys.multibanking.domain.Credentials;
import de.adorsys.multibanking.domain.RawSepaTransactionEntity;
import de.adorsys.multibanking.domain.SinglePaymentEntity;
import de.adorsys.multibanking.domain.exception.MultibankingException;
import de.adorsys.multibanking.domain.request.TransactionRequest;
import de.adorsys.multibanking.domain.request.TransactionRequestFactory;
import de.adorsys.multibanking.domain.request.UpdatePsuAuthenticationRequest;
import de.adorsys.multibanking.domain.response.AbstractResponse;
import de.adorsys.multibanking.domain.response.UpdateAuthResponse;
import de.adorsys.multibanking.domain.spi.OnlineBankingService;
import de.adorsys.multibanking.domain.transaction.BulkPayment;
import de.adorsys.multibanking.domain.transaction.RawSepaPayment;
import de.adorsys.multibanking.domain.transaction.SinglePayment;
import de.adorsys.multibanking.exception.domain.MissingPinException;
import de.adorsys.multibanking.hbci.model.HbciConsent;
import de.adorsys.multibanking.pers.spi.repository.BulkPaymentRepositoryIf;
import de.adorsys.multibanking.pers.spi.repository.RawSepaTransactionRepositoryIf;
import de.adorsys.multibanking.pers.spi.repository.SinglePaymentRepositoryIf;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;

@RequiredArgsConstructor
@Service
public class PaymentService {

    private final OnlineBankingServiceProducer bankingServiceProducer;
    private final UserService userService;
    private final BankService bankService;
    private final RawSepaTransactionRepositoryIf rawSepaTransactionRepository;
    private final SinglePaymentRepositoryIf singlePaymentRepository;
    private final BulkPaymentRepositoryIf bulkPaymentRepository;

    RawSepaTransactionEntity createSepaRawPayment(BankAccessEntity bankAccess, Credentials credentials,
                                                  RawSepaPayment payment) {
        OnlineBankingService bankingService = bankingServiceProducer.getBankingService(bankAccess.getBankCode());

        BankApiUser bankApiUser = userService.checkApiRegistration(bankingService,
            userService.findUser(bankAccess.getUserId()));

        if (credentials.getPin() == null) {
            throw new MissingPinException();
        }

        BankEntity bankEntity = bankService.findBank(bankAccess.getBankCode());

        try {
            TransactionRequest<RawSepaPayment> request =
                TransactionRequestFactory.create(payment, bankApiUser, bankAccess, bankEntity, null);

            AbstractResponse response = bankingService.executePayment(request);

            RawSepaTransactionEntity target = new RawSepaTransactionEntity();
            BeanUtils.copyProperties(payment, target);
            target.setUserId(bankAccess.getUserId());
            target.setCreatedDateTime(new Date());
            target.setTanSubmitExternal(response.getAuthorisationCodeResponse().getTanSubmit());

            rawSepaTransactionRepository.save(target);
            return target;
        } catch (MultibankingException e) {
            throw new de.adorsys.multibanking.exception.PaymentException(e.getMessage());
        }
    }

    public SinglePaymentEntity createSinglePayment(BankAccessEntity bankAccess, Credentials credentials,
                                                   SinglePayment payment) {
        OnlineBankingService bankingService = bankingServiceProducer.getBankingService(bankAccess.getBankCode());

        BankApiUser bankApiUser = userService.checkApiRegistration(bankingService,
            userService.findUser(bankAccess.getUserId()));

        if (credentials.getPin() == null) {
            throw new MissingPinException();
        }

        BankEntity bankEntity = bankService.findBank(bankAccess.getBankCode());

        try {
            HbciConsent consent = new HbciConsent();
            consent.setHbciProduct(null);
            consent.setCredentials(Credentials.builder()
                .userId(credentials.getUserId())
                .pin(credentials.getPin())
                .build());

            UpdatePsuAuthenticationRequest updateRequest = new UpdatePsuAuthenticationRequest();
            updateRequest.setCredentials(credentials);
            updateRequest.setBankApiConsentData(consent);
            updateRequest.setBank(bankEntity);

            UpdateAuthResponse updateResponse =
                bankingService.getStrongCustomerAuthorisation().updatePsuAuthentication(updateRequest);

            consent = (HbciConsent) updateResponse.getBankApiConsentData();

            TransactionRequest<SinglePayment> request =
                TransactionRequestFactory.create(payment, bankApiUser, bankAccess, bankEntity, consent);

            AbstractResponse response = bankingService.executePayment(request);

            SinglePaymentEntity target = new SinglePaymentEntity();
            BeanUtils.copyProperties(payment, target);
            target.setUserId(bankAccess.getUserId());
            target.setCreatedDateTime(new Date());
            target.setTanSubmitExternal(response.getAuthorisationCodeResponse().getTanSubmit());

            singlePaymentRepository.save(target);
            return target;
        } catch (MultibankingException e) {
            throw new de.adorsys.multibanking.exception.PaymentException(e.getMessage());
        }
    }

    BulkPaymentEntity createBulkPayment(BankAccessEntity bankAccess, Credentials credentials, BulkPayment payment) {
        OnlineBankingService bankingService = bankingServiceProducer.getBankingService(bankAccess.getBankCode());

        BankApiUser bankApiUser = userService.checkApiRegistration(bankingService,
            userService.findUser(bankAccess.getUserId()));

        if (credentials.getPin() == null) {
            throw new MissingPinException();
        }

        BankEntity bankEntity = bankService.findBank(bankAccess.getBankCode());

        try {
            TransactionRequest<BulkPayment> request =
                TransactionRequestFactory.create(payment, bankApiUser, bankAccess, bankEntity, null);

            AbstractResponse response = bankingService.executePayment(request);

            BulkPaymentEntity target = new BulkPaymentEntity();
            BeanUtils.copyProperties(payment, target);
            target.setUserId(bankAccess.getUserId());
            target.setCreatedDateTime(new Date());
            target.setTanSubmitExternal(response.getAuthorisationCodeResponse().getTanSubmit());

            bulkPaymentRepository.save(target);
            return target;
        } catch (MultibankingException e) {
            throw new de.adorsys.multibanking.exception.PaymentException(e.getMessage());
        }
    }

    void submitRawSepaTransaction(RawSepaTransactionEntity transactionEntity, BankAccessEntity bankAccess,
                                  Credentials credentials, String tan) {
        OnlineBankingService bankingService = bankingServiceProducer.getBankingService(bankAccess.getBankCode());

        if (credentials == null) {
            throw new MissingPinException();
        }

//        try {
//            SubmitAuthorizationCodeRequest request = new SubmitAuthorizationCodeRequest();
//            request.setTransaction(transactionEntity);
//            request.setTanSubmit(transactionEntity.getTanSubmitExternal());
//            request.setTan(tan);
//
//            request.setCredentials(credentials);
//            request.setHbciProduct(finTSProductConfig.getProduct());
//            bankingService.submitAuthorizationCode(request);
//        } catch (MultibankingException e) {
//            throw new de.adorsys.multibanking.exception.PaymentException(e.getMessage());
//        }

        rawSepaTransactionRepository.delete(transactionEntity.getId());
    }

    public void submitSinglePayment(SinglePaymentEntity paymentEntity, BankAccessEntity bankAccess,
                                    Credentials credentials,
                                    String tan) {
        OnlineBankingService bankingService = bankingServiceProducer.getBankingService(bankAccess.getBankCode());

        if (credentials == null) {
            throw new MissingPinException();
        }

//        try {
//            SubmitAuthorizationCodeRequest request = new SubmitAuthorizationCodeRequest();
//            request.setTransaction(paymentEntity);
//            request.setTanSubmit(paymentEntity.getTanSubmitExternal());
//            request.setTan(tan);
//
//            request.setCredentials(credentials);
//            request.setHbciProduct(finTSProductConfig.getProduct());
//            bankingService.submitAuthorizationCode(request);
//        } catch (MultibankingException e) {
//            throw new de.adorsys.multibanking.exception.PaymentException(e.getMessage());
//        }

        singlePaymentRepository.delete(paymentEntity.getId());
    }

    void submitBulkPayment(BulkPaymentEntity paymentEntity, BankAccessEntity bankAccess, Credentials credentials,
                           String tan) {
        OnlineBankingService bankingService = bankingServiceProducer.getBankingService(bankAccess.getBankCode());

        if (credentials == null) {
            throw new MissingPinException();
        }

//        try {
//            SubmitAuthorizationCodeRequest request = new SubmitAuthorizationCodeRequest();
//            request.setTransaction(paymentEntity);
//            request.setTanSubmit(paymentEntity.getTanSubmitExternal());
//            request.setTan(tan);
//
//            request.setCredentials(credentials);
//            request.setHbciProduct(finTSProductConfig.getProduct());
//            bankingService.submitAuthorizationCode(request);
//        } catch (MultibankingException e) {
//            throw new de.adorsys.multibanking.exception.PaymentException(e.getMessage());
//        }

        bulkPaymentRepository.delete(paymentEntity.getId());
    }
}
