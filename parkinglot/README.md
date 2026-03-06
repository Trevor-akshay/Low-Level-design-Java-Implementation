# Parking Lot — Low Level Design

A multi-floor parking lot system with dynamic slot allocation, pluggable fee strategies, and pluggable payment methods.

---

## Class Entities

| Class / Interface               | Role                                                                                          |
| ------------------------------- | --------------------------------------------------------------------------------------------- |
| `ParkingLot`                    | Root aggregate — owns floors, delegates to TicketService and PaymentService                   |
| `ParkingFloor`                  | Manages available slots per floor using `BlockingQueue<Slot>` per `SlotType`                  |
| `Slot`                          | Immutable unit of parking space with an ID and a `SlotType`                                   |
| `Ticket`                        | Issued on entry; records entry time, vehicle type, slot, and floor                            |
| `Vehicle` _(abstract)_          | Base class: `Car`, `Bike`, `Bus` — each declares its preferred `SlotType`                     |
| `VehicleFactory`                | Factory to create vehicles by `VehicleType`                                                   |
| `EntryGate`                     | Physical gate that calls `ParkingLot.generateTicket()`                                        |
| `TicketService`                 | Iterates floors to find a compatible slot and creates a `Ticket`                              |
| `PaymentService`                | Calculates fee via `FeeStrategy`, processes payment via `PaymentStrategy`, and frees the slot |
| `FeeStrategy` _(interface)_     | Fee calculation — `TimeBasedFee`, `VehicleBasedFee`                                           |
| `PaymentStrategy` _(interface)_ | Payment collection — `CashPayment`, `CreditCardPayment`, `PaypalPayment`                      |

---

## Functional Requirements

1. Vehicle enters through an `EntryGate`; a `Ticket` is issued with the assigned slot and entry time.
2. Slot assignment uses a best-fit strategy (`ANY → COMPACT → LARGE` for bikes, etc.).
3. On exit, the system calculates the fee based on the chosen `FeeStrategy`.
4. Payment is collected via the chosen `PaymentStrategy` (cash, credit card, PayPal).
5. After successful payment, the slot is freed for future use.
6. Multiple floors are supported; slot search goes floor-by-floor (floor 1 first).

---

## Non-Functional Requirements

- **Thread safety**: `ParkingFloor` uses `BlockingQueue<Slot>` — slot allocation and return are atomic and safe under concurrent access.
- **Extensibility**: New vehicle types, fee strategies, or payment methods require no changes to existing classes (Open/Closed Principle).
- **Reliability**: `PaymentService` throws `PaymentFailedException` if payment fails — the slot is **not** freed in that case.

---

## Concurrency Requirements

- Slot allocation: `BlockingQueue.poll()` is thread-safe — two concurrent vehicles on the same floor will never receive the same slot.
- Slot release: `BlockingQueue.offer()` is also atomic.
- The `TicketService` iterates floors sequentially; a full parking-lot scenario could upgrade this to a `ConcurrentHashMap` with per-floor locks for finer granularity.

---

## Class Diagram

```
                          ┌──────────────┐
                          │  EntryGate   │
                          └──────┬───────┘
                                 │ delegates
                          ┌──────▼───────┐
          ┌───────────────│  ParkingLot  │──────────────────┐
          │               └──────┬───────┘                  │
          │                      │ owns                      │
   ┌──────▼──────┐    ┌──────────▼──────────┐   ┌──────────▼──────┐
   │TicketService│    │    ParkingFloor[]    │   │ PaymentService  │
   └──────────── ┘    └──┬─────────────────-┘   └────────┬────────┘
          │              │ owns (BlockingQueue)            │ uses
   ┌──────▼──────┐    ┌──▼────┐              ┌────────────▼────────────┐
   │   Ticket    │    │ Slot  │              │ FeeStrategy  (interface) │
   └─────────────┘    └───────┘              │  - TimeBasedFee         │
                                             │  - VehicleBasedFee      │
                                             └────────────────────────-┘
                                             ┌────────────────────────-┐
                                             │PaymentStrategy(interface)│
                                             │  - CashPayment          │
                                             │  - CreditCardPayment    │
                                             │  - PaypalPayment        │
                                             └────────────────────────-┘

Vehicle (abstract)
   ├── Car   (prefers COMPACT)
   ├── Bike  (prefers ANY)
   └── Bus   (prefers LARGE)
```

---

## Design Patterns Used

| Pattern         | Where                                                                       |
| --------------- | --------------------------------------------------------------------------- |
| Strategy        | `FeeStrategy`, `PaymentStrategy` — swap algorithms without changing callers |
| Factory         | `VehicleFactory` — decouple vehicle creation from ParkingLot                |
| Template Method | `Vehicle` abstract class dictates the slot-type preference interface        |
