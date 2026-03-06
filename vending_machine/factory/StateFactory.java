package vending_machine.factory;

import vending_machine.enums.MachineStates;
import vending_machine.state.DispenseState;
import vending_machine.state.IdleState;
import vending_machine.state.PaymentState;
import vending_machine.state.SelectionState;
import vending_machine.state.State;

public class StateFactory {
	public static State createState(MachineStates machineStates) {
		switch (machineStates) {
			case IDLE:
				return new IdleState();
			case SELECTION:
				return new SelectionState();
			case PAYMENT:
				return new PaymentState();
			case DISPENSE:
				return new DispenseState();
			default:
				throw new IllegalArgumentException("Invalid State");
		}
	}
}
