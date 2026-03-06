# Splitwise — Low Level Design

A shared expense tracking and debt settlement system, modelled after Splitwise. Supports equal and percentage expense splits, groups, and minimum-settlement computation.

---

## Class Entities

| Class / Interface                                 | Role                                                                             |
| ------------------------------------------------- | -------------------------------------------------------------------------------- |
| `SplitwiseService`                                | Main service — thread-safe entry point for all operations                        |
| `User`                                            | A participant with a `userId` and a balance ledger                               |
| `Expense`                                         | Records a payment: who paid, total amount, description, split type, participants |
| `Group`                                           | Named collection of users and their shared expenses                              |
| `UserRepo`                                        | Manages user balances and inter-user debt maps                                   |
| `ExpenseRepo`                                     | Persists and retrieves expenses                                                  |
| `GroupRepo`                                       | Persists groups and their expense lists                                          |
| `SplitStrategy` _(interface)_                     | Computes how to split an expense among participants                              |
| `EqualSplitStrategy`                              | Divides amount equally, crediting the payer and debiting each participant        |
| `PercentageSplitStrategy`                         | Splits by user-defined percentages                                               |
| `SplitStrategyFactory`                            | Creates the correct strategy for a `SplitType`                                   |
| `ExpenseFactory` / `UserFactory` / `GroupFactory` | Factories for domain objects                                                     |
| `SplitType` _(enum)_                              | `EQUAL`, `PERCENTAGE`                                                            |

---

## Functional Requirements

1. Record an expense paid by one user, split among a list of participants (with or without a group).
2. Query any user's net balance (positive = owed money, negative = owes money).
3. Query per-creditor debt breakdown (`getDebtOf(user)`).
4. Settle a specific debt from payer → payee.
5. Compute minimum number of transactions to clear all debts (`getMinimumSettlements()`).
6. Create / delete groups; add / remove users from groups.
7. Query all expenses for a group or a user.
8. Modify an existing expense (amount or description).
9. Change the split strategy for future expenses.

---

## Non-Functional Requirements

- **Thread safety**: All write operations use `ReentrantReadWriteLock` write locks; reads use read locks — multiple concurrent readers are non-blocking.
- **Correctness**: `validateExpenseInputs()` enforces positive amount, non-empty participant list, and payer in the list.
- **Extensibility**: New split types require a new `SplitStrategy` implementation and a `SplitType` enum entry.

---

## Concurrency Requirements

- `ReentrantReadWriteLock` allows concurrent reads and serializes writes.
- `recordExpense()` holds the write lock for the full duration (balance update + expense save) to keep the ledger consistent.
- `settlePayment()` also holds the write lock — avoids race conditions where two settlements of the same debt can proceed simultaneously.

---

## Class Diagram

```
SplitwiseService
    ├── userRepo: UserRepo         (balance ledger, debt maps)
    ├── expenseRepo: ExpenseRepo   (expense store)
    ├── groupRepo: GroupRepo       (group + expense registry)
    ├── splitStrategy: SplitStrategy
    └── locks: ReentrantReadWriteLock

SplitStrategy (interface)
    └── split(paidBy, users, amount) : Map<User, Double>
         ├── EqualSplitStrategy
         └── PercentageSplitStrategy

Expense
    ├── paidBy: User
    ├── amount: double
    ├── users: List<User>
    └── splitType: SplitType

Group
    ├── groupId: UUID
    ├── name: String
    └── expenses: List<Expense>

User ──────► balance: double, debts: Map<User, Double>

SplitType (enum): EQUAL, PERCENTAGE
```

---

## Design Patterns Used

| Pattern    | Where                                                                   |
| ---------- | ----------------------------------------------------------------------- |
| Strategy   | `SplitStrategy` — plug in EQUAL, PERCENTAGE, or custom split logic      |
| Factory    | `ExpenseFactory`, `UserFactory`, `GroupFactory`, `SplitStrategyFactory` |
| Repository | `UserRepo`, `ExpenseRepo`, `GroupRepo` — isolate data access            |
| Facade     | `SplitwiseService` — single simplified entry point for all operations   |
