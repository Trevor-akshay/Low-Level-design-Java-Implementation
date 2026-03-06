package hotel_management.models;

import hotel_management.enums.RoomStatus;
import hotel_management.enums.RoomType;

public abstract class Room {
	private final String roomNumber;
	private int price;
	private final RoomType roomType;
	private RoomStatus roomStatus;
	private int maxCapacity;

	public Room(String roomNumber, int price, RoomType roomType, RoomStatus roomStatus, int maxCapacity) {
		this.roomNumber = roomNumber;
		this.price = price;
		this.roomType = roomType;
		this.roomStatus = roomStatus;
		this.maxCapacity = maxCapacity;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public RoomType getRoomType() {
		return roomType;
	}

	public RoomStatus getRoomStatus() {
		return roomStatus;
	}

	public void setRoomStatus(RoomStatus roomStatus) {
		this.roomStatus = roomStatus;
	}

	public int getMaxCapacity() {
		return maxCapacity;
	}

	public void setMaxCapacity(int maxCapacity) {
		this.maxCapacity = maxCapacity;
	}

	public String getRoomNumber() {
		return roomNumber;
	}

}
