package car_rental_system.service;

import java.util.UUID;

import car_rental_system.database.ReservationRepo;
import car_rental_system.database.VehicleRepo;
import car_rental_system.enums.ReservationStatus;

/**
 * Coordinates reservation lifecycle transitions with vehicle lifecycle
 * transitions.
 *
 * Reservation: RESERVED -> RENTED -> COMPLETED
 * RESERVED -> CANCELLED
 *
 * Vehicle: AVAILABLE -> RESERVED -> RENTED -> AVAILABLE
 */
public class ReservationService {
	private final ReservationRepo reservationRepo;
	private final VehicleRepo vehicleRepo;

	public ReservationService(ReservationRepo reservationRepo, VehicleRepo vehicleRepo) {
		this.reservationRepo = reservationRepo;
		this.vehicleRepo = vehicleRepo;
	}

	public void startRental(UUID reservationId) {
		var reservation = reservationRepo.getReservationOrThrow(reservationId);
		if (!reservation.tryTransitionStatus(ReservationStatus.RESERVED, ReservationStatus.RENTED))
			throw new Error("Reservation is not in RESERVED state");

		var vehicle = vehicleRepo.getVehicle(reservation.getVehicleId());
		boolean ok = vehicle.tryStartRental();
		if (!ok) {
			reservation.tryTransitionStatus(ReservationStatus.RENTED, ReservationStatus.RESERVED);
			throw new Error("Vehicle is not in RESERVED state");
		}
	}

	public void cancel(UUID reservationId) {
		var reservation = reservationRepo.getReservationOrThrow(reservationId);
		if (!reservation.tryTransitionStatus(ReservationStatus.RESERVED, ReservationStatus.CANCELLED))
			throw new Error("Only RESERVED reservations can be cancelled");

		var vehicle = vehicleRepo.getVehicle(reservation.getVehicleId());
		boolean ok = vehicle.tryCancelReservation();
		if (!ok) {
			reservation.tryTransitionStatus(ReservationStatus.CANCELLED, ReservationStatus.RESERVED);
			throw new Error("Vehicle could not be released from RESERVED state");
		}
	}

	public void complete(UUID reservationId) {
		var reservation = reservationRepo.getReservationOrThrow(reservationId);
		if (!reservation.tryTransitionStatus(ReservationStatus.RENTED, ReservationStatus.COMPLETED))
			throw new Error("Only RENTED reservations can be completed");

		var vehicle = vehicleRepo.getVehicle(reservation.getVehicleId());
		boolean ok = vehicle.tryCompleteRental();
		if (!ok) {
			reservation.tryTransitionStatus(ReservationStatus.COMPLETED, ReservationStatus.RENTED);
			throw new Error("Vehicle could not be released from RENTED state");
		}
	}
}
