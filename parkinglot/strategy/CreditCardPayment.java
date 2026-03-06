package parkinglot.strategy;

/**
 * Credit-card payment simulation.
 */
public class CreditCardPayment implements PaymentStrategy {
	@Override
	public boolean pay(double amount) {
		System.out.println("Fee of " + amount + " paid through Credit Card");
		return true;
	}
}
