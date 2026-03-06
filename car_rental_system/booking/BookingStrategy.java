package car_rental_system.booking;

import java.util.UUID;

import car_rental_system.enums.VehicleType;
import car_rental_system.models.Reservation;

public interface BookingStrategy {
	public Reservation book(VehicleType vehicleType, UUID userId, long rentTime, String from, String to, int distance);

	/**
	 * Time-window booking API.
	 *
	 * Default implementation delegates to the legacy API for backward
	 * compatibility.
	 * Implementations that support conflict checks should override this method.
	 */
	default Reservation book(VehicleType vehicleType, UUID userId, long startTimeMillis, long endTimeMillis,
			String from,
			String to, int distance) {
		return book(vehicleType, userId, startTimeMillis, from, to, distance);
	}
}
