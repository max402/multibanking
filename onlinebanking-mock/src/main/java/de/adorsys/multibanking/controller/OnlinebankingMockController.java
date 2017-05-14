package de.adorsys.multibanking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.adorsys.multibanking.hbci4java.OnlineBankingMockService;

@RestController
@RequestMapping(path = "/api/v1/mock")
public class OnlinebankingMockController {

	@Autowired
	private OnlineBankingMockService mockService;

    @PostMapping("/accounts")
    public Resource<String> createBankAccounts(AddBankAccountsRequest request) {
		mockService.addBankAccounts(request.getAccounts(), request.getBankAccess());
        return new Resource<String>("ok");
    }

    @PostMapping("/bookings")
    public Resource<String> getBookings(AddBookingsRequest request) {
		mockService.addBookings(request.getBookings(), request.getBankAccess(), request.getBankAccount());
        return new Resource<String>("ok");
    }
    
    
    @GetMapping("/all")
    public String all(){
    	return "Salut clsis" ;
    }
}
