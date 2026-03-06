package car_rental_system.factories;

import java.util.UUID;
import car_rental_system.models.Reservation;

public class ReservarionFactory {
	public static Reservation createReservation(long startTimeMillis, long endTimeMillis, UUID userId, UUID vehicleId,
			String from, String to, int distance) {
		UUID reservationId = UUID.randomUUID();
		return new Reservation(reservationId, startTimeMillis, endTimeMillis, userId, vehicleId, from, to, distance);
	}

	/**
	 * Backward-compatible overload: treats rentedTime as start time and assumes a
	 * 1-hour window.
	 */
	public static Reservation createReservation(long rentedTime, UUID userId, UUID vehicleId, String from, String to,
			int distance) {
		return createReservation(rentedTime, rentedTime + 60 * 60 * 1000L, userId, vehicleId, from, to, distance);
	}
}
