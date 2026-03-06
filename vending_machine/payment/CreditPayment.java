package vending_machine.payment;

import vending_machine.exceptions.HardwareFailureException;

public class CreditPayment implements IPaymentStrategy {

	@Override
	public boolean pay(int price) throws HardwareFailureException {
		System.out.println("Amount " + price + "paid with Credit");
		return true;
	}

}
