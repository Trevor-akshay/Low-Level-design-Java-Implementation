package parkinglot.strategy.paymenttype;

import parkinglot.models.Ticket;

/**
 * Charges a fixed price per hour.
 */
public class TimeBasedFee implements FeeStrategy {
	private static final double PRICE_PER_HOUR = 7.5;
	private static final long MILLIS_PER_HOUR = 60L * 60L * 1000L;

	@Override
	public double calculateFee(Ticket ticket, long exitTimeMillis) {
		long entryTimeMillis = ticket.getEntryTimeMillis();
		long durationMillis = Math.max(0L, exitTimeMillis - entryTimeMillis);

		// Charge by started hour (1..N) instead of by milliseconds.
		long hours = (durationMillis + MILLIS_PER_HOUR - 1) / MILLIS_PER_HOUR;
		return hours * PRICE_PER_HOUR;
	}
}
