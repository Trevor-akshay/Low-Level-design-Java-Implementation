# Hotel Management — Low Level Design

A hotel room reservation system with concurrent booking support and room status lifecycle management.

---

## Class Entities

| Class / Interface     | Role                                                                       |
| --------------------- | -------------------------------------------------------------------------- |
| `Hotel`               | Core service — manages room inventory, handles bookings and status updates |
| `Room`                | A hotel room with a type, price, and current `RoomStatus`                  |
| `Reservation`         | Issued on successful booking — holds the room, price, and booking date     |
| `HotelRepo`           | In-memory repository of all rooms                                          |
| `RoomType` _(enum)_   | `SINGLE`, `DOUBLE`, `SUITE`, `DELUXE`, etc.                                |
| `RoomStatus` _(enum)_ | `AVAILABLE`, `OCCUPIED`, `MAINTENANCE`, `RESERVED`                         |

---

## Functional Requirements

1. Book a room by type — system selects the first available room of the requested type.
2. On successful booking, the room transitions from `AVAILABLE` → `OCCUPIED`.
3. Room status can be updated manually (e.g., mark room as `MAINTENANCE`, or free it back to `AVAILABLE`).
4. If no available room exists for the requested type, the booking returns `Optional.empty()`.

---

## Non-Functional Requirements

- **Thread safety**: Per-`RoomType` `ReentrantReadWriteLock` ensures that concurrent booking requests for the same room type are serialized while different room types can proceed concurrently.
- **Data consistency**: Room status transitions (`AVAILABLE` → `OCCUPIED`) update both the room object and the status-indexed set atomically within the write lock.
- **Extensibility**: Add new `RoomType` or `RoomStatus` values via the enums without changing service logic.

---

## Concurrency Requirements

- `bookRoom()` uses per-`RoomType` write locks — two concurrent bookings of DOUBLE rooms serialize; a SINGLE and DOUBLE booking proceed concurrently.
- `ConcurrentHashMap.newKeySet()` is used for room sets — safe for concurrent reads outside the lock.
- `locks.computeIfAbsent()` creates per-type locks lazily and atomically.

---

## Class Diagram

```
Hotel
    ├── rooms: Map<roomId, Room>
    ├── roomStatusToRoomMapping: Map<RoomStatus, Map<RoomType, Set<Room>>>
    ├── locks: Map<RoomType, ReentrantReadWriteLock>
    ├── bookRoom(RoomType) → Optional<Reservation>
    └── updateRoomStatus(roomId, RoomStatus)

Room
    ├── roomId: String
    ├── roomType: RoomType
    ├── price: double
    └── roomStatus: RoomStatus  ← mutable

Reservation
    ├── reservationId: UUID
    ├── room: Room
    ├── price: double
    └── bookingDate: Date

RoomType (enum): SINGLE, DOUBLE, SUITE, DELUXE, ...
RoomStatus (enum): AVAILABLE, OCCUPIED, MAINTENANCE, RESERVED
```

---

## Design Patterns Used

| Pattern                  | Where                                                                   |
| ------------------------ | ----------------------------------------------------------------------- |
| Repository               | `HotelRepo` — separates data access from business logic                 |
| Strategy (possible ext.) | Room selection policy could be extracted into a `RoomSelectionStrategy` |
