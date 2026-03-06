package parkinglot.models;

import java.util.UUID;

import parkinglot.enums.SlotType;

public class Slot {
	private final UUID id;

	private final SlotType slotType;

	public Slot(UUID id, SlotType slotType) {
		this.id = id;
		this.slotType = slotType;
	}

	public UUID getId() {
		return id;
	}

	public SlotType getSlotType() {
		return slotType;
	}
}
