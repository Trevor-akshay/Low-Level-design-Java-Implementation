package hotel_management.models;

import java.util.Date;
import java.util.UUID;

public class Reservation {
	private final UUID reservationId;
	private final Room room;
	private final int amountPaid;
	private final Date timeStamp;

	public Reservation(UUID reservationId, Room room, int amountPaid, Date timeStamp) {
		this.reservationId = reservationId;
		this.room = room;
		this.amountPaid = amountPaid;
		this.timeStamp = timeStamp;
	}

	public UUID getReservationId() {
		return reservationId;
	}

	public Room getRoom() {
		return room;
	}

	public int getAmountPaid() {
		return amountPaid;
	}

	public Date getTimeStamp() {
		return timeStamp;
	}
}
