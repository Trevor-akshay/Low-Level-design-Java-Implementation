package parkinglot.exceptions;

import parkinglot.enums.VehicleType;

/**
 * Thrown when a factory or system component cannot handle a given vehicle type.
 */
public class UnsupportedVehicleTypeException extends RuntimeException {
	public UnsupportedVehicleTypeException(VehicleType type) {
		super("Unsupported vehicle type: " + type);
	}
}
