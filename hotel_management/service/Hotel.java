package hotel_management.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import hotel_management.enums.RoomStatus;
import hotel_management.enums.RoomType;
import hotel_management.models.Reservation;
import hotel_management.models.Room;

/**
 * Core hotel service — manages room inventory and concurrent booking
 * operations.
 *
 * <p>
 * <b>Data structures</b>:
 * <ul>
 * <li>{@code rooms} – flat map for O(1) lookup by roomId</li>
 * <li>{@code roomStatusToRoomMapping} – two-level index: status → type →
 * Set&lt;Room&gt;
 * lets us find an available room of a given type without scanning all
 * rooms.</li>
 * <li>{@code locks} – per-{@link RoomType} {@link ReentrantReadWriteLock};
 * concurrent bookings for different types proceed simultaneously.</li>
 * </ul>
 *
 * <p>
 * <b>Concurrency</b>: A per-{@code RoomType} write-lock is acquired before
 * scanning for
 * an available room, ensuring that two simultaneous bookings of the same type
 * cannot receive
 * the same room. Bookings for different types are fully concurrent.
 */
public class Hotel {
	private final Map<String, Room> rooms;
	/**
	 * Two-level index: RoomStatus → RoomType → Set<Room> for fast availability
	 * lookup.
	 */
	private final Map<RoomStatus, Map<RoomType, Set<Room>>> roomStatusToRoomMapping;
	/** Per-RoomType write locks — finer granularity than a single global lock. */
	private final Map<RoomType, ReentrantReadWriteLock> locks;

	public Hotel(Map<String, Room> rooms, Map<RoomStatus, Map<RoomType, Set<Room>>> roomStatusToRoomMapping) {
		this.rooms = rooms;
		this.roomStatusToRoomMapping = new HashMap<>();
		for (var roomStatus : RoomStatus.values()) {
			this.roomStatusToRoomMapping.put(roomStatus, new ConcurrentHashMap<>());

			for (var roomType : RoomType.values()) {
				this.roomStatusToRoomMapping.get(roomStatus)
						.computeIfAbsent(roomType, set -> ConcurrentHashMap.newKeySet())
						.addAll(roomStatusToRoomMapping.get(roomStatus).get(roomType));
			}
		}
		locks = new ConcurrentHashMap<>();
	}

	/**
	 * Books the first available room of the requested type.
	 *
	 * <p>
	 * Acquires the per-type write lock, iterates available rooms, transitions
	 * the first AVAILABLE room to OCCUPIED, and returns a {@link Reservation}.
	 *
	 * @param roomType the type of room requested (SINGLE, DOUBLE, SUITE, etc.)
	 * @return a {@link Reservation} wrapped in an {@link Optional}, or
	 *         {@link Optional#empty()} if no room of the requested type is
	 *         available
	 */
	public Optional<Reservation> bookRoom(RoomType roomType) {
		var rooms = roomStatusToRoomMapping.get(RoomStatus.AVAILABLE);
		if (rooms == null || rooms.isEmpty() || roomType == null)
			return Optional.empty();

		// Acquire write lock for this room type before modifying status.
		var lock = locks.computeIfAbsent(roomType, k -> new ReentrantReadWriteLock());
		lock.writeLock().lock();
		try {
			var iterator = rooms.get(roomType).iterator();
			while (iterator.hasNext()) {
				var room = iterator.next();
				if (room == null)
					return Optional.empty();

				var currentStatus = room.getRoomStatus();
				if (currentStatus != RoomStatus.AVAILABLE)
					continue; // skip rooms whose status changed since iterator started

				// Atomically move room from AVAILABLE → OCCUPIED set.
				updateStatus(room, currentStatus, RoomStatus.OCCUPIED);

				return Optional.of(new Reservation(UUID.randomUUID(), room, room.getPrice(), new Date()));
			}
			return Optional.empty();

		} finally {
			lock.writeLock().unlock();
		}
	}

	/**
	 * Manually updates a room's status (e.g., OCCUPIED → AVAILABLE, or AVAILABLE →
	 * MAINTENANCE).
	 * Useful for housekeeping, maintenance, and check-out workflows.
	 *
	 * @param roomId        the ID of the room to update
	 * @param newRoomStatus the desired new status
	 */
	public void updateRoomStatus(String roomId, RoomStatus newRoomStatus) {
		var room = rooms.get(roomId);

		if (room == null || newRoomStatus == null)
			return;

		var lock = locks.computeIfAbsent(room.getRoomType(), k -> new ReentrantReadWriteLock());

		lock.writeLock().lock();
		try {
			var currentStatus = room.getRoomStatus();
			updateStatus(room, currentStatus, newRoomStatus);
		} finally {
			lock.writeLock().unlock();
		}
	}

	/**
	 * Moves {@code room} from the {@code currentStatus} set to the
	 * {@code newRoomStatus} set
	 * in the two-level index, and updates the room object's internal status field.
	 * Must be called while holding the appropriate write lock.
	 */
	private void updateStatus(Room room, RoomStatus currentStatus, RoomStatus newRoomStatus) {
		var roomType = room.getRoomType();
		var fromSet = roomStatusToRoomMapping.get(currentStatus).get(roomType);
		var toSet = roomStatusToRoomMapping.get(newRoomStatus).get(roomType);
		if (fromSet == null || toSet == null)
			return;

		fromSet.remove(room);
		toSet.add(room);
		room.setRoomStatus(newRoomStatus);
	}
}
