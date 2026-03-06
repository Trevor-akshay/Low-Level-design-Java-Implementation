package vending_machine.factory;

import java.util.Date;
import java.util.UUID;

import vending_machine.models.Item;

public class ItemFactory {
	public static Item createItem(Date expiryDate) {
		var itemId = UUID.randomUUID();
		return new Item(itemId, expiryDate);
	}
}
