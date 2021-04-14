package de.adorsys.multibanking.hbci;

import de.adorsys.multibanking.domain.BankAccount;
import de.adorsys.multibanking.domain.BankApi;
import de.adorsys.multibanking.domain.BankApiUser;
import de.adorsys.multibanking.domain.request.TransactionRequest;
import de.adorsys.multibanking.domain.response.*;
import de.adorsys.multibanking.domain.spi.OnlineBankingService;
import de.adorsys.multibanking.domain.spi.StrongCustomerAuthorisable;
import de.adorsys.multibanking.domain.transaction.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HbciBankingLoggingWrapper implements OnlineBankingService {

    HbciBanking hbciBanking = new HbciBanking(null, 0, 0);

    @Override
    public BankApi bankApi() {
        BankApi bankApi = hbciBanking.bankApi();
        log.info(bankApi.name());
        return bankApi;
    }

    @Override
    public boolean externalBankAccountRequired() {
        boolean b = hbciBanking.externalBankAccountRequired();
        log.info("" + b);
        return b;
    }

    @Override
    public boolean userRegistrationRequired() {
        boolean b = hbciBanking.userRegistrationRequired();
        log.info("" + b);
        return b;
    }

    @Override
    public BankApiUser registerUser(String userId) {
        BankApiUser bankApiUser = hbciBanking.registerUser(userId);
        log.info(userId, bankApiUser.toString());
        return bankApiUser;
    }

    @Override
    public void removeUser(BankApiUser bankApiUser) {
        log.info(bankApiUser.toString());
        hbciBanking.removeUser(bankApiUser);
    }

    @Override
    public void removeBankAccount(BankAccount bankAccount, BankApiUser bankApiUser) {
        log.info(bankAccount.toString());
        log.info(bankApiUser.toString());
        hbciBanking.removeBankAccount(bankAccount, bankApiUser);
    }

    @Override
    public boolean bankSupported(String bankCode) {
        log.info(bankCode);
        boolean b = hbciBanking.bankSupported(bankCode);
        log.info("" + b);
        return b;
    }

    @Override
    public boolean bookingsCategorized() {
        boolean b = hbciBanking.bookingsCategorized();
        log.info("" + b);
        return b;
    }

    @Override
    public AccountInformationResponse loadBankAccounts(TransactionRequest<LoadAccounts> loadAccountInformationRequest) {
        log.info("loadBankAccounts request: [{}]", loadAccountInformationRequest);
        AccountInformationResponse accountInformationResponse = hbciBanking.loadBankAccounts(loadAccountInformationRequest);
        log.info("loadBankAccounts response: [{}]", accountInformationResponse);
        return accountInformationResponse;
    }

    @Override
    public TransactionsResponse loadTransactions(TransactionRequest<LoadTransactions> loadTransactionsRequest) {
        log.info("loadTransactions request: [{}]", loadTransactionsRequest);
        TransactionsResponse transactionsResponse = hbciBanking.loadTransactions(loadTransactionsRequest);
        log.info("loadTransactions response: [{}]", transactionsResponse);
        return transactionsResponse;
    }

    @Override
    public StandingOrdersResponse loadStandingOrders(TransactionRequest<LoadStandingOrders> loadStandingOrdersRequest) {
        log.info("loadStandingOrders request: [{}]", loadStandingOrdersRequest);
        StandingOrdersResponse standingOrdersResponse = hbciBanking.loadStandingOrders(loadStandingOrdersRequest);
        log.info("loadStandingOrders response: [{}]", standingOrdersResponse);
        return standingOrdersResponse;
    }

    @Override
    public LoadBalancesResponse loadBalances(TransactionRequest<LoadBalances> request) {
        log.info("loadBalances request: [{}]", request);
        LoadBalancesResponse loadBalancesResponse = hbciBanking.loadBalances(request);
        log.info("loadBalances response: [{}]", loadBalancesResponse);
        return loadBalancesResponse;
    }

    @Override
    public PaymentResponse executePayment(TransactionRequest<? extends AbstractPayment> paymentRequest) {
        log.info("executePayment request: [{}]", paymentRequest);
        PaymentResponse paymentResponse = hbciBanking.executePayment(paymentRequest);
        log.info("executePayment response: [{}]", paymentResponse);
        return paymentResponse;
    }

    @Override
    public StrongCustomerAuthorisable getStrongCustomerAuthorisation() {
        StrongCustomerAuthorisable strongCustomerAuthorisation = hbciBanking.getStrongCustomerAuthorisation();
        log.info("getStrongCustomerAuthorisation response: [{}]", strongCustomerAuthorisation);
        return strongCustomerAuthorisation;
    }
}
