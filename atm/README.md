# ATM — Low Level Design

A simulated ATM machine using the State design pattern to model the ATM lifecycle: Idle → Card Inserted → Authenticated.

---

## Class Entities

| Class / Interface   | Role                                                                                 |
| ------------------- | ------------------------------------------------------------------------------------ |
| `Atm`               | Context class — holds current `IAtm` state and delegates all operations to it        |
| `IAtm` _(abstract)_ | State base class — all operations throw `FunctionNotAllowed` by default              |
| `Idle`              | State when no card is inserted — only `insertCard()` is permitted                    |
| `CardInserted`      | State after card is inserted — only `validatePin()` and `ejectCard()` permitted      |
| `Authenticated`     | State after PIN validation — `withdraw()`, `fetchBalance()`, `ejectCard()` permitted |
| `Card`              | Represents a physical card linked to a bank `Account`                                |
| `Account`           | Bank account with balance and PIN                                                    |
| `InventoryManager`  | Manages ATM cash vault — denominations and counts; performs greedy note selection    |
| `Dispenser`         | Hardware stub — simulates physical cash dispensing                                   |
| `ATMStateFactory`   | Creates the appropriate `IAtm` state instance by `ATMStates` enum                    |
| `AccountService`    | Validates the PIN against the stored account                                         |

---

## Functional Requirements

1. User inserts card → ATM transitions to `CardInserted` state.
2. User enters PIN → validated by `AccountService`; on success, transitions to `Authenticated`.
3. Authenticated user can fetch balance or withdraw cash.
4. Withdrawal checks: account balance ≥ request, ATM vault ≥ request, and notes are available.
5. After transaction, user can eject card → ATM returns to `Idle`.
6. Any operation disallowed in the current state throws `FunctionNotAllowed`.

---

## Non-Functional Requirements

- **Safety**: State machine prevents illegal transitions — e.g., withdrawing without authentication is impossible.
- **Extensibility**: New states (e.g., `PinRetryState`) can be added without changing existing states.
- **Separation of concerns**: `InventoryManager` handles cash math; `AccountService` handles PIN validation; `Dispenser` handles hardware.

---

## Concurrency Requirements

- In a multi-terminal deployment, `InventoryManager` would need synchronization on denomination counts. Current implementation is single-threaded.
- `Account` balance updates should be transactional in production (two-phase commit with the bank).

---

## Class Diagram

```
                     ┌──────────────────────┐
                     │          Atm         │
                     │  - state: IAtm       │
                     │  - currentCard: Card  │
                     │  - inventoryManager  │
                     └────────┬─────────────┘
                              │ delegates to
                ┌─────────────▼──────────────┐
                │      IAtm (abstract)        │
                │  + validatePin()            │
                │  + withdraw()               │
                │  + fetchBalance()           │
                │  + insertCard()             │
                │  + ejectCard()              │
                └────┬──────────┬────────────┘
              ┌──────▼──┐   ┌───▼────────┐   ┌────────────────┐
              │  Idle   │   │CardInserted│   │ Authenticated   │
              └─────────┘   └────────────┘   └────────────────┘

Card ─────────► Account (balance, pin)
InventoryManager ── denominationCounts: Map<CashDenominators, Integer>
```

---

## Design Patterns Used

| Pattern         | Where                                                                                 |
| --------------- | ------------------------------------------------------------------------------------- |
| State           | `IAtm` / `Idle` / `CardInserted` / `Authenticated` — encapsulates per-state behaviour |
| Factory         | `ATMStateFactory.createState(ATMStates)` — decouples state instantiation              |
| Template Method | `IAtm` base provides default `throw FunctionNotAllowed` for all operations            |
