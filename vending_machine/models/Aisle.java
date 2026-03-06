package vending_machine.models;

import java.util.Queue;

public class Aisle {
	private final String aisleId;
	private final Queue<Item> items;
	private String name;
	private int price;

	public Aisle(String aisleId, Queue<Item> items, String name, int price) {
		this.aisleId = aisleId;
		this.items = items;
		this.name = name;
		this.price = price;
	}

	public void offerItem(Item item) {
		items.offer(item);
	}

	public Item dispense() {
		return items.poll();
	}

	public String getAisleId() {
		return aisleId;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isAisleEmpty() {
		return items.isEmpty();
	}

	public int getItemCount() {
		return items.size();
	}
}
