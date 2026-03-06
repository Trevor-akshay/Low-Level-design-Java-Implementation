package vending_machine.state;

import vending_machine.enums.MachineStates;
import vending_machine.exceptions.ActionNotAllowedException;

public class IdleState extends State {

	public void intializeSelection() throws ActionNotAllowedException {
	}

	@Override
	public MachineStates getMachineState() {
		return MachineStates.IDLE;
	}
}
