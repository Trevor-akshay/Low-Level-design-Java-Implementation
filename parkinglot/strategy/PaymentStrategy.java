package parkinglot.strategy;

/**
 * Strategy for collecting payment.
 * In real systems, this would integrate with an external payment provider.
 */
public interface PaymentStrategy {
	boolean pay(double fee);
}
