package vending_machine.payment;

import vending_machine.exceptions.HardwareFailureException;

public class CashPayment implements IPaymentStrategy {
	@Override
	public boolean pay(int price) throws HardwareFailureException {
		System.out.println("Amount " + price + "paid with Cash");
		return true;
	}
}
