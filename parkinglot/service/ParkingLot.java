package parkinglot.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import parkinglot.enums.SlotType;
import parkinglot.models.Slot;
import parkinglot.models.Ticket;
import parkinglot.vehicles.Vehicle;

public class ParkingLot {
	/**
	 * Floors keyed by floor number (1..N).
	 */
	private final Map<Integer, ParkingFloor> floors;
	private final int numberOfFloors;
	private final PaymentService paymentService;
	private final TicketService ticketService;

	public ParkingLot(int numberOfFloors, PaymentService paymentService, TicketService ticketService) {
		this.numberOfFloors = numberOfFloors;
		this.floors = new HashMap<>();
		initializeFloors();

		this.ticketService = ticketService;
		this.paymentService = paymentService;
	}

	/**
	 * Creates a simple default layout.
	 * In a production design, this would be driven by configuration.
	 */
	private void initializeFloors() {
		for (int i = 1; i <= numberOfFloors; ++i) {
			List<Slot> slots = new ArrayList<>();
			for (int j = 0; j <= 10; ++j) {
				if (j % 2 == 0) {
					slots.add(new Slot(UUID.randomUUID(), SlotType.COMPACT));
				} else if (j % 3 == 0) {
					slots.add(new Slot(UUID.randomUUID(), SlotType.LARGE));
				} else {
					slots.add(new Slot(UUID.randomUUID(), SlotType.ANY));
				}
			}
			floors.put(i, new ParkingFloor(slots));
		}
	}

	public Ticket generateTicket(Vehicle vehicle) {
		return this.ticketService.generateTicket(vehicle, floors);
	}

	public boolean processPayment(Ticket ticket) {
		return this.paymentService.processPayment(ticket, floors);
	}
}
