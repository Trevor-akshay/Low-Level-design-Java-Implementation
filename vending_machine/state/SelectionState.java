package vending_machine.state;

import vending_machine.enums.MachineStates;
import vending_machine.exceptions.ActionNotAllowedException;
import vending_machine.exceptions.HardwareFailureException;
import vending_machine.exceptions.ItemOutOfStockException;
import vending_machine.manager.InventoryManager;
import vending_machine.models.Aisle;

public class SelectionState extends State {

	@Override
	public Aisle selectAisle(InventoryManager inventoryManager, String aisleId)
			throws ActionNotAllowedException, ItemOutOfStockException, HardwareFailureException {
		return inventoryManager.selectAisle(aisleId);
	}

	@Override
	public MachineStates getMachineState() {
		return MachineStates.SELECTION;
	}

}
