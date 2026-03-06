package vending_machine.state;

import vending_machine.enums.MachineStates;
import vending_machine.exceptions.ActionNotAllowedException;
import vending_machine.exceptions.HardwareFailureException;
import vending_machine.payment.IPaymentStrategy;

public class PaymentState extends State {
	@Override
	public boolean pay(IPaymentStrategy payment, int price) throws ActionNotAllowedException, HardwareFailureException {
		return payment.pay(price);
	}

	@Override
	public MachineStates getMachineState() {
		return MachineStates.PAYMENT;
	}

}
