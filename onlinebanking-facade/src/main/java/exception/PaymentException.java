package exception;

import lombok.Data;

/**
 * Created by alexg on 21.11.17.
 */
@Data
public class PaymentException extends RuntimeException {
	private static final long serialVersionUID = -3082040997097241607L;

	public PaymentException(String msg) {
        super(msg);
    }
}
