package ratelimiter.algorithms; // Namespace for rate limiter algorithm implementations.

import java.util.ArrayDeque; // Fast in-memory FIFO/Deque implementation for timestamps.
import java.util.Deque; // Interface for queue-like operations at both ends.
import java.util.Map; // Map interface for per-user state.
import java.util.concurrent.ConcurrentHashMap; // Thread-safe map for concurrent access.
import java.util.concurrent.locks.ReentrantLock; // Explicit per-user mutual exclusion.
import java.util.concurrent.TimeUnit; // Time conversion utilities (ms -> ns).

import ratelimiter.model.User; // Domain model representing the caller/user.

/**
 * Sliding Window (a.k.a. Sliding Window Log) rate limiter.
 *
 * How it works:
 * - For each user, we keep a FIFO queue of timestamps (milliseconds) for
 * requests that were allowed.
 * - On each request, we evict timestamps older than the current rolling window
 * (timeFrame).
 * - If the remaining count is below the tier's limit (tokenAllowed), we allow
 * and record this request.
 *
 * Benefits:
 * - More accurate than Fixed Window: it enforces a true rolling window,
 * avoiding the boundary burst
 * problem where a user can do N requests at the end of one window and N at the
 * start of the next.
 * - Simple and intuitive to explain in interviews.
 *
 * Trade-offs vs other common algorithms:
 * - vs Fixed Window: better smoothing / fairness, but slightly more CPU (needs
 * eviction) and
 * potentially more memory (stores up to N timestamps per user).
 * - vs Token Bucket: token bucket supports controlled burst + smooth refill
 * with O(1) state (tokens +
 * lastRefillTime), while sliding window log can be more memory heavy; however,
 * token bucket is a
 * little harder to reason about precisely at boundaries.
 * - vs Sliding Window Counter: counter uses multiple buckets (e.g., current +
 * previous) with O(1)
 * memory and approximates the rolling window; sliding window log is exact but
 * uses a queue.
 *
 * Notes:
 * - Time source: uses {@code System.nanoTime()} (monotonic) so elapsed-time
 * comparisons
 * are robust even if the system wall clock changes.
 * - Concurrency: thread-safe per user. Different users proceed concurrently;
 * same user
 * requests serialize under a per-user lock to make "evict → check → add"
 * atomic.
 */
public class SlidingWindow implements RateLimiter { // Concrete algorithm plugged into RateLimiterService.
	// Per-user mutable state, grouped so creation and locking are consistent.
	private static final class UserWindow { // Holds all state needed to rate limit ONE user.
		private final ReentrantLock lock = new ReentrantLock(); // Serializes access for this user.
		private final Deque<Long> timeStamps = new ArrayDeque<>(); // FIFO of allowed request times.
	}

	// Map: userId -> that user's sliding-window state.
	private final Map<Integer, UserWindow> userWindows; // Concurrent map supports concurrent users.

	public SlidingWindow() { // Constructor.
		this.userWindows = new ConcurrentHashMap<>(); // Thread-safe map for computeIfAbsent.
	}

	@Override
	public boolean canPassThrough(User user) { // Returns true if this request is allowed.
		var userId = user.getUserId(); // Identify which bucket/state to use.
		var tier = user.getUserTier(); // Tier determines limit (tokenAllowed) and window size.

		// Use monotonic time for elapsed-time comparisons (robust to wall-clock
		// adjustments).
		long nowNanos = System.nanoTime(); // Current instant in nanoseconds (monotonic, not epoch).
		long windowNanos = TimeUnit.MILLISECONDS.toNanos(tier.getTimeFrame()); // Convert ms window to ns.

		// Atomically create or fetch the per-user state.
		// computeIfAbsent ensures only one UserWindow is created per userId (even under
		// concurrency).
		var window = userWindows.computeIfAbsent(userId, ignored -> new UserWindow()); // Per-user state.

		// Acquire the per-user lock so that: refill + size-check + add are a single
		// atomic operation.
		// This is the core concurrency fix: no two threads can both "pass" for the same
		// user
		// based on a stale size() observation.
		window.lock.lock(); // Begin critical section for this user.
		try { // Ensure unlock even if something throws.
				// Evict expired timestamps FIRST so the queue represents only requests in the
				// active window.
			refill(nowNanos, window.timeStamps, windowNanos); // Mutates deque by removing old entries.

			// If remaining requests in the window are below the allowed limit, allow this
			// request.
			if (window.timeStamps.size() < tier.getTokenAllowed()) { // Size = count of allowed requests.
				window.timeStamps.addLast(nowNanos); // Record this request as allowed.
				return true; // Admit request.
			}

			return false; // Reject request (rate limit exceeded for this rolling window).
		} finally {
			window.lock.unlock(); // End critical section for this user.
		}
	}

	private void refill(long nowNanos, Deque<Long> timeStamps, long windowNanos) { // Evicts old entries.
		// Remove timestamps older than the window start.
		// Requirement detail: uses '>' (not '>='). That means a timestamp exactly
		// windowNanos old
		// is still considered inside the window.
		while (!timeStamps.isEmpty() // Stop if there is nothing to evict.
				&& nowNanos - timeStamps.peekFirst() > windowNanos) { // Oldest entry is too old.
			timeStamps.removeFirst(); // Evict the oldest (FIFO).
		}
	}

}