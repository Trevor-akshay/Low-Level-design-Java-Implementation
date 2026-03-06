package parkinglot.models;

import java.util.UUID;

import parkinglot.enums.VehicleType;

public class Ticket {
	private final UUID id;
	private final VehicleType vehicleType;
	private final int floorId;
	private final long entryTimeMillis;
	private final Slot slot;

	private boolean paid;
	private Long exitTimeMillis;

	/**
	 * Ticket is the contract between entry and exit.
	 * It records where the vehicle was parked and when it entered.
	 */
	public Ticket(UUID id, long entryTimeMillis, VehicleType vehicleType, Slot slot, int floorId) {
		this.id = id;
		this.entryTimeMillis = entryTimeMillis;
		this.vehicleType = vehicleType;
		this.slot = slot;
		this.floorId = floorId;
		this.paid = false;
		this.exitTimeMillis = null;
	}

	public UUID getId() {
		return id;
	}

	public long getEntryTimeMillis() {
		return entryTimeMillis;
	}

	public VehicleType getVehicleType() {
		return vehicleType;
	}

	public Slot getSlot() {
		return slot;
	}

	public int getFloorId() {
		return floorId;
	}

	public boolean isPaid() {
		return paid;
	}

	public Long getExitTimeMillis() {
		return exitTimeMillis;
	}

	/**
	 * Marks the ticket as paid and stores exit time for audit/fee calculations.
	 */
	public void markPaid(long exitTimeMillis) {
		this.paid = true;
		this.exitTimeMillis = exitTimeMillis;
	}

}
