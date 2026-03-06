# Low Level Design — Java Implementations

A collection of **Low Level Design (LLD)** implementations written in Java. Each module is a self-contained solution to a well-known system design interview problem, built with clean object-oriented principles, real-world design patterns, and proper concurrency handling.

Every module includes:

- A **`README.md`** with class entities, functional/non-functional/concurrency requirements, and a class diagram
- **Detailed Javadoc comments** on all key classes and methods explaining the design decisions

---

## Problems Solved

| #   | Module                                                                 | Problem                                                                                                                                   | Key Patterns                           |
| --- | ---------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------- | -------------------------------------- |
| 1   | [`parkinglot/`](./parkinglot/)                                         | **Parking Lot System** — multi-floor parking with slot types, ticketing, and pluggable payment/fee strategies                             | Strategy, Factory, Template Method     |
| 2   | [`amazon_locker/`](./amazon_locker/)                                   | **Amazon Locker System** — package drop-off and pickup with size/temperature compatibility checks                                         | Factory, Repository                    |
| 3   | [`atm/`](./atm/)                                                       | **ATM Machine** — full card lifecycle (insert → authenticate → withdraw) with a state machine and greedy cash dispensing                  | State, Factory, Template Method        |
| 4   | [`elevator/`](./elevator/)                                             | **Elevator System** — multi-car dispatch with a three-level priority strategy (idle → same-direction → nearest) and tick-based simulation | Strategy, Observer                     |
| 5   | [`hotel_management/`](./hotel_management/)                             | **Hotel Management** — concurrent room booking with per-RoomType locks and a two-level status-indexed room inventory                      | Repository                             |
| 6   | [`in_memory_pub_sub/`](./in_memory_pub_sub/)                           | **In-Memory Pub/Sub System** — topic-based message broker with per-consumer offset tracking and concurrent read-write access              | Observer, Strategy                     |
| 7   | [`logger/`](./logger/)                                                 | **Logger Framework** — multi-appender logging with dual-level filtering (global + per-appender) and pluggable formatters                  | Template Method, Strategy, Observer    |
| 8   | [`logger_chain_of_responsibility/`](./logger_chain_of_responsibility/) | **Logger (Chain of Responsibility)** — level-based log routing through a handler chain instead of fan-out to all appenders                | Chain of Responsibility, Strategy      |
| 9   | [`ratelimiter/`](./ratelimiter/)                                       | **Rate Limiter** — three interchangeable algorithms (Token Bucket, Fixed Window, Sliding Window) with per-user tier configuration         | Strategy, Factory                      |
| 10  | [`room_booking_system/`](./room_booking_system/)                       | **Room Booking System** — meeting room reservations with per-room locks to prevent double-booking and pluggable availability rules        | Strategy, Repository                   |
| 11  | [`snakeAndLadders/`](./snakeAndLadders/)                               | **Snake and Ladders** — configurable board game with extensible obstacle types (snake, ladder, portal) and a round-robin turn loop        | Factory, Template Method               |
| 12  | [`splitwise/`](./splitwise/)                                           | **Splitwise** — shared expense tracking with equal/percentage splits, group management, and minimum-settlement computation                | Strategy, Factory, Repository, Facade  |
| 13  | [`task_management/`](./task_management/)                               | **Task Management System** — multi-user CRUD task tracker with ownership validation and priority/tag support                              | Factory, Repository, DTO               |
| 14  | [`tictactoe/`](./tictactoe/)                                           | **Tic-Tac-Toe** — configurable N×N board game supporting both human (keyboard) and AI players interchangeably                             | Strategy, Decorator                    |
| 15  | [`vending_machine/`](./vending_machine/)                               | **Vending Machine** — state-machine-driven vending with pluggable payment strategies and inventory management                             | State, Strategy, Factory               |
| 16  | [`car_rental_system/`](./car_rental_system/)                           | **Car Rental System** — full vehicle rental lifecycle (book → start → return) with billing, payment, and reservation state management     | Singleton, Factory, Repository, Facade |

---

## Core Design Patterns Demonstrated

| Pattern                     | Where It Appears                                                                                                         |
| --------------------------- | ------------------------------------------------------------------------------------------------------------------------ |
| **State**                   | ATM, Vending Machine — objects change behaviour based on internal state without conditionals                             |
| **Strategy**                | Parking Lot fees/payments, Rate Limiter algorithms, Room Booking availability rules, Logger formatters, Splitwise splits |
| **Factory**                 | ATM states, Vending Machine states, Parking Lot vehicles, Rate Limiter algorithms, Splitwise expense/user creation       |
| **Chain of Responsibility** | Logger (CoR variant) — log level routing through linked handlers                                                         |
| **Observer**                | Pub/Sub broker, Logger appender fan-out                                                                                  |
| **Decorator**               | Tic-Tac-Toe `InputProcessor` wraps `InputStrategy` adding input validation                                               |
| **Singleton**               | Car Rental Service — single system-wide instance via double-checked locking                                              |
| **Template Method**         | ATM state base class (`IAtm`), Logger appender base (`AbstractAppender`)                                                 |
| **Repository**              | Hotel Management, Task Management, Splitwise, Car Rental — isolate in-memory data access                                 |
| **Facade**                  | Splitwise service, Car Rental entry point — single simplified API over multiple subsystems                               |

---

## Concurrency Highlights

All modules that require thread safety use idiomatic Java concurrency primitives:

| Technique                    | Used In                                                    |
| ---------------------------- | ---------------------------------------------------------- |
| `ReentrantReadWriteLock`     | Splitwise (balance ledger), Hotel Management (room status) |
| `ReentrantLock` (per-entity) | Room Booking (per-room), Hotel Management (per-RoomType)   |
| `ReentrantLock` (per-user)   | Rate Limiter Token Bucket — atomic refill + token consume  |
| `ConcurrentHashMap`          | All modules for shared in-memory state                     |
| `BlockingQueue`              | Amazon Locker — FIFO available locker queue per size       |
| `AtomicInteger`              | Pub/Sub consumer offsets                                   |
| `volatile` + `synchronized`  | Car Rental Singleton — double-checked locking              |

---

## Project Structure

```
Low Level design Java Implementation/
├── amazon_locker/          # Each folder is a self-contained LLD module
│   ├── README.md           # Class entities, requirements, class diagram
│   └── *.java              # Documented source files
├── atm/
├── car_rental_system/
├── elevator/
├── hotel_management/
├── in_memory_pub_sub/
├── logger/
├── logger_chain_of_responsibility/
├── parkinglot/
├── ratelimiter/
├── room_booking_system/
├── snakeAndLadders/
├── splitwise/
├── task_management/
├── tictactoe/
└── vending_machine/
```

---

## How to Navigate

1. **Pick a problem** from the table above.
2. **Read the `README.md`** inside the module folder for the design overview, class diagram, and requirements.
3. **Browse the source files** — all key classes have Javadoc explaining their role, design decisions, and concurrency model.
