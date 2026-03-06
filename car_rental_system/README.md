# Car Rental System — Low Level Design

A full-lifecycle car rental system with booking, reservation management, billing, and payment.

> **Note**: A `DESIGN.md` is also present in this folder with additional design notes.

---

## Class Entities

| Class / Interface      | Role                                                                     |
| ---------------------- | ------------------------------------------------------------------------ |
| `CarRentalService`     | Singleton facade — entry point for all user-facing operations            |
| `BookingService`       | Creates reservations after verifying vehicle availability                |
| `ReservationService`   | Manages reservation lifecycle: PENDING → RENTED → COMPLETED / CANCELLED  |
| `BillingService`       | Computes the bill based on actual rental duration                        |
| `PaymentService`       | Processes payment for the computed bill                                  |
| `VehicleRepo`          | In-memory vehicle store — tracks availability                            |
| `UserRepo`             | In-memory user store                                                     |
| `Reservation`          | Core entity: vehicle, user, time slot, status                            |
| `Receipt`              | Issued on vehicle return — breakdown of charges                          |
| `Vehicle` _(abstract)_ | Base vehicle class — subclassed by type                                  |
| `VehicleFactory`       | Creates the correct vehicle subclass by `VehicleType`                    |
| `BillingFactory`       | Selects the billing strategy based on vehicle type or booking parameters |
| `VehicleType` _(enum)_ | `CAR`, `BIKE`, `TRUCK`, etc.                                             |

---

## Functional Requirements

1. Add users and vehicles to the system.
2. Book a vehicle by type for a user between a from/to location and distance (or by explicit time range).
3. Start a rental — transitions the reservation to `RENTED`.
4. Return a vehicle — bills the user, processes payment, and marks the reservation `COMPLETED`; releases the vehicle.
5. Cancel a reservation before it starts.
6. Billing is time-based (actual rental duration from start to return).

---

## Non-Functional Requirements

- **Singleton Pattern**: `CarRentalService.getInstance()` uses double-checked locking to ensure a single shared instance.
- **Separation of concerns**: Booking, reservation state, billing, and payment are independent services.
- **Extensibility**: New vehicle types require a new entry in `VehicleType` and a new factory branch — no service changes.

---

## Concurrency Requirements

- `volatile INSTANCE` in `CarRentalService` combined with `synchronized` block ensures thread-safe singleton construction.
- `VehicleRepo` must atomically check availability and mark a vehicle as reserved — requires a lock per vehicle type or optimistic locking.
- `ReservationService` state transitions should be guarded by per-reservation locks to avoid double-completion or double-cancel.

---

## Class Diagram

```
CarRentalService (Singleton)
    ├── bookingService: BookingService
    ├── reservationService: ReservationService
    ├── billingService: BillingService
    ├── paymentService: PaymentService
    ├── vehicleRepo: VehicleRepo
    └── userRepo: UserRepo

BookingService
    └── book(...) → creates Reservation, assigns Vehicle from VehicleRepo

ReservationService
    ├── startRental(reservationId)   PENDING → RENTED
    ├── complete(reservationId)      RENTED  → COMPLETED
    └── cancel(reservationId)        PENDING → CANCELLED

BillingService
    └── bill(reservationId, billedAt) → Receipt

Receipt
    ├── totalAmount: double
    ├── duration: long
    └── breakdown: Map<BillingComponent, double>

VehicleType (enum): CAR, BIKE, TRUCK, ...
```

---

## Design Patterns Used

| Pattern    | Where                                                             |
| ---------- | ----------------------------------------------------------------- |
| Singleton  | `CarRentalService` — one shared system-wide instance              |
| Factory    | `VehicleFactory`, `BillingFactory` — decouple creation from logic |
| Repository | `VehicleRepo`, `UserRepo` — abstract persistence                  |
| Facade     | `CarRentalService` — single simplified entry point                |
