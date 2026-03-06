package vending_machine.state;

import vending_machine.enums.MachineStates;
import vending_machine.exceptions.ActionNotAllowedException;
import vending_machine.exceptions.HardwareFailureException;
import vending_machine.manager.InventoryManager;
import vending_machine.models.Item;

public class DispenseState extends State {

	@Override
	public Item dispense(InventoryManager inventoryManager, String aisleId)
			throws HardwareFailureException, IllegalArgumentException, ActionNotAllowedException {
		return inventoryManager.dispense(aisleId);
	}

	@Override
	public MachineStates getMachineState() {
		return MachineStates.DISPENSE;
	}
}
