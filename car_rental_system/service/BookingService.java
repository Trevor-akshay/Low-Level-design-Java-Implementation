package car_rental_system.service;

import java.util.UUID;

import car_rental_system.booking.BookingStrategy;
import car_rental_system.enums.VehicleType;
import car_rental_system.models.Reservation;

public class BookingService {
	private BookingStrategy bookingStrategy;
	private static final long DEFAULT_RESERVATION_WINDOW_MILLIS = 60 * 60 * 1000L;

	public BookingService(BookingStrategy bookingStrategy) {
		this.bookingStrategy = bookingStrategy;
	}

	public Reservation book(VehicleType vehicleType, UUID userId, long rentTime, String from, String to, int distance) {
		// Backward-compatible call: treat rentTime as start time and assume a 1-hour
		// window.
		return bookingStrategy.book(vehicleType, userId, rentTime, rentTime + DEFAULT_RESERVATION_WINDOW_MILLIS, from,
				to,
				distance);
	}

	public Reservation book(VehicleType vehicleType, UUID userId, long startTimeMillis, long endTimeMillis, String from,
			String to, int distance) {
		return bookingStrategy.book(vehicleType, userId, startTimeMillis, endTimeMillis, from, to, distance);
	}

	public void setBookingStrategy(BookingStrategy bookingStrategy) {
		this.bookingStrategy = bookingStrategy;
	}

	public BookingStrategy getBookingStrategy() {
		return bookingStrategy;
	}
}
