package vending_machine.models;

import java.util.Date;
import java.util.UUID;

public class Item {
	private final UUID itemId;
	private final Date expiryDate;

	public Item(UUID itemId, Date expiryDate) {
		this.itemId = itemId;
		this.expiryDate = expiryDate;
	}

	public UUID getItemId() {
		return itemId;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

}
