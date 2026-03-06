package parkinglot.vehicles;

import java.util.UUID;

import parkinglot.enums.SlotType;
import parkinglot.enums.VehicleType;

public class Bus extends Vehicle {
	public Bus(UUID id, SlotType slot, VehicleType vehicleType) {
		super(id, slot, vehicleType);
	}
}
