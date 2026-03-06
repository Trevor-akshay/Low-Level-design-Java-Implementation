package parkinglot.strategy;

/**
 * Cash-based payment simulation.
 */
public class CashPayment implements PaymentStrategy {
	@Override
	public boolean pay(double amount) {
		System.out.println("Fee of " + amount + " paid through Cash");
		return true;
	}
}
