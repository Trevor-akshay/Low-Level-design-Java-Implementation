package vending_machine.state;

import vending_machine.enums.MachineStates;
import vending_machine.exceptions.ActionNotAllowedException;
import vending_machine.exceptions.HardwareFailureException;
import vending_machine.exceptions.ItemOutOfStockException;
import vending_machine.manager.InventoryManager;
import vending_machine.models.Aisle;
import vending_machine.models.Item;
import vending_machine.payment.IPaymentStrategy;

public abstract class State {
	public void intializeSelection() throws ActionNotAllowedException {
		throw new ActionNotAllowedException("Action is not allowed");
	}

	public Aisle selectAisle(InventoryManager inventoryManager, String aisleId)
			throws ActionNotAllowedException, ItemOutOfStockException, HardwareFailureException {
		throw new ActionNotAllowedException("Action is not allowed");
	}

	public boolean pay(IPaymentStrategy paymentStrategy, int price)
			throws ActionNotAllowedException, HardwareFailureException {
		throw new ActionNotAllowedException("Action is not allowed");
	}

	public Item dispense(InventoryManager inventoryManager, String aisleId)
			throws HardwareFailureException, IllegalArgumentException, ActionNotAllowedException {
		throw new ActionNotAllowedException("Action is not allowed");
	}

	abstract public MachineStates getMachineState();
}
