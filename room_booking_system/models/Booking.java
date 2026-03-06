package room_booking_system.models;

import java.util.UUID;

public class Booking {
	private final UUID bookingId;

	private final int userId;
	private final int roomId;

	public Booking(int userId, int roomId) {
		this.bookingId = UUID.randomUUID();
		this.userId = userId;
		this.roomId = roomId;
	}

	public UUID getBookingId() {
		return bookingId;
	}

	public int getUser() {
		return userId;
	}

	public int getRoom() {
		return roomId;
	}
}
