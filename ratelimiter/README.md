# Rate Limiter — Low Level Design

A pluggable rate limiting service supporting multiple algorithms: Token Bucket, Fixed Window, and Sliding Window.

---

## Class Entities

| Class / Interface                | Role                                                                         |
| -------------------------------- | ---------------------------------------------------------------------------- |
| `RateLimiterService`             | Entry point — delegates `canPassThrough()` to the active `RateLimiter`       |
| `RateLimiter` _(interface)_      | Contract for all rate-limiting algorithms                                    |
| `TokenBucket`                    | Smooth continuous rate limit — tokens refill over time up to capacity        |
| `FixedWindow`                    | Per-window counter — resets at fixed time boundaries                         |
| `SlidingWindow`                  | Rolling window approximation — weighted blend of current and previous window |
| `RateLimiterFactory`             | Creates the correct `RateLimiter` implementation by enum                     |
| `TierManager`                    | Manages per-tier configuration (capacity + time frame)                       |
| `Tier`                           | Defines `tokenAllowed` (capacity) and `timeFrame` (window in ms)             |
| `User`                           | Caller with a `userId` and an assigned `Tier`                                |
| `RateLimiterAlgorithms` _(enum)_ | `TOKEN_BUCKET`, `FIXED_WINDOW`, `SLIDING_WINDOW`                             |

---

## Functional Requirements

1. Each incoming request is checked via `canPassThrough(user)`.
2. The algorithm decides allow/deny based on the user's tier (capacity, time frame).
3. Different users can have different tiers (free, pro, enterprise).
4. The algorithm can be swapped at runtime via `setRateLimiter()`.
5. New tiers can be added dynamically via `addUserTier()`.

---

## Algorithm Comparison

| Algorithm      | Memory        | Accuracy                         | Burst Handling                |
| -------------- | ------------- | -------------------------------- | ----------------------------- |
| Token Bucket   | O(1) per user | Smooth continuous                | Natural burst (full bucket)   |
| Fixed Window   | O(1) per user | Simple but allows boundary burst | Hard reset at window boundary |
| Sliding Window | O(1) per user | Approximates rolling window      | Weighted blend of windows     |

---

## Non-Functional Requirements

- **Extensibility**: New algorithms only implement `RateLimiter` and register in `RateLimiterFactory`.
- **Per-tier configuration**: Rate limits are configurable per tier without code changes.
- **Production note**: Distributed environments require a shared counter store (Redis with Lua scripts) — current implementation is in-memory/per-JVM.

---

## Concurrency Requirements

- `TokenBucket`: Per-user `ReentrantLock` serializes refill + consume atomically; different users proceed concurrently (`ConcurrentHashMap`).
- `FixedWindow` and `SlidingWindow`: Use `AtomicLong` or `synchronized` counters per user.
- `UserState` in `TokenBucket` is accessed only after acquiring the per-user lock — no data races.

---

## Class Diagram

```
RateLimiterService
    ├── rateLimiter: RateLimiter
    └── canPassThrough(User) : boolean

RateLimiter (interface)
    └── canPassThrough(User) : boolean
         ├── TokenBucket     (per-user TokenBucket with continuous refill)
         ├── FixedWindow     (per-user counter, resets at window boundary)
         └── SlidingWindow   (weighted approximation of a rolling window)

RateLimiterFactory
    └── createRateLimiter(RateLimiterAlgorithms) : RateLimiter

User ──────► Tier (tokenAllowed: long, timeFrame: long ms)
TierManager → Map<String, Tier>
```

---

## Design Patterns Used

| Pattern  | Where                                                                  |
| -------- | ---------------------------------------------------------------------- |
| Strategy | `RateLimiter` interface — swap algorithms without changing the service |
| Factory  | `RateLimiterFactory` — decouple instantiation from usage               |
