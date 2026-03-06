package car_rental_system.enums;

/**
 * Reservation lifecycle.
 *
 * Typical flow:
 * RESERVED -> RENTED -> COMPLETED
 *
 * Alternate flow:
 * RESERVED -> CANCELLED
 */
public enum ReservationStatus {
	RESERVED,
	RENTED,
	COMPLETED,
	CANCELLED
}
