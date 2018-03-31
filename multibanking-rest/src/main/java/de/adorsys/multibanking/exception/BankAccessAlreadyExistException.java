package de.adorsys.multibanking.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
        value = HttpStatus.BAD_REQUEST,
        reason = "BANK_ACCESS_ALREADY_EXIST"
)
public class BankAccessAlreadyExistException extends ParametrizedMessageException {
	private static final long serialVersionUID = 5078077955213908774L;
	private final String accessId;
    public BankAccessAlreadyExistException(String accessId) {
        super("Bank access already exist");
        this.accessId=accessId;
    }
	public String getAccessId() {
		return accessId;
	}
}
