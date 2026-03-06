package parkinglot.factory;

import java.util.UUID;

import parkinglot.enums.SlotType;
import parkinglot.enums.VehicleType;
import parkinglot.exceptions.UnsupportedVehicleTypeException;
import parkinglot.vehicles.Bike;
import parkinglot.vehicles.Bus;
import parkinglot.vehicles.Car;
import parkinglot.vehicles.Vehicle;

/**
 * Simple factory for creating vehicles.
 */
public class VehicleFactory {
	public static Vehicle createVehicle(VehicleType vehicleType) {
		if (vehicleType == null) {
			throw new IllegalArgumentException("vehicleType cannot be null");
		}
		switch (vehicleType) {
			case BUS:
				return new Bus(UUID.randomUUID(), SlotType.LARGE, VehicleType.BUS);
			case CAR:
				return new Car(UUID.randomUUID(), SlotType.COMPACT, VehicleType.CAR);
			case BIKE:
				return new Bike(UUID.randomUUID(), SlotType.ANY, VehicleType.BIKE);
			default:
				throw new UnsupportedVehicleTypeException(vehicleType);
		}
	}
}
