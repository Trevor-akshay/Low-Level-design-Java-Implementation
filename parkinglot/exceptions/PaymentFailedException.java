package parkinglot.exceptions;

/**
 * Thrown when payment is unsuccessful and the vehicle cannot be exited.
 */
public class PaymentFailedException extends RuntimeException {
	public PaymentFailedException(String message) {
		super(message);
	}
}
