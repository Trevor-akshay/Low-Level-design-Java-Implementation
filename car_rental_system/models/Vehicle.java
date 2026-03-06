package car_rental_system.models;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import car_rental_system.enums.VehicleType;
import car_rental_system.enums.VehicleState;

public class Vehicle {
	private final UUID vehicleId;
	private VehicleType vehicleType;
	private final AtomicReference<VehicleState> state;

	public Vehicle(UUID vehicleId, VehicleType vehicleType) {
		this.vehicleId = vehicleId;
		this.vehicleType = vehicleType;
		this.state = new AtomicReference<>(VehicleState.AVAILABLE);
	}

	public VehicleState getState() {
		return state.get();
	}

	/**
	 * Marks a vehicle as out of circulation (maintenance/decommissioned).
	 *
	 * This is intentionally a strong operation: it forcefully sets the state to
	 * OUT_OF_SERVICE.
	 * In a more complete system you might restrict this transition (e.g., disallow
	 * while RENTED).
	 */
	public void markOutOfService() {
		state.set(VehicleState.OUT_OF_SERVICE);
	}

	/**
	 * Attempt to reserve this vehicle.
	 *
	 * Thread-safe and lock-free: only one caller can win the AVAILABLE -> RESERVED
	 * transition.
	 */
	public boolean tryReserve() {
		return state.compareAndSet(VehicleState.AVAILABLE, VehicleState.RESERVED);
	}

	/**
	 * Marks the vehicle as rented (pickup/start).
	 *
	 * This enforces RESERVED -> RENTED.
	 */
	public boolean tryStartRental() {
		return state.compareAndSet(VehicleState.RESERVED, VehicleState.RENTED);
	}

	/**
	 * Releases the vehicle back to AVAILABLE after a successful rental completion.
	 *
	 * This enforces RENTED -> AVAILABLE.
	 */
	public boolean tryCompleteRental() {
		return state.compareAndSet(VehicleState.RENTED, VehicleState.AVAILABLE);
	}

	/**
	 * Releases the vehicle back to AVAILABLE after a cancellation.
	 *
	 * This enforces RESERVED -> AVAILABLE.
	 */
	public boolean tryCancelReservation() {
		return state.compareAndSet(VehicleState.RESERVED, VehicleState.AVAILABLE);
	}

	public VehicleType getVehicleType() {
		return vehicleType;
	}

	public void setVehicleType(VehicleType vehicleType) {
		this.vehicleType = vehicleType;
	}

	public UUID getVehicleId() {
		return vehicleId;
	}
}
