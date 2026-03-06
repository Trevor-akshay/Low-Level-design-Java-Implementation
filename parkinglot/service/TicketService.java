package parkinglot.service;

import java.util.Map;
import java.util.UUID;

import parkinglot.exceptions.NoAvailableSlotException;
import parkinglot.models.Ticket;
import parkinglot.vehicles.Vehicle;

public class TicketService {
	/**
	 * Allocates a compatible slot on one of the floors and returns a ticket.
	 */
	public Ticket generateTicket(Vehicle vehicle, Map<Integer, ParkingFloor> floors) {
		for (int i = 1; i <= floors.size(); ++i) {
			var slot = floors.get(i).bookNextSlot(vehicle);
			if (slot != null) {
				Ticket ticket = new Ticket(
						UUID.randomUUID(),
						System.currentTimeMillis(),
						vehicle.getVehicleType(),
						slot,
						i);
				return ticket;
			}
		}
		throw new NoAvailableSlotException("No compatible slots are available");
	}
}
