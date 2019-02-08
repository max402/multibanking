package org.adorsys.multibanking.onlinebanking.facade.mock;

import domain.BankAccess;
import domain.BankAccount;
import domain.Booking;
import domain.request.LoadBookingsRequest;
import domain.response.LoadBookingsResponse;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by peter on 08.02.19 17:03.
 */
public class LoadBookingsTest {
    private final static Logger LOGGER = LoggerFactory.getLogger(LoadBookingsTest.class);
    @Test
    public void getAllBookings() {
        List<Booking> bookings = getBookings("m.becker","DE81199999993528307800","12345");
        Assert.assertEquals(62, bookings.size());
        bookings = getBookings("m.becker","DE12199999994076397393","12345");
        Assert.assertEquals(7,bookings.size());
        bookings = getBookings("p.spiessbach", "DE99199999991010101010", "11111");
        Assert.assertEquals(7,bookings.size());
        bookings = getBookings("p.spiessbach", "DE99199999991010101011", "11111");
        Assert.assertEquals(6,bookings.size());
    }


    private List<Booking> getBookings(String user, String iban, String pin) {
        SimpleMockBanking simpleMockBanking = new SimpleMockBanking(null, null);
        BankAccess bankAccess = new BankAccess();
        bankAccess.setBankLogin(user);
        BankAccount bankAccount = new BankAccount();
        bankAccount.setIban(iban);
        LoadBookingsResponse bookingsResponse = simpleMockBanking.loadBookings(
                null,
                LoadBookingsRequest.builder()
                        .bankApiUser(null)
                        .bankAccess(bankAccess)
                        .bankCode(null)
                        .bankAccount(bankAccount)
                        .pin(pin)
                        .withTanTransportTypes(true)
                        .withBalance(true)
                        .withStandingOrders(true)
                        .build()
        );
        return bookingsResponse.getBookings();
    }
}
