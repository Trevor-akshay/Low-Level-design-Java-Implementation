package parkinglot.vehicles;

import java.util.UUID;

import parkinglot.enums.SlotType;
import parkinglot.enums.VehicleType;

public class Car extends Vehicle {

	public Car(UUID id, SlotType slot, VehicleType vehicleType) {
		super(id, slot, vehicleType);
	}

}
