package room_booking_system.strategy;

import room_booking_system.BookingManager;

public class ThirtyMinutesStrategy implements AvailabilityStrategy {
	private final BookingManager bookingManager;

	public ThirtyMinutesStrategy(BookingManager bookingManager) {
		this.bookingManager = bookingManager;
	}

	@Override
	public boolean isAvailableToBookRoom(int roomId, int startHour, int startMinute, int endHour, int endMinute) {
		if (endHour < startHour || (startHour == endHour && endMinute <= startMinute))
			return false;
		try {
			var bookings = bookingManager.getBookings(roomId);

			int minutesRequested = (endHour - startHour) * 60 + (endMinute - startMinute);

			int tempStartMinute = startHour * 2 + startMinute;
			while (tempStartMinute < minutesRequested) {
				if (bookings[tempStartMinute])
					return false;
				tempStartMinute += 30;
			}

			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
