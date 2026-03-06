package vending_machine.repo;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import vending_machine.factory.ItemFactory;
import vending_machine.models.Item;

public class ItemRepo {
	Map<UUID, Item> items;

	public ItemRepo() {
		this.items = new HashMap<>();
	}

	public void create(Date expiryDate) {
		var item = ItemFactory.createItem(expiryDate);
		items.put(item.getItemId(), item);
	}

	public Item read(UUID itemId) {
		return items.get(itemId);
	}

	public void remove(UUID itemId) {
		items.remove(itemId);
	}
}
