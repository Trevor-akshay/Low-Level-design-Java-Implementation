package elevator.service;

import java.util.HashSet;
import java.util.Set;

import elevator.enums.Direction;
import elevator.enums.ElevatorState;
import elevator.hardware.Door;
import elevator.models.Request;
import elevator.strategy.ElevatorStrategy;

public class Elevator {
	private int currentFloor;
	private Direction currentDirection;
	private ElevatorState elevatorState;
	private final Set<Request> requests;
	private ElevatorStrategy elevatorStrategy;
	private final int totalFloors;
	private int doorTimer; // Tracks how long doors stay open
	private final int DOOR_OPEN_TIME;// Doors stay open for 3 ticks
	private final Door door;

	public Elevator(ElevatorStrategy elevatorStrategy, int totalFloors) {
		this.currentFloor = 0;
		this.currentDirection = Direction.IDLE;
		this.elevatorState = ElevatorState.DOORS_CLOSE;
		this.requests = new HashSet<>();
		this.elevatorStrategy = elevatorStrategy;
		this.totalFloors = totalFloors;
		this.doorTimer = 0;
		this.DOOR_OPEN_TIME = 3;
		this.door = new Door();
	}

	public void addRequest(int floor, Direction direction) {
		if (floor < 0 || floor > totalFloors)
			return;
		if (floor == currentFloor)
			return;

		requests.add(new Request(floor, direction));
	}

	public void tick() {
		if (doorTimer > 0) {
			--doorTimer;
			if (doorTimer == 0) {
				door.closeDoor();
			}
			return;
		}
		var moveCommand = elevatorStrategy.determineNextMove(requests, currentFloor, currentDirection);

		var targetFloor = moveCommand.getTargetFloor();
		var intentedDirection = moveCommand.getIntentedDirection();

		if (currentFloor == targetFloor) {
			door.openDoor();
			doorTimer = DOOR_OPEN_TIME;
			return;
		}

		setCurrentFloor(targetFloor);
		setCurrentDirection(intentedDirection);
	}

	public int getCurrentFloor() {
		return currentFloor;
	}

	public void setCurrentFloor(int currentFloor) {
		this.currentFloor = currentFloor;
	}

	public Direction getCurrentDirection() {
		return currentDirection;
	}

	public void setCurrentDirection(Direction currentDirection) {
		this.currentDirection = currentDirection;
	}

	public ElevatorState getElevatorState() {
		return elevatorState;
	}

	public void setElevatorState(ElevatorState elevatorState) {
		this.elevatorState = elevatorState;
	}

	public Set<Request> getRequests() {
		return requests;
	}

	public ElevatorStrategy getElevatorStrategy() {
		return elevatorStrategy;
	}

	public void setElevatorStrategy(ElevatorStrategy elevatorStrategy) {
		this.elevatorStrategy = elevatorStrategy;
	}
}
