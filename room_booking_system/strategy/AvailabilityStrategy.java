package room_booking_system.strategy;

public interface AvailabilityStrategy {
	boolean isAvailableToBookRoom(int roomId, int startHour, int startMinute, int endHour, int endMinute);
}
