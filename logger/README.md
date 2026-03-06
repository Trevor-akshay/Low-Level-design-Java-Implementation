# Logger — Low Level Design

A flexible logging framework with multiple appenders and formatters, inspired by Log4j.

---

## Class Entities

| Class / Interface               | Role                                                                           |
| ------------------------------- | ------------------------------------------------------------------------------ |
| `Logger`                        | Entry point — accepts log calls, filters by level, fans out to all appenders   |
| `AbstractAppender` _(abstract)_ | Base appender — filters by its own configured level, then calls `write()`      |
| `ConsoleAppender`               | Writes formatted log messages to `System.out`                                  |
| `FileAppender`                  | Writes formatted log messages to a file (append or overwrite mode)             |
| `Formatter` _(interface)_       | Formats a `LogMessage` into a string — `PlainTextFormatter`                    |
| `LogMessage`                    | Immutable log record: message text, timestamp, `LogLevel`                      |
| `LogLevel` _(enum)_             | `DEBUG`, `INFO`, `WARNING`, `ERROR`, `FATAL` — each carries a numeric priority |

---

## Functional Requirements

1. Log at any of five levels (DEBUG, INFO, WARNING, ERROR, FATAL).
2. The `Logger` global level acts as a coarse filter — messages below configured level are discarded.
3. Each appender further filters by its own configured level, enabling different sinks to capture different severities.
4. Appenders are independently configurable — add, remove, or reconfigure at runtime.
5. Formatters decouple the output format (plain text, JSON, etc.) from message handling.

---

## Non-Functional Requirements

- **Extensibility**: Add new appenders (e.g., `DatabaseAppender`, `SlackAppender`) by subclassing `AbstractAppender` without touching `Logger`.
- **Extensibility of format**: New format styles only require implementing `Formatter`.
- **Performance**: The level check in `Logger.log()` short-circuits before any formatting or I/O if the message is below the configured threshold.

---

## Concurrency Requirements

- `notifyAllAppenders()` iterates over the appenders list — in a multi-threaded scenario, this list should be protected with a `ReadWriteLock` or replaced with `CopyOnWriteArrayList`.
- `FileAppender` writes to a file — in production, a `BufferedWriter` with `synchronized` access or a background thread queue (async logging) should be used.

---

## Class Diagram

```
Logger
    ├── configuredLogLevel: LogLevel
    ├── appenders: List<AbstractAppender>
    └── log(level, message)
            └── notifyAllAppenders(LogMessage)

AbstractAppender
    ├── configuredLogLevel: LogLevel
    ├── formatter: Formatter
    ├── shouldLog(LogMessage) : boolean
    └── append(LogMessage)
         └── write(LogMessage) ← abstract
              ├── ConsoleAppender
              └── FileAppender

Formatter (interface)
    └── format(LogMessage) : String
         └── PlainTextFormatter

LogLevel (enum): DEBUG(1), INFO(2), WARNING(3), ERROR(4), FATAL(5)
```

---

## Design Patterns Used

| Pattern         | Where                                                                   |
| --------------- | ----------------------------------------------------------------------- |
| Template Method | `AbstractAppender.append()` defines the skeleton; `write()` is the hook |
| Strategy        | `Formatter` — swap output format without changing appenders             |
| Observer        | `Logger.notifyAllAppenders()` — broadcasts to all registered appenders  |
