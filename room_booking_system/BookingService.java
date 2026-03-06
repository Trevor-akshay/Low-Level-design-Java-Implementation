package room_booking_system;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import room_booking_system.models.Booking;
import room_booking_system.strategy.AvailabilityStrategy;

/**
 * BookingService — manages room reservations with per-room concurrency safety.
 *
 * <p>
 * <b>Concurrency model</b>: A per-room {@link ReentrantLock} is obtained before
 * checking
 * availability and creating a booking. This makes the check-then-act sequence
 * atomic for each
 * room, preventing double-bookings under concurrent requests. Requests for
 * different rooms
 * proceed fully in parallel.
 *
 * <p>
 * {@code locks.compute(roomId, ...)} creates a lock lazily and atomically —
 * safe when multiple
 * threads request the first lock for the same room simultaneously.
 *
 * <p>
 * <b>Strategy pattern</b>: The availability check logic is delegated to an
 * {@link AvailabilityStrategy} implementation — swap rules (e.g., 30-minute
 * minimum slots,
 * whole-hour enforcement) without touching this class.
 */
public class BookingService {
	/**
	 * Pluggable rule for determining if a room is free in a requested time slot.
	 */
	private AvailabilityStrategy availabilityStrategy;
	/**
	 * One lock per roomId, created lazily on first booking attempt for that room.
	 */
	private ConcurrentHashMap<Integer, ReentrantLock> locks;

	public BookingService(AvailabilityStrategy availabilityStrategy) {
		this.availabilityStrategy = availabilityStrategy;
		this.locks = new ConcurrentHashMap<>();
	}

	/**
	 * Attempts to book a room for the given time slot.
	 *
	 * <p>
	 * Acquires the per-room lock first, then delegates availability checking to
	 * {@link AvailabilityStrategy#isAvailableToBookRoom}. If available, creates and
	 * returns a {@link Booking}; otherwise throws an exception.
	 *
	 * @param userId      ID of the user making the booking
	 * @param roomId      ID of the room to book
	 * @param startHour   booking start hour (24h)
	 * @param startMinute booking start minute
	 * @param endHour     booking end hour (24h)
	 * @param endMinute   booking end minute
	 * @return a confirmed {@link Booking} object
	 * @throws Exception if the room is unavailable for the requested time slot
	 */
	public Booking book(int userId, int roomId, int startHour, int startMinute, int endHour, int endMinute)
			throws Exception {
		// Acquire (or create) the per-room lock atomically.
		var lock = locks.compute(roomId, (key, value) -> {
			if (value == null) {
				value = new ReentrantLock();
			}
			return value;
		});

		lock.lock();
		try {
			// Check availability and create booking atomically — under the room lock.
			boolean result = availabilityStrategy.isAvailableToBookRoom(roomId, startHour, startMinute, endHour,
					endMinute);

			if (result) {
				return new Booking(userId, roomId);
			} else {
				throw new Exception("Room unavailable for the given time, please book for a valid available time");
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Replaces the availability strategy — takes effect for all subsequent booking
	 * requests.
	 */
	public void setAvailabilityStrategy(AvailabilityStrategy availabilityStrategy) {
		this.availabilityStrategy = availabilityStrategy;
	}

}
