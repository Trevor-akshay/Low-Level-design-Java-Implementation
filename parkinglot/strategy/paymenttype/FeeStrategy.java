package parkinglot.strategy.paymenttype;

import parkinglot.models.Ticket;

/**
 * Strategy for computing parking fee.
 *
 * The exit time is passed in by the caller (PaymentService) so that fee
 * calculation
 * is deterministic and testable.
 */
public interface FeeStrategy {
	double calculateFee(Ticket ticket, long exitTimeMillis);
}
