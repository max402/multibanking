package de.adorsys.multibanking.web;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.adorsys.multibanking.domain.StandingOrderEntity;
import de.adorsys.multibanking.exception.SyncInProgressException;
import de.adorsys.multibanking.service.BankAccountService;
import de.adorsys.multibanking.service.StandingOrderService;
import domain.BankAccount;

@UserResource
@RestController
@SuppressWarnings({"unused"})
@RequestMapping(path = "api/v1/bankaccesses/{accessId}/accounts/{accountId}/standingorders")
public class StandingOrderController {

    @Autowired
    private StandingOrderService standingOrderService;
    @Autowired
    private BankAccountService bankAccountService;

    @RequestMapping(method = RequestMethod.GET)
    public Resources<StandingOrderEntity> getStandingOrders(@PathVariable String accessId, @PathVariable String accountId) {
        if (bankAccountService.getSyncStatus(accessId, accountId) == BankAccount.SyncStatus.SYNC) {
            throw new SyncInProgressException(accountId);
        }

        return new Resources(standingOrderService.load(accessId, accountId),
                linkTo(methodOn(StandingOrderController.class).getStandingOrders(accessId, accountId)).withSelfRel());
    }
}
