package car_rental_system.factories;

import java.util.UUID;

import car_rental_system.enums.VehicleType;
import car_rental_system.models.Vehicle;

public class VehicleFactory {
	static public Vehicle createVehicle(VehicleType vehicleType) {
		UUID vehicleId = UUID.randomUUID();
		return new Vehicle(vehicleId, vehicleType);
	}
}
