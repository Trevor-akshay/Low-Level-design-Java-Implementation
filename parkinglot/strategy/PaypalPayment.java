package parkinglot.strategy;

/**
 * PayPal payment simulation.
 */
public class PaypalPayment implements PaymentStrategy {
	@Override
	public boolean pay(double amount) {
		System.out.println("Fee of " + amount + " paid through PayPal");
		return true;
	}
}
