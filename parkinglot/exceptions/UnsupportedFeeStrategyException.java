package parkinglot.exceptions;

import parkinglot.enums.PaymentType;

/**
 * Thrown when a fee strategy cannot be determined for a requested payment type.
 */
public class UnsupportedFeeStrategyException extends RuntimeException {
	public UnsupportedFeeStrategyException(PaymentType type) {
		super("Unsupported fee strategy type: " + type);
	}
}
