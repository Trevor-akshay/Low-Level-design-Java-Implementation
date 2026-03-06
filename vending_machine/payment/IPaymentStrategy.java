package vending_machine.payment;

import vending_machine.exceptions.HardwareFailureException;

public interface IPaymentStrategy {
	public boolean pay(int price) throws HardwareFailureException;
}
