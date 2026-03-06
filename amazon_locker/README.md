# Amazon Locker — Low Level Design

A smart package locker assignment and pickup system, modelled after Amazon Hub Locker.

---

## Class Entities

| Class / Interface     | Role                                                                                      |
| --------------------- | ----------------------------------------------------------------------------------------- |
| `LockerService`       | Entry-point: delegates drop-off and pick-up to `LockerManager`                            |
| `LockerManager`       | Core business logic — assigns lockers, handles pickup, releases slots                     |
| `Locker` _(abstract)_ | Base class with size, type, and `open()` hook; extended by `StandardLocker`, `ColdLocker` |
| `UserPackage`         | Represents the incoming package with size, type, and temperature requirements             |
| `CodeGenerator`       | Utility that generates a unique alphanumeric pickup code                                  |
| `Size` _(enum)_       | `SMALL`, `MEDIUM`, `LARGE` — each has a numeric size value for comparison                 |
| `Type` _(enum)_       | `STANDARD`, `COLD` — cold lockers can also hold standard packages                         |

---

## Functional Requirements

1. A delivery driver drops off a package — the system assigns the smallest compatible available locker.
2. A unique pickup code is generated and sent to the customer (`notifyUser()`).
3. The customer enters the code; the locker opens and the slot is returned to the pool.
4. If no suitable locker is available, `NoLockerAvailableException` is thrown.
5. If a hardware failure occurs opening the locker on drop-off, the system retries with another locker.
6. If an invalid code is supplied at pickup, `InvalidCodeException` is thrown.

---

## Non-Functional Requirements

- **Thread safety**: `ConcurrentHashMap` is used for both the locker pool and the code-to-locker mapping, enabling safe concurrent access.
- **Best-fit allocation**: Sizes are iterated smallest-first, so packages are placed in the smallest locker that fits them.
- **Extensibility**: New locker types (e.g., `FreezerLocker`) only require subclassing `Locker` and updating `acceptanceFactor`.

---

## Concurrency Requirements

- `BlockingQueue<Locker>` per `(Size, Type)` pair ensures atomic slot acquisition via `poll()` — no two requests get the same locker.
- `ConcurrentHashMap.compute()` is used to atomically return a locker to the pool on pickup.
- Recursive retry on hardware failure is bounded by available lockers of the same size — in production, a retry limit / circuit breaker should be added.

---

## Class Diagram

```
LockerService
    └── LockerManager
            ├── lockers: ConcurrentHashMap<Size, ConcurrentHashMap<Type, BlockingQueue<Locker>>>
            ├── codeToLockerMapping: ConcurrentHashMap<String, Locker>
            └── assignLocker(UserPackage) : String
            └── pickUp(code)

UserPackage
    ├── size: Size (SMALL / MEDIUM / LARGE)
    ├── type: Type (STANDARD / COLD)
    └── temperature: int

Locker (abstract)
    ├── size: Size
    ├── lockerType: Type
    └── open()     ← hardware hook
      ├── StandardLocker
      └── ColdLocker
              └── canAccept(int temperature)

Type (enum): STANDARD, COLD
Size (enum): SMALL(1), MEDIUM(2), LARGE(3)
```

---

## Design Patterns Used

| Pattern            | Where                                                                                |
| ------------------ | ------------------------------------------------------------------------------------ |
| Template Method    | `Locker.canAccept()` — subclasses define temperature acceptance logic                |
| Factory            | `LockerManager` orchestrates creation — could introduce a `LockerFactory`            |
| Strategy (implied) | `acceptanceFactor()` can be extracted into a strategy for richer type-matching logic |
