package parkinglot.service;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import parkinglot.enums.SlotType;
import parkinglot.models.Slot;
import parkinglot.vehicles.Vehicle;

public class ParkingFloor {
	/**
	 * Available slots grouped by slot type.
	 * Each queue represents currently-free slots.
	 */
	private final Map<SlotType, BlockingQueue<Slot>> availableByType;

	public ParkingFloor(List<Slot> slots) {
		availableByType = new HashMap<>();
		intializeFloor(slots);
	}

	private void intializeFloor(List<Slot> slots) {
		for (var slotType : SlotType.values()) {
			availableByType.put(slotType, new LinkedBlockingQueue<>());
		}
		for (var slot : slots) {
			availableByType.get(slot.getSlotType()).offer(slot);
		}
	}

	/**
	 * Books the next compatible slot for the vehicle.
	 *
	 * Compatibility rules (simple version):
	 * - BIKE (prefers ANY): try ANY, then COMPACT, then LARGE
	 * - CAR (prefers COMPACT): try COMPACT, then LARGE, then ANY
	 * - BUS (prefers LARGE): try LARGE, then ANY
	 */
	public Slot bookNextSlot(Vehicle vehicle) {
		if (vehicle == null) {
			throw new IllegalArgumentException("vehicle cannot be null");
		}
		SlotType preferred = vehicle.getSlotType();
		for (SlotType candidate : compatibleOrder(preferred)) {
			Slot slot = availableByType.get(candidate).poll();
			if (slot != null) {
				return slot;
			}
		}
		return null;
	}

	public void freeSlot(Slot slot) {
		if (slot == null) {
			throw new IllegalArgumentException("slot cannot be null");
		}
		availableByType.get(slot.getSlotType()).offer(slot);
	}

	private List<SlotType> compatibleOrder(SlotType preferred) {
		if (preferred == SlotType.ANY) {
			return List.of(SlotType.ANY, SlotType.COMPACT, SlotType.LARGE);
		}
		if (preferred == SlotType.COMPACT) {
			return List.of(SlotType.COMPACT, SlotType.LARGE, SlotType.ANY);
		}
		// LARGE
		return List.of(SlotType.LARGE, SlotType.ANY);
	}
}
