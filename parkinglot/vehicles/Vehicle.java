package parkinglot.vehicles;

import java.util.UUID;

import parkinglot.enums.SlotType;
import parkinglot.enums.VehicleType;

public abstract class Vehicle {
	private final SlotType preferredSlotType;
	private final UUID id;
	private final VehicleType vehicleType;

	/**
	 * @param preferredSlotType the slot type this vehicle prefers/requires.
	 */
	public Vehicle(UUID id, SlotType preferredSlotType, VehicleType vehicleType) {
		this.id = id;
		this.preferredSlotType = preferredSlotType;
		this.vehicleType = vehicleType;
	}

	public SlotType getSlotType() {
		return this.preferredSlotType;
	}

	public VehicleType getVehicleType() {
		return vehicleType;
	}

	public UUID getId() {
		return id;
	}
}
