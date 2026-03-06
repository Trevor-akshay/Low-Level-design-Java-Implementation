package parkinglot.gates;

import java.util.UUID;

import parkinglot.enums.GateType;
import parkinglot.models.Ticket;
import parkinglot.service.ParkingLot;

public class ExitGate {
	private final UUID id;
	private final GateType gateType;
	private final ParkingLot parkingLot;

	public ExitGate(UUID id, GateType gateType, ParkingLot parkingLot) {
		this.id = id;
		this.gateType = gateType;
		this.parkingLot = parkingLot;
	}

	/**
	 * Exit gate delegates to ParkingLot to collect payment and free the slot.
	 */
	public boolean processPayment(Ticket ticket) {
		return parkingLot.processPayment(ticket);
	}

	public UUID getId() {
		return id;
	}

	public GateType getGateType() {
		return gateType;
	}
}
