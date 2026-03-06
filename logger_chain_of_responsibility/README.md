# Logger — Chain of Responsibility — Low Level Design

A logging framework redesigned using the Chain of Responsibility pattern, where each log handler decides whether to process or pass a message down the chain.

---

## Class Entities

| Class / Interface                               | Role                                                                             |
| ----------------------------------------------- | -------------------------------------------------------------------------------- |
| `Logger`                                        | Entry point — receives log calls, forwards to the handler chain via `LogManager` |
| `LogManager`                                    | Assembles and holds the root of the handler chain                                |
| `LogHandler` _(abstract)_                       | Chain node — each handler either handles the message or passes it to `next`      |
| `DebugHandler` / `InfoHandler` / `ErrorHandler` | Concrete handlers — each handles its specific `LogLevel`                         |
| `Appender` _(interface)_                        | Output sink — each handler holds multiple appenders                              |
| `ConsoleAppender` / `FileAppender`              | Concrete appenders — write to console or file                                    |
| `Formatter` _(interface)_                       | Formats `LogMessage` into a string                                               |
| `LogMessage`                                    | Immutable record: message text and `LogLevel`                                    |
| `LogLevel` _(enum)_                             | `DEBUG`, `INFO`, `ERROR` — each with a numeric priority                          |

---

## Functional Requirements

1. A log message travels down the handler chain starting from the root.
2. Each handler checks `canHandle(message)` — if true, it notifies its appenders; otherwise, it forwards to `next`.
3. Handlers and appenders can be configured independently.
4. New log levels require adding a new `LogHandler` subclass — no changes to existing handlers.

---

## Non-Functional Requirements

- **Decoupling**: Handlers are unaware of which handler comes after them.
- **Extensibility**: Add new log levels or routing rules by inserting a new `LogHandler` into the chain.
- **Comparison with basic Logger**: The basic logger broadcasts to all appenders; this variant routes to exactly the right handler by level, making per-level routing explicit and configurable.

---

## Concurrency Requirements

- The handler chain is built once and is read-only at runtime — no synchronization needed on the chain structure itself.
- Individual `Appender` implementations (file, DB) must synchronize their own write operations.

---

## Class Diagram

```
Logger
    ├── logManager: LogManager
    └── log(level, message) → logManager.getChain().handle(logMessage)

LogManager
    └── chain: LogHandler (root)  DebugHandler → InfoHandler → ErrorHandler

LogHandler (abstract)
    ├── next: LogHandler
    ├── appenders: List<Appender>
    ├── handle(LogMessage)
    │       └── if canHandle() → notifyAppenders() else → next.handle()
    └── canHandle(LogMessage) ← abstract

Appender (interface)
    └── append(LogMessage)
         ├── ConsoleAppender
         └── FileAppender

Formatter (interface)
    └── format(LogMessage) : String

LogLevel (enum): DEBUG, INFO, ERROR
```

---

## Design Patterns Used

| Pattern                 | Where                                                                  |
| ----------------------- | ---------------------------------------------------------------------- |
| Chain of Responsibility | `LogHandler` chain — each node handles or delegates to next            |
| Strategy                | `Formatter` — pluggable output format                                  |
| Observer                | Each `LogHandler.notifyAppenders()` broadcasts to its set of appenders |
