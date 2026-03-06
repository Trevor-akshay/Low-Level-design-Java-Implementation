# Vending Machine — Low Level Design

A fully state-machine-driven vending machine with pluggable payment strategies and inventory management.

---

## Class Entities

| Class / Interface                | Role                                                                    |
| -------------------------------- | ----------------------------------------------------------------------- |
| `VendingMachine`                 | Context — holds current state and delegates all user interactions to it |
| `State` _(interface)_            | Defines all vending machine operations per state                        |
| `IdleState`                      | Default state — only `initializeSelection()` is permitted               |
| `SelectionState`                 | User is browsing — `selectAisle()` is permitted                         |
| `PaymentState`                   | Item selected — user must `pay()`                                       |
| `DispenseState`                  | Payment successful — item is dispensed via `dispense()`                 |
| `InventoryManager`               | Manages aisles and items — tracks stock counts                          |
| `Aisle`                          | A slot in the machine with an item type and count                       |
| `Item`                           | A product that can be dispensed                                         |
| `IPaymentStrategy` _(interface)_ | Pluggable payment: `CashPayment`, `CreditPayment`                       |
| `StateFactory`                   | Creates the correct `State` instance by `MachineStates` enum            |
| `ItemFactory`                    | Creates `Item` instances                                                |
| `Dispenser` / `Motor`            | Hardware abstractions for physical dispensing                           |
| `MachineStates` _(enum)_         | `IDLE`, `SELECTION`, `PAYMENT`, `DISPENSE`                              |

---

## Functional Requirements

1. User presses a button → machine enters `SelectionState`.
2. User selects an aisle → machine validates stock and transitions to `PaymentState`.
3. User pays → machine validates payment and transitions to `DispenseState`.
4. Machine dispenses item, decrements stock, and returns to `IdleState`.
5. Any operation not allowed in the current state throws `ActionNotAllowedException`.
6. If an item is out of stock, `ItemOutOfStockException` is thrown during aisle selection.
7. Hardware failures during dispensing throw `HardwareFailureException`.

---

## Non-Functional Requirements

- **Safety**: State machine prevents invalid operation sequences — cannot pay before selecting, cannot dispense before paying.
- **Extensibility**: New states or new payment methods require no changes to existing classes.
- **Separation of concerns**: `InventoryManager` handles stock; `IPaymentStrategy` handles payment; `State` classes handle flow control.

---

## Concurrency Requirements

- In a networked vending machine, state transitions must be synchronized to prevent two users triggering a dispense of the same last item.
- `InventoryManager` should use `AtomicInteger` for stock counts to handle concurrent access safely.

---

## Class Diagram

```
VendingMachine
    ├── currentState: State
    ├── inventoryManager: InventoryManager
    ├── paymentStrategy: IPaymentStrategy
    └── currentAisle: Aisle

State (interface)
    ├── initializeSelection()
    ├── selectAisle(inventoryManager, aisleId) : Aisle
    ├── pay(paymentStrategy, price) : boolean
    └── dispense(inventoryManager, aisleId) : Item
         ├── IdleState       → only initializeSelection() allowed
         ├── SelectionState  → only selectAisle() allowed
         ├── PaymentState    → only pay() allowed
         └── DispenseState   → only dispense() allowed

InventoryManager
    └── aisles: Map<aisleId, Aisle>

IPaymentStrategy (interface)
    └── pay(amount) : boolean
         ├── CashPayment
         └── CreditPayment

MachineStates (enum): IDLE, SELECTION, PAYMENT, DISPENSE
```

---

## Design Patterns Used

| Pattern                   | Where                                                                       |
| ------------------------- | --------------------------------------------------------------------------- |
| State                     | `State` interface + 4 concrete states — each encapsulates allowed behaviour |
| Strategy                  | `IPaymentStrategy` — swap payment methods without touching the machine      |
| Factory                   | `StateFactory` (states), `ItemFactory` (items)                              |
| Template Method (implied) | `State` default methods throw `ActionNotAllowedException`                   |
