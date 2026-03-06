package elevator.strategy;

import java.util.Set;

import elevator.enums.Direction;
import elevator.models.MoveCommand;
import elevator.models.Request;

public interface ElevatorStrategy {
	MoveCommand determineNextMove(Set<Request> requests, int currentFloor, Direction currentDirection);
}
