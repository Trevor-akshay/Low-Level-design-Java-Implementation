# In-Memory Pub/Sub — Low Level Design

A lightweight, in-process publish/subscribe messaging system with offset-based consumption and push notifications.

---

## Class Entities

| Class / Interface          | Role                                                                                                |
| -------------------------- | --------------------------------------------------------------------------------------------------- |
| `Broker`                   | Central hub — stores messages, maintains topic→consumer registry, pushes notifications              |
| `Publisher` / `IPublisher` | Produces messages to a topic via the broker                                                         |
| `Consumer` / `IConsumer`   | Subscribes to topics; tracks its own per-topic offset using `AtomicInteger`                         |
| `ConsumerExecutor`         | Executes consumer callbacks asynchronously when a new message arrives                               |
| `IStore` _(interface)_     | Storage abstraction for append and offset-read                                                      |
| `InMemoryStore`            | `ConcurrentHashMap<topicId, List<Message>>` backed store; `synchronized` per-list for thread safety |
| `Topic`                    | Named channel — identified by UUID and name                                                         |
| `Message`                  | Payload unit — content, UUID, timestamp                                                             |

---

## Functional Requirements

1. Publishers publish messages to a `Topic`; the `Broker` appends to `IStore` and notifies all registered consumers.
2. Consumers can **poll** (pull) messages in batches by supplying an offset and limit.
3. Consumers can **subscribe** (push) — they are notified via `ConsumerExecutor` when new messages arrive.
4. Each consumer tracks its own offset per topic independently — multiple consumers on the same topic see all messages.
5. New consumers can be dynamically registered to a topic via `Broker.addConsumer()`.

---

## Non-Functional Requirements

- **Thread safety**: `ConcurrentHashMap` for topic registration; `synchronized` block on per-topic message lists; `AtomicInteger` offsets.
- **Durability**: In-memory only — messages are lost on restart. A production system would use persistent storage.
- **Extensibility**: Swap `InMemoryStore` for a database-backed `IStore` without touching `Broker` or consumers.

---

## Concurrency Requirements

- `InMemoryStore.publish()` uses `computeIfAbsent` (atomic map insertion) + `synchronized(messages)` (atomic list append).
- `InMemoryStore.getMessagesFromOffset()` returns a **copy** of the sub-list inside the lock to prevent `ConcurrentModificationException` outside the critical section.
- `Consumer` offsets use `AtomicInteger.addAndGet()` — safe for concurrent polling from the same consumer instance.
- `Broker.addConsumer()` uses a write lock; `notifyMessage()` uses a read lock — standard reader-writer pattern.

---

## Class Diagram

```
Publisher ──publish()──► Broker ──────────────────────► IStore
                           │                               │
                           │ notifyMessage()    InMemoryStore
                           │                    ConcurrentHashMap<UUID, List<Message>>
                     ┌─────▼──────┐
                     │ Consumer[] │
                     └─────┬──────┘
                           │ ConsumerExecutor (async)
                           │
                      Consumer
                        ├── consumerId: UUID
                        ├── offSets: ConcurrentHashMap<Topic, AtomicInteger>
                        └── poll(topic, maxMessages) → List<Message>

Topic
    ├── topicId: UUID
    └── name: String

Message
    ├── messageId: UUID
    ├── content: String
    └── timestamp: long
```

---

## Design Patterns Used

| Pattern  | Where                                                                  |
| -------- | ---------------------------------------------------------------------- |
| Observer | `Broker.notifyMessage()` pushes to all subscribed `Consumer` instances |
| Strategy | `IStore` — swap storage backends without touching `Broker`             |
| Command  | `ConsumerExecutor` defers consumer callback execution asynchronously   |
