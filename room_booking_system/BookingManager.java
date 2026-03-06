package room_booking_system;

import java.util.Map;

public class BookingManager {
	private final Map<Integer, boolean[]> bookings;

	public BookingManager(Map<Integer, boolean[]> bookings) {
		this.bookings = bookings;
	}

	public boolean[] getBookings(int roomId) throws Exception {
		if (bookings.get(roomId) == null)
			throw new Exception("Room not available");
		return bookings.get(roomId);
	}

}
