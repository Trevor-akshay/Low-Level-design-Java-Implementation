package vending_machine.hardware;

import vending_machine.exceptions.HardwareFailureException;
import vending_machine.models.Aisle;
import vending_machine.models.Item;

public class Dispenser {
	public static Item dispense(Aisle aisle) throws HardwareFailureException {
		return aisle.dispense();
	}
}
