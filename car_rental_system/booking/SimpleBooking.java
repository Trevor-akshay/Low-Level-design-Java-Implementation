package car_rental_system.booking;

import java.util.UUID;

import car_rental_system.database.ReservationRepo;
import car_rental_system.database.VehicleRepo;
import car_rental_system.enums.VehicleType;
import car_rental_system.models.Reservation;
import car_rental_system.models.Vehicle;

public class SimpleBooking implements BookingStrategy {
	private final VehicleRepo vehicleRepo;
	private final ReservationRepo reservationRepo;
	private static final long DEFAULT_RESERVATION_WINDOW_MILLIS = 60 * 60 * 1000L;

	public SimpleBooking(VehicleRepo vehicleRepo, ReservationRepo reservationRepo) {
		this.vehicleRepo = vehicleRepo;
		this.reservationRepo = reservationRepo;
	}

	@Override
	public Reservation book(VehicleType vehicleType, UUID userId, long rentTime, String from, String to, int distance) {
		return book(vehicleType, userId, rentTime, rentTime + DEFAULT_RESERVATION_WINDOW_MILLIS, from, to, distance);
	}

	@Override
	public Reservation book(VehicleType vehicleType, UUID userId, long startTimeMillis, long endTimeMillis, String from,
			String to, int distance) {
		// Snapshot IDs so we don't need to hold VehicleRepo's lock while we attempt
		// reservations.
		var vehicleIds = vehicleRepo.getVehicleIdsByTypeSnapshot(vehicleType);
		for (var vId : vehicleIds) {
			Vehicle vehicle;
			try {
				vehicle = vehicleRepo.getVehicle(vId);
			} catch (Error e) {
				continue;
			}

			// Attempt AVAILABLE -> RESERVED; only one thread can win per vehicle.
			if (!vehicle.tryReserve())
				continue;

			try {
				// Enforce time-window conflicts at reservation level.
				return reservationRepo.addReservation(vehicle, userId, startTimeMillis, endTimeMillis, from, to,
						distance);
			} catch (Error conflict) {
				// If reservation creation fails (conflict/validation), release RESERVED ->
				// AVAILABLE.
				vehicle.tryCancelReservation();
			}
		}

		throw new Error("No Vehicle available at the moment");
	}
}
