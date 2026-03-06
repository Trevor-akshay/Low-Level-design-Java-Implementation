package vending_machine.manager;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import vending_machine.exceptions.HardwareFailureException;
import vending_machine.exceptions.ItemOutOfStockException;
import vending_machine.hardware.Dispenser;
import vending_machine.hardware.Motor;
import vending_machine.models.Aisle;
import vending_machine.models.Item;

public class InventoryManager {
	private final Map<String, Aisle> inventory;
	private final ReentrantReadWriteLock lock;
	private int capacity;

	InventoryManager(Map<String, Aisle> inventory, int capacity) {
		this.inventory = inventory;
		this.lock = new ReentrantReadWriteLock();
		this.capacity = capacity;
	}

	public Aisle selectAisle(String aisleId)
			throws ItemOutOfStockException, IllegalArgumentException, HardwareFailureException {
		lock.writeLock().lock();
		try {
			var aisle = inventory.get(aisleId);
			if (aisle == null)
				throw new IllegalArgumentException("Please select a valid Aisle Id");
			if (aisle.isAisleEmpty())
				throw new ItemOutOfStockException("Items at the Aisle" + aisleId + " are out of stock");

			Motor.selectAisle();

			return aisle;
		} finally {
			lock.writeLock().unlock();
		}
	}

	public int getPrice(String aisleId) {
		lock.readLock().lock();
		try {
			return inventory.get(aisleId).getPrice();
		} finally {
			lock.readLock().unlock();

		}
	}

	public Item dispense(String aisleId) throws HardwareFailureException {
		lock.writeLock().lock();
		try {
			var aisle = inventory.get(aisleId);
			return Dispenser.dispense(aisle);
		} finally {
			lock.writeLock().unlock();
		}
	}

	public void restock(String aisleId, List<Item> items) {
		lock.writeLock().lock();
		try {
			var aisle = inventory.get(aisleId);
			if (aisle == null)
				throw new IllegalArgumentException("Please select a valid Aisle Id");
			// Check feasibility BEFORE modifying state
			if (aisle.getItemCount() + items.size() > capacity) {
				throw new IllegalArgumentException("Not enough space for all items");
			}
			// If we get here, we know they all fit
			for (var item : items) {
				aisle.offerItem(item);
			}
		} finally {
			lock.writeLock().unlock();
		}
	}
}
