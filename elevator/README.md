# Elevator System — Low Level Design

A multi-elevator dispatch system with pluggable scheduling strategies.

---

## Class Entities

| Class / Interface                | Role                                                                                  |
| -------------------------------- | ------------------------------------------------------------------------------------- |
| `ElevatorService`                | Dispatch controller — manages all elevator instances and assigns floor requests       |
| `Elevator`                       | Single elevator car — holds a request queue and moves floor-by-floor on each `tick()` |
| `ElevatorStrategy` _(interface)_ | Pluggable scheduling algorithm — e.g., SCAN (LOOK), First-Come-First-Served           |
| `Direction` _(enum)_             | `UP`, `DOWN`, `IDLE`                                                                  |
| `Button`                         | Represents a hardware floor-request button (inside or outside the car)                |

---

## Functional Requirements

1. External floor request (`addRequest(floor, direction)`) dispatches to the best elevator:
   - **Priority 1**: An idle elevator nearest to the requested floor.
   - **Priority 2**: An elevator already travelling in the same direction and not yet past the floor.
   - **Priority 3**: Any nearest elevator as a fallback.
2. Each `tick()` advances every elevator one step toward its next destination.
3. The elevator stops at requested floors, picks up passengers, and drops them off.
4. Elevator transitions to `IDLE` when its request queue is empty.

---

## Non-Functional Requirements

- **Extensibility**: Swap the scheduling algorithm by passing a different `ElevatorStrategy` — no changes to `ElevatorService` or `Elevator` required.
- **Scalability**: `ElevatorService` can manage any number of elevator cars.
- **Testability**: `tick()`-based simulation allows deterministic unit testing without threading.

---

## Concurrency Requirements

- In a production system, `addRequest()` may be called concurrently by many floor panels. The request queues inside each `Elevator` should use `PriorityBlockingQueue` (for SCAN order) or `ConcurrentLinkedQueue`.
- `ElevatorService.addRequest()` should synchronize the elevator-selection logic to prevent two threads from picking the same elevator for two simultaneous requests.

---

## Class Diagram

```
ElevatorService
    ├── elevators: List<Elevator>
    ├── addRequest(floor, direction)
    │       ├── getIdleElevator()          → nearest idle
    │       ├── getSameDirectionElevator() → same-direction elevator not past floor
    │       └── getNearestElevator()       → fallback nearest
    └── tick()   → calls elevator.tick() for each elevator

Elevator
    ├── currentFloor: int
    ├── currentDirection: Direction
    ├── requestQueue (managed by ElevatorStrategy)
    ├── addRequest(floor, direction)
    └── tick()   → move one floor; stop and open doors if floor is in queue

ElevatorStrategy (interface)
    └── getNextFloor(requests, currentFloor, direction) : int

Direction (enum): UP, DOWN, IDLE
```

---

## Design Patterns Used

| Pattern             | Where                                                                           |
| ------------------- | ------------------------------------------------------------------------------- |
| Strategy            | `ElevatorStrategy` — plug in LOOK, FCFS, or any other scheduling algorithm      |
| Observer (implicit) | `ElevatorService` watches elevator states to pick the best one for each request |
