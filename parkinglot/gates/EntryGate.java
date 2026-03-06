package parkinglot.gates;

import java.util.UUID;

import parkinglot.enums.GateType;
import parkinglot.models.Ticket;
import parkinglot.service.ParkingLot;
import parkinglot.vehicles.Vehicle;

public class EntryGate {
	private final UUID id;
	private final GateType gateType;
	private final ParkingLot parkingLot;

	public EntryGate(UUID id, GateType gateType, ParkingLot parkingLot) {
		this.id = id;
		this.gateType = gateType;
		this.parkingLot = parkingLot;
	}

	/**
	 * Entry gate delegates to ParkingLot to allocate a slot and create a Ticket.
	 */
	public Ticket generateTicket(Vehicle vehicle) {
		return parkingLot.generateTicket(vehicle);
	}

	public UUID getId() {
		return id;
	}

	public GateType getGateType() {
		return gateType;
	}

}
