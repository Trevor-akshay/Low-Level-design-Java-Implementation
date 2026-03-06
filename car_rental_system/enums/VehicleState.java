package car_rental_system.enums;

/**
 * Vehicle lifecycle state.
 *
 * Core transitions expected in the rental flow:
 * AVAILABLE -> RESERVED -> RENTED -> AVAILABLE
 *
 * OUT_OF_SERVICE represents a vehicle removed from circulation
 * (maintenance/decommissioned).
 *
 * (If you later need maintenance/deactivation, introduce additional states like
 * OUT_OF_SERVICE.)
 */
public enum VehicleState {
	AVAILABLE,
	RESERVED,
	RENTED,
	OUT_OF_SERVICE
}
