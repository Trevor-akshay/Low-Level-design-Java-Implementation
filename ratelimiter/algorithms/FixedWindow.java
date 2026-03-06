package ratelimiter.algorithms; // Package for different rate limiting algorithms.

import java.util.Map; // Stores per-user state.
import java.util.concurrent.ConcurrentHashMap; // Thread-safe map so many threads can access per-user state.
import java.util.concurrent.locks.ReentrantLock; // Per-user mutex to make check+update atomic.

import ratelimiter.model.User; // Domain model for the caller.

/**
 * Fixed Window rate limiter (per-user counter).
 *
 * How it works:
 * - For each user we track:
 * 1) the start timestamp of the current window (userToStartTime)
 * 2) how many requests have been allowed in that window (userToTokens)
 * - If the elapsed time since window start is >= timeFrame, we reset the window
 * and counter.
 * - If the counter is below the allowed limit (tokenAllowed), we increment and
 * allow the request.
 *
 * What {@code Tier} means here:
 * - {@code getTokenAllowed()} is the maximum allowed requests per window (e.g.,
 * 10).
 * - {@code getTimeFrame()} is the window duration in milliseconds (e.g., 60_000
 * for 1 minute).
 *
 * Benefits:
 * - O(1) per request: constant time check and update.
 * - O(1) memory per user: just a counter and a window start time.
 * - Very simple to implement and reason about.
 *
 * Trade-offs vs other algorithms:
 * - vs Sliding Window Log: fixed window can allow "boundary bursts" (N requests
 * right before the
 * window ends and N right after it resets). Sliding window log avoids that but
 * uses more memory.
 * - vs Token Bucket: token bucket provides smooth refill and controlled bursts,
 * while fixed window
 * is coarse (hard reset at boundaries). Token bucket typically needs fractional
 * math/time.
 * - vs Sliding Window Counter: counter approximates a rolling window using
 * buckets; more accurate
 * than fixed window at boundaries, still O(1) memory, but slightly more
 * complex.
 *
 * Notes:
 * - This implementation's window is per-user and starts at the user's first
 * request. Some systems
 * use globally aligned windows (e.g., wall-clock minute boundaries); behavior
 * differs at edges.
 * - Concurrency: this implementation is thread-safe and concurrent.
 * - Different users proceed concurrently (no global lock).
 * - Requests for the same user are serialized using a per-user
 * {@link ReentrantLock},
 * making the sequence "check window → maybe reset → check tokens → increment"
 * atomic.
 */
public class FixedWindow implements RateLimiter { // Fixed window counter implementation.
	/**
	 * Per-user mutable state.
	 *
	 * Concurrency model:
	 * - The {@code userWindow} map is a {@link ConcurrentHashMap}, so threads can
	 * safely look up/create per-user state concurrently.
	 * - Once a user's state is obtained, a per-user {@link ReentrantLock} protects
	 * the state fields so that updates are atomic for that user.
	 */
	static class UserState { // Holds state for ONE user.
		private final ReentrantLock lock = new ReentrantLock(); // Serializes access to this user's fields.
		private long timeStamp; // Window start time in milliseconds (epoch ms).
		private long tokens; // Count of allowed requests in the current window.

		public UserState(long timeStamp, long tokens) { // Construct initial state for a new user.
			this.timeStamp = timeStamp; // Initial window starts at first request time.
			this.tokens = tokens; // Initially, 0 requests have been allowed.
		}

		public void setTokens(long tokens) { // Update allowed-count.
			this.tokens = tokens;
		}

		public void setTimeStamp(long timeStamp) { // Update window start time.
			this.timeStamp = timeStamp;
		}
	}

	// userId -> per-user fixed window state.
	private final Map<Integer, UserState> userWindow;
	// Note: commented code below shows an earlier non-concurrent approach using
	// separate maps.
	// Keeping it for reference; the current design stores both values in UserState.
	// private final Map<Integer, Use> userToStartTime;

	public FixedWindow() { // Constructor.
		this.userWindow = new ConcurrentHashMap<>(); // Thread-safe per-user storage.
		// this.userToStartTime = new HashMap<>();
	}

	@Override
	public boolean canPassThrough(User user) { // Return true if the request should be allowed.
		int userId = user.getUserId(); // Identify user.
		var tier = user.getUserTier(); // Tier contains window size and max allowed count.

		// Current request time (epoch milliseconds).
		long currentTime = System.currentTimeMillis();

		// Create or fetch this user's state atomically.
		// computeIfAbsent ensures only one UserState is created per userId under race.
		UserState userState = userWindow.computeIfAbsent(userId,
				ignored -> new UserState(currentTime, 0L));

		// Lock per-user so "check/reset → check capacity → increment" is atomic.
		// This prevents the classic check-then-act race where two threads could both
		// observe "tokens < limit" and both increment.
		userState.lock.lock();
		try {
			long startTime = userState.timeStamp; // Start of the current fixed window.

			// If the window has expired, start a new window and reset the counter.
			// Using ">=" means a request exactly at the boundary begins a new window.
			if (currentTime - startTime >= tier.getTimeFrame()) {
				userState.setTimeStamp(currentTime); // New window begins now.
				userState.setTokens(0L); // Reset allowed count for the new window.
			}

			// Allow if user has remaining capacity in this fixed window.
			if (userState.tokens < tier.getTokenAllowed()) {
				// Record that this request was allowed.
				userState.setTokens(userState.tokens + 1L);
				return true;
			}

			return false; // Reject: this user exhausted their quota for the current window.
		} finally {
			userState.lock.unlock(); // Always release lock.
		}
	}

}