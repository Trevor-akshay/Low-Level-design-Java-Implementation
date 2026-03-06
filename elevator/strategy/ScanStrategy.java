package elevator.strategy;

import java.util.Set;

import elevator.enums.Direction;
import elevator.models.MoveCommand;
import elevator.models.Request;

public class ScanStrategy implements ElevatorStrategy {
	@Override
	public MoveCommand determineNextMove(Set<Request> requests, int currentFloor, Direction currentDirection) {
		// case 1 : No request:
		if (requests.isEmpty()) {
			return new MoveCommand(currentFloor, Direction.IDLE);
		}

		// case 2: Elevator is IDLE:
		Request destinationRequest = null;
		if (currentDirection == Direction.IDLE) {
			destinationRequest = getNearestRequest(requests, currentFloor);
			currentDirection = destinationRequest.getDirection();
		}

		// case 3: current request is available at this floor.
		var currentRequest = new Request(currentFloor, currentDirection);
		if (requests.contains(currentRequest)) {
			requests.remove(currentRequest);

			// We dont change the floor because door opens and closes.
			if (requests.isEmpty())
				return new MoveCommand(currentFloor, Direction.IDLE);

			if (!hasAheadRequest(requests, currentFloor, currentDirection)) {
				currentDirection = switchDirection(currentDirection);
			}
			return new MoveCommand(currentFloor, currentDirection);
		}

		// case 4: No request available for current direction:
		if (!hasAheadRequest(requests, currentFloor, currentDirection)) {
			currentDirection = switchDirection(currentDirection);

			return new MoveCommand(currentFloor, currentDirection);
		}

		// case 5: Request available.
		if (currentDirection == Direction.UP)
			++currentFloor;
		else
			--currentFloor;
		return new MoveCommand(currentFloor, currentDirection);
	}

	private Direction switchDirection(Direction direction) {
		return direction == Direction.UP ? Direction.DOWN : Direction.UP;
	}

	private Request getNearestRequest(Set<Request> requests, int currentFloor) {
		Request nearest = null;
		int distance = Integer.MAX_VALUE;

		for (var request : requests) {
			var floor = request.getFloor();

			var distanceDiff = Math.abs(currentFloor - floor);

			if (distanceDiff < distance || (nearest == null || nearest.getFloor() > floor)) {
				distance = distanceDiff;
				nearest = request;
			}
		}

		return nearest;
	}

	private boolean hasAheadRequest(Set<Request> requests, int currentFloor, Direction currentDirection) {
		for (var request : requests) {
			if (currentDirection == Direction.UP && request.getFloor() > currentFloor)
				return true;

			if (currentDirection == Direction.DOWN && request.getFloor() < currentFloor)
				return true;
		}

		return false;
	}
}
