package parkinglot.exceptions;

/**
 * Thrown when a ticket is requested but no compatible slot is available.
 */
public class NoAvailableSlotException extends RuntimeException {
	public NoAvailableSlotException(String message) {
		super(message);
	}
}
