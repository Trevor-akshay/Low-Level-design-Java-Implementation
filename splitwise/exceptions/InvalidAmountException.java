package splitwise.exceptions;

/**
 * Exception thrown when an invalid amount is provided for an expense.
 * Valid amounts should be positive and non-zero.
 */
public class InvalidAmountException extends RuntimeException {
	public InvalidAmountException(String message) {
		super(message);
	}

	public InvalidAmountException(String message, Throwable cause) {
		super(message, cause);
	}
}
