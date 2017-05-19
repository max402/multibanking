package controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import hbci4java.OnlineBankingMockService;

@RestController
@RequestMapping(path = "api/v1/mock")
public class OnlinebankingMockController {

	@Autowired
	private OnlineBankingMockService mockService;

    @RequestMapping(method = RequestMethod.POST, path="/accounts")
    public Resource<String> createBankAccounts(AddBankAccountsRequest request) {
		mockService.addBankAccounts(request.getAccounts(), request.getBankAccess());
        return new Resource("ok");
    }

    @RequestMapping(method = RequestMethod.POST, path="/bookings")
    public Resource<String> getBookings(AddBookingsRequest request) {
		mockService.addBookings(request.getBookings(), request.getBankAccess(), request.getBankAccount());
        return new Resource("ok");
    }
}
