package splitwise.exceptions;

/**
 * Exception thrown when a user attempts to settle more than they owe.
 * Example: User owes ₹100 but tries to settle ₹150
 */
public class InsufficientBalanceException extends RuntimeException {
	public InsufficientBalanceException(String message) {
		super(message);
	}

	public InsufficientBalanceException(String message, Throwable cause) {
		super(message, cause);
	}
}
