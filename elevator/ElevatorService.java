package elevator;

import java.util.ArrayList;
import java.util.List;

import elevator.enums.Direction;
import elevator.service.Elevator;
import elevator.strategy.ElevatorStrategy;

/**
 * ElevatorService — dispatch controller for a bank of elevator cars.
 *
 * <p>
 * Request dispatching follows a three-level priority strategy:
 * <ol>
 * <li><b>Idle nearest</b> – assign to the closest idle elevator.</li>
 * <li><b>Same-direction</b> – assign to an elevator already travelling toward
 * the floor
 * in the same direction and not yet past it.</li>
 * <li><b>Nearest overall</b> – fallback: assign to the nearest elevator
 * regardless of
 * direction or state.</li>
 * </ol>
 *
 * <p>
 * Movement is driven by {@link #tick()} — each call advances every elevator one
 * floor
 * toward its next scheduled stop. This tick-based model decouples the
 * simulation from
 * real-time threading and makes the system deterministically testable.
 */
public class ElevatorService {
	private final List<Elevator> elevators;
	private final int totalFloors;

	/**
	 * Creates a bank of {@code elevatorCount} elevators each aware of
	 * {@code totalFloors}.
	 *
	 * @param elevatorStrategy the scheduling algorithm each elevator uses
	 *                         internally
	 * @param elevatorCount    number of elevator cars to create
	 * @param totalFloors      total number of floors in the building
	 */
	public ElevatorService(ElevatorStrategy elevatorStrategy, int elevatorCount, int totalFloors) {
		this.totalFloors = totalFloors;
		this.elevators = new ArrayList<>();
		for (int i = 0; i < elevatorCount; ++i) {
			elevators.add(new Elevator(elevatorStrategy, totalFloors));
		}
	}

	/**
	 * Advances every elevator one floor toward its next destination.
	 * Call this in a loop (or on a timer) to simulate real-time movement.
	 */
	public void tick() {
		for (var elevator : elevators) {
			elevator.tick();
		}
	}

	/**
	 * Dispatches a floor request to the most appropriate elevator.
	 *
	 * <p>
	 * Priority order: idle nearest → same-direction → nearest overall.
	 *
	 * @param floor     the floor number where a user is waiting
	 * @param direction the direction the user wants to travel (UP or DOWN)
	 */
	public void addRequest(int floor, Direction direction) {
		// case 1: add the request to the idle elevator.
		Elevator elevator = getIdleElevator(floor);
		// case 2: add the request to the elevator which is going on the same direction.
		if (elevator == null) {
			elevator = getSameDirectionElevator(floor, direction);
		}

		// case 3: add the request to the nearest elevator.
		if (elevator == null) {
			elevator = getNearestElevator(floor);
		}

		elevator.addRequest(floor, direction);
	}

	/**
	 * Returns the idle elevator closest to {@code floor}, or {@code null} if all
	 * are busy.
	 */
	private Elevator getIdleElevator(int floor) {
		Elevator nearestElevator = null;
		int minDistance = Integer.MAX_VALUE;
		for (var elevator : elevators) {
			if (elevator.getCurrentDirection() == Direction.IDLE) {
				var elevatorFloor = elevator.getCurrentFloor();

				int distanceDiff = Math.abs(floor - elevatorFloor);
				if (distanceDiff < minDistance || nearestElevator == null) {
					minDistance = distanceDiff;
					nearestElevator = elevator;
				}
			}
		}
		return nearestElevator;
	}

	/**
	 * Returns the nearest elevator already travelling in {@code direction} and
	 * whose
	 * current floor is on the correct side of {@code floor} (i.e., it has not
	 * passed it yet).
	 * Returns {@code null} if no such elevator exists.
	 */
	private Elevator getSameDirectionElevator(int floor, Direction direction) {
		Elevator nearestElevator = null;
		int minDistance = Integer.MAX_VALUE;
		for (var elevator : elevators) {
			if (elevator.getCurrentDirection() == direction) {
				var elevatorFloor = elevator.getCurrentFloor();

				int distanceDiff = Math.abs(floor - elevatorFloor);
				// For UP: elevator must be below the requested floor.
				if (direction == Direction.UP && floor > elevator.getCurrentFloor()
						&& (nearestElevator == null || distanceDiff < minDistance)) {
					minDistance = distanceDiff;
					nearestElevator = elevator;
				}
				// For DOWN: elevator must be above the requested floor.
				if (direction == Direction.DOWN && floor < elevator.getCurrentFloor()
						&& (nearestElevator == null || distanceDiff < minDistance)) {
					minDistance = distanceDiff;
					nearestElevator = elevator;
				}
			}
		}

		return nearestElevator;
	}

	/**
	 * Returns the elevator whose current floor is closest to {@code floor}.
	 * Used as a last-resort fallback; always returns a non-null value if elevators
	 * list is non-empty.
	 */
	private Elevator getNearestElevator(int floor) {
		Elevator nearestElevator = null;
		int minDistance = Integer.MAX_VALUE;
		for (var elevator : elevators) {
			var elevatorFloor = elevator.getCurrentFloor();

			int distanceDiff = Math.abs(floor - elevatorFloor);
			if (distanceDiff < minDistance || nearestElevator == null) {
				minDistance = distanceDiff;
				nearestElevator = elevator;
			}
		}

		return nearestElevator;
	}

	public List<Elevator> getElevators() {
		return elevators;
	}

	public int getTotalFloors() {
		return totalFloors;
	}
}
