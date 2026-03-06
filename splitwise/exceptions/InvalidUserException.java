package splitwise.exceptions;

/**
 * Exception thrown when an invalid user operation is attempted.
 * This includes cases like:
 * - Empty user lists
 * - User not found in records
 * - Payer not in the split list
 */
public class InvalidUserException extends RuntimeException {
	public InvalidUserException(String message) {
		super(message);
	}

	public InvalidUserException(String message, Throwable cause) {
		super(message, cause);
	}
}
