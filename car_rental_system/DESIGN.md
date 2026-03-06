# Car Rental System — Design Notes

This document explains the high-level flow, design patterns used, SOLID principles applied, and how concurrency/thread-safety is achieved.

## 1) High-level flow

### A) Onboarding / setup

1. Create users (via `UserFactory`, stored in `UserRepo`).
2. Add vehicles (via `VehicleFactory`, stored in `VehicleRepo`).
3. Configure strategies:
   - Booking strategy (e.g., `SimpleBooking`) via `BookingService`.
   - Billing strategy (e.g., `TimeBasedBilling`, `DistanceBasedBilling`) via `BillingService`.
   - Payment strategy (e.g., `CashPayment`, `CreditPayment`) via `PaymentService`.

### B) Booking a vehicle

Typical booking flow:

1. Client asks `BookingService` to create a reservation for a given `VehicleType`.
2. The booking strategy (`SimpleBooking`) iterates vehicles of that type and tries to reserve one using the vehicle state machine (`AVAILABLE -> RESERVED`).
3. `ReservationRepo` creates a **time-window** reservation and rejects overlapping reservations for the same vehicle.
4. The reservation is returned to the client.

Time-window rule (conflict check): intervals are treated as half-open: `[startTimeMillis, endTimeMillis)`.

### C) Billing and receipt generation

1. After the reservation completes (e.g., trip ends), `BillingService` computes the charge using a configured `BillingStrategy`.
2. `ReceiptFactoy` creates a `Receipt` for the reservation + billed amount.

### D) Payment

1. Client chooses a payment method.
2. `PaymentService` executes the payment via a configured `PaymentStrategy` (cash/credit).
3. Payment success/failure is reported back.

## 2) Design patterns used

### Strategy Pattern

Used where you want to switch behavior at runtime without changing calling code.

- Billing strategies:
  - Interface: `billing/BillingStrategy`
  - Implementations: `billing/TimeBasedBilling`, `billing/DistanceBasedBilling`
  - Used by: `service/BillingService`

- Payment strategies:
  - Interface: `payment/PaymentStrategy`
  - Implementations: `payment/CashPayment`, `payment/CreditPayment`
  - Used by: `service/PaymentService`

- Booking strategies:
  - Interface: `booking/BookingStrategy`
  - Implementation: `booking/SimpleBooking`
  - Used by: `service/BookingService`

Why it fits:

- New billing/payment/booking behavior can be added by implementing the interface, without rewriting services.

### Factory Pattern

Used to centralize object creation logic and hide construction details.

- `factories/VehicleFactory`: creates `Vehicle` based on `VehicleType`.
- `factories/UserFactory`: creates `User`.
- `factories/ReservarionFactory`: creates `Reservation`.
- `factories/ReceiptFactoy`: creates `Receipt`.

Why it fits:

- Keeps construction rules in one place (e.g., default attributes, IDs).
- Calling code doesn’t need to know which concrete class is created.

### Repository Pattern (in-memory)

Used to isolate persistence/storage concerns.

- `database/VehicleRepo`, `database/UserRepo`, `database/ReservationRepo`

Why it fits:

- Services can depend on repositories and remain agnostic to storage (in-memory today; DB later).

## 3) SOLID principles applied

### S — Single Responsibility Principle (SRP)

Examples:

- `BillingService` focuses on billing logic; strategies focus on _how_ to compute price.
- `PaymentService` focuses on payment orchestration; payment strategies focus on _how_ to pay.
- Factories focus on object creation; repositories focus on storage.

### O — Open/Closed Principle (OCP)

Examples:

- Add a new `BillingStrategy` (e.g., surge pricing) without modifying `BillingService`.
- Add a new `PaymentStrategy` (e.g., UPI/Wallet) without modifying `PaymentService`.

### L — Liskov Substitution Principle (LSP)

Examples:

- Any `BillingStrategy` should be swappable anywhere `BillingStrategy` is expected.
- Any `PaymentStrategy` should be swappable anywhere `PaymentStrategy` is expected.

### I — Interface Segregation Principle (ISP)

Examples:

- Separate small, focused interfaces like `BillingStrategy`, `PaymentStrategy`, `BookingStrategy`.
- Avoids forcing implementers to depend on methods they don’t use.

### D — Dependency Inversion Principle (DIP)

Examples:

- Services depend on abstractions (strategy interfaces) rather than concrete implementations.
- This reduces coupling and improves testability.

## 4) Concurrency and thread-safety

### A) What needs to be thread-safe?

The repository layer is a shared mutable state in typical service architectures:

- Multiple threads may try to:
  - read vehicle details,
  - select an available vehicle of a type,
  - add/remove vehicles,
  - create reservations.

Without coordination, you can get:

- inconsistent views (e.g., map updated halfway),
- race conditions (two threads “book” the same vehicle),
- exceptions while iterating if the underlying collections are not safe.

### B) Approach in `VehicleRepo`

`database/VehicleRepo` uses **two layers** of concurrency control:

1. **Repository structure consistency via `ReentrantReadWriteLock`**

- The maps `vehicles` and `vehiclesPerType` must remain consistent with each other.
- Operations like `addVehicle` update both maps; those are _compound_ operations.

Locking policy:

- Read operations acquire `lock.readLock()`
  - `getVehicle(...)`
  - `getVehicleFromType(...)` while reading/iterating the maps
- Write operations acquire `lock.writeLock()`
  - `addVehicle(...)` (updates both maps atomically)
  - `removeVehicle(...)` (marks vehicle `OUT_OF_SERVICE` and removes from indexes)

Why a read/write lock is useful here:

- Many callers can read concurrently.
- Writes are still safe and exclusive.
- This is generally more throughput-friendly than synchronizing every method.

2. **Per-vehicle state machine via atomic CAS**
   After finding a candidate vehicle, booking uses an atomic compare-and-set on `VehicleState`:

- `vehicle.tryReserve()` performs `AVAILABLE -> RESERVED`

Why this matters:

- Even if many threads iterate concurrently, only one can “win” the reservation for a given vehicle.
- It avoids turning every booking attempt into a global write-lock, which would serialize all bookings.

### C) Thread-safety scope and caveats

- The read/write lock ensures _repository maps_ are accessed safely.
- The CAS ensures per-vehicle state transitions are safe even under heavy contention.

## 5) Vehicle & reservation lifecycle

### Vehicle lifecycle (`VehicleState`)

- `AVAILABLE -> RESERVED -> RENTED -> AVAILABLE`
- `OUT_OF_SERVICE` means removed from circulation (maintenance/decommissioned)

### Reservation lifecycle (`ReservationStatus`)

- `RESERVED -> RENTED -> COMPLETED`
- `RESERVED -> CANCELLED`

The `service/ReservationService` coordinates reservation transitions with vehicle transitions so that:

- Cancelling releases the vehicle (`RESERVED -> AVAILABLE`)
- Completing releases the vehicle (`RENTED -> AVAILABLE`)

## 5) Suggested extensions (optional)

If you want a more production-like design:

- Replace `Error` with domain-specific exceptions (e.g., `VehicleNotFoundException`, `NoVehicleAvailableException`).
- Make repositories fully own their internal collections (construct `ConcurrentHashMap` inside) to prevent callers from passing non-thread-safe maps.

---

If you want, I can also add a short sequence diagram (text-based) for the booking/billing/payment flow.
