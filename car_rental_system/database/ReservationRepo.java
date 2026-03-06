package car_rental_system.database;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import car_rental_system.enums.ReservationStatus;
import car_rental_system.factories.ReservarionFactory;
import car_rental_system.models.Reservation;
import car_rental_system.models.Vehicle;

public class ReservationRepo {
	private final Map<UUID, Reservation> reservationDb;
	private final Map<UUID, Set<UUID>> reservationIdsByVehicle;
	private final ReadWriteLock lock = new ReentrantReadWriteLock();

	public ReservationRepo() {
		reservationDb = new ConcurrentHashMap<>();
		reservationIdsByVehicle = new ConcurrentHashMap<>();
	}

	public Reservation getReservation(UUID reservationId) {
		lock.readLock().lock();
		try {
			return reservationDb.get(reservationId);
		} finally {
			lock.readLock().unlock();
		}
	}

	public Reservation getReservationOrThrow(UUID reservationId) {
		var reservation = getReservation(reservationId);
		if (reservation == null)
			throw new Error("Reservation not found");
		return reservation;
	}

	/**
	 * Creates a reservation for the given vehicle in the given time window.
	 *
	 * Conflict rule: a vehicle cannot have overlapping reservations (unless the
	 * existing reservation
	 * was CANCELLED).
	 */
	public Reservation addReservation(Vehicle vehicle, UUID userId, long startTimeMillis, long endTimeMillis,
			String from,
			String to, int distance) {
		if (endTimeMillis <= startTimeMillis)
			throw new Error("Invalid reservation window");

		lock.writeLock().lock();
		try {
			var vehicleId = vehicle.getVehicleId();
			ensureNoConflicts(vehicleId, startTimeMillis, endTimeMillis);

			var reservation = ReservarionFactory.createReservation(startTimeMillis, endTimeMillis, userId, vehicleId,
					from,
					to, distance);

			UUID reservationId = reservation.getReservationID();
			reservationDb.put(reservationId, reservation);
			reservationIdsByVehicle.compute(vehicleId, (k, v) -> {
				if (v == null)
					v = ConcurrentHashMap.newKeySet();
				v.add(reservationId);
				return v;
			});

			return reservation;
		} finally {
			lock.writeLock().unlock();
		}
	}

	/**
	 * Backward-compatible overload: treats rentTime as start time and assumes a
	 * 1-hour window.
	 */
	public Reservation addReservation(Vehicle vehicle, UUID userId, long rentTime, String from, String to,
			int distance) {
		return addReservation(vehicle, userId, rentTime, rentTime + 60 * 60 * 1000L, from, to, distance);
	}

	private void ensureNoConflicts(UUID vehicleId, long startTimeMillis, long endTimeMillis) {
		var existingIds = reservationIdsByVehicle.get(vehicleId);
		if (existingIds == null || existingIds.isEmpty())
			return;

		for (var rId : existingIds) {
			var existing = reservationDb.get(rId);
			if (existing == null)
				continue;

			if (existing.getStatus() == ReservationStatus.CANCELLED)
				continue;

			// Overlap check for half-open intervals: [start, end)
			boolean overlaps = startTimeMillis < existing.getEndTimeMillis()
					&& endTimeMillis > existing.getStartTimeMillis();
			if (overlaps)
				throw new Error("Vehicle already reserved for the requested time window");
		}
	}

	/**
	 * Returns true if the given vehicle has at least one non-cancelled reservation
	 * that overlaps the
	 * requested window.
	 */
	public boolean hasConflict(UUID vehicleId, long startTimeMillis, long endTimeMillis) {
		lock.readLock().lock();
		try {
			var existingIds = reservationIdsByVehicle.get(vehicleId);
			if (existingIds == null || existingIds.isEmpty())
				return false;

			for (var rId : existingIds) {
				var existing = reservationDb.get(rId);
				if (existing == null)
					continue;
				if (existing.getStatus() == ReservationStatus.CANCELLED)
					continue;
				boolean overlaps = startTimeMillis < existing.getEndTimeMillis()
						&& endTimeMillis > existing.getStartTimeMillis();
				if (overlaps)
					return true;
			}
			return false;
		} finally {
			lock.readLock().unlock();
		}
	}

}
