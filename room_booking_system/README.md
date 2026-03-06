# Room Booking System — Low Level Design

A meeting room / hotel room booking system with pluggable availability strategies and per-room concurrent safety.

---

## Class Entities

| Class / Interface                    | Role                                                                |
| ------------------------------------ | ------------------------------------------------------------------- |
| `BookingService`                     | Core service — checks availability and creates `Booking` records    |
| `BookingManager`                     | Orchestrator that exposes booking operations to callers             |
| `Booking`                            | Represents a confirmed room reservation (userId, roomId, time slot) |
| `Room`                               | A bookable room with an ID and associated bookings                  |
| `User`                               | The person making the booking                                       |
| `AvailabilityStrategy` _(interface)_ | Pluggable rule for checking if a room is free in a time slot        |
| `ThirtyMinutesStrategy`              | Enforces minimum 30-minute booking intervals to prevent overlap     |

---

## Functional Requirements

1. A user books a room for a given time slot (startHour, startMinute → endHour, endMinute).
2. The system checks availability using the active `AvailabilityStrategy`.
3. If the room is available, a `Booking` is created and returned.
4. If unavailable, an exception is thrown indicating the conflict.
5. The availability strategy can be swapped at runtime via `setAvailabilityStrategy()`.

---

## Non-Functional Requirements

- **Thread safety**: Per-room `ReentrantLock` ensures that two concurrent bookings for the same room are serialized; different rooms can be booked concurrently.
- **Extensibility**: Swap or extend availability rules by providing a new `AvailabilityStrategy` implementation.

---

## Concurrency Requirements

- `locks.compute(roomId, ...)` atomically creates a per-room lock if one doesn't exist.
- The lock is acquired before calling `isAvailableToBookRoom()` — the check-then-act sequence is atomic per room.
- Bookings for different rooms proceed fully concurrently.

---

## Class Diagram

```
BookingManager
    └── BookingService
            ├── availabilityStrategy: AvailabilityStrategy
            └── locks: ConcurrentHashMap<roomId, ReentrantLock>
            └── book(userId, roomId, startHour, startMinute, endHour, endMinute) : Booking

AvailabilityStrategy (interface)
    └── isAvailableToBookRoom(roomId, ...) : boolean
         └── ThirtyMinutesStrategy

Booking
    ├── userId: int
    └── roomId: int

Room
    ├── roomId: int
    └── bookings: List<Booking>

User
    ├── userId: int
    └── name: String
```

---

## Design Patterns Used

| Pattern              | Where                                                      |
| -------------------- | ---------------------------------------------------------- |
| Strategy             | `AvailabilityStrategy` — plug in different time-slot rules |
| Repository (implied) | `BookingManager` acts as the booking registry              |
