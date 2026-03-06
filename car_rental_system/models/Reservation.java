package car_rental_system.models;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import car_rental_system.enums.ReservationStatus;

public class Reservation {
	private final UUID reservationID;
	private final long startTimeMillis;
	private final long endTimeMillis;
	private final UUID userId;
	private final UUID vehicleId;
	private final String from;
	private final String to;
	private double amount;
	private final int distance;
	private final AtomicReference<ReservationStatus> status;

	/**
	 * New time-window based constructor.
	 *
	 * @param startTimeMillis start of reservation window (inclusive)
	 * @param endTimeMillis   end of reservation window (exclusive)
	 */
	public Reservation(UUID reservationID, long startTimeMillis, long endTimeMillis, UUID userId, UUID vehicleId,
			String from, String to, int distance) {
		this.reservationID = reservationID;
		this.startTimeMillis = startTimeMillis;
		this.endTimeMillis = endTimeMillis;
		this.userId = userId;
		this.vehicleId = vehicleId;
		this.from = from;
		this.to = to;
		this.distance = distance;
		this.status = new AtomicReference<>(ReservationStatus.RESERVED);
	}

	/**
	 * Backward-compatible constructor (older code treated "rentedTime" as the
	 * booking timestamp).
	 *
	 * For compatibility, we model this as a 1-hour window starting at rentedTime.
	 */
	public Reservation(UUID reservationID, long rentedTime, UUID userId, UUID vehicleId, String from, String to,
			int distance) {
		this(reservationID, rentedTime, rentedTime + 60 * 60 * 1000L, userId, vehicleId, from, to, distance);
	}

	public UUID getReservationID() {
		return reservationID;
	}

	/**
	 * Backward-compatible name; returns the reservation start time.
	 */
	public long getRentedTime() {
		return startTimeMillis;
	}

	public long getStartTimeMillis() {
		return startTimeMillis;
	}

	public long getEndTimeMillis() {
		return endTimeMillis;
	}

	public UUID getUserId() {
		return userId;
	}

	public UUID getVehicleId() {
		return vehicleId;
	}

	public double getAmount() {
		return amount;
	}

	public int getDistance() {
		return distance;
	}

	public String getFrom() {
		return from;
	}

	public String getTo() {
		return to;
	}

	public ReservationStatus getStatus() {
		return status.get();
	}

	public boolean tryTransitionStatus(ReservationStatus expected, ReservationStatus next) {
		return status.compareAndSet(expected, next);
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

}
