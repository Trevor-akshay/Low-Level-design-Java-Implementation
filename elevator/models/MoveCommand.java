package elevator.models;

import elevator.enums.Direction;

public class MoveCommand {
	private final int targetFloor;
	private final Direction intentedDirection;

	public MoveCommand(int targetFloor, Direction intentedDirection) {
		this.targetFloor = targetFloor;
		this.intentedDirection = intentedDirection;
	}

	public int getTargetFloor() {
		return targetFloor;
	}

	public Direction getIntentedDirection() {
		return intentedDirection;
	}
}
