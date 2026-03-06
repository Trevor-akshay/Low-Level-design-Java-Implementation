package ratelimiter.algorithms; // Package containing rate limiter algorithm implementations.

import java.util.Map; // Stores per-user token bucket state.
import java.util.concurrent.ConcurrentHashMap; // Thread-safe map for concurrent access.
import java.util.concurrent.locks.ReentrantLock; // Per-user mutex for atomic refill+consume.

import ratelimiter.model.Tier; // Tier defines capacity and refill window.
import ratelimiter.model.User; // Domain model representing the caller.

/**
 * Token Bucket rate limiter.
 *
 * Intuition:
 * - Each user has a "bucket" of tokens.
 * - Tokens are added continuously over time at a fixed rate up to a maximum
 * capacity.
 * - Each allowed request consumes 1 token.
 *
 * How this implementation maps to {@code Tier}:
 * - capacity = {@code tier.getTokenAllowed()} (max burst size)
 * - refill rate = capacity tokens per {@code tier.getTimeFrame()} milliseconds
 * (e.g., 10 per 60_000 ms => ~0.0001667 tokens/ms).
 *
 * Why tokens are doubles:
 * - A "true" token bucket refills continuously, which often produces fractional
 * tokens
 * (e.g., 1 token every 6 seconds). Using {@code double} keeps the math simple.
 *
 * Benefits:
 * - Smooth limiting: avoids hard reset boundaries typical of fixed window.
 * - Natural burst support: bucket can start full so a user can burst up to
 * capacity.
 * - O(1) state per user (tokens + lastRefillTime) and O(1) work per request.
 *
 * Trade-offs vs other algorithms:
 * - vs Fixed Window: token bucket is fairer/smoother across time; fixed window
 * is simpler but
 * allows boundary bursts (N at end + N at start).
 * - vs Sliding Window Log: token bucket uses constant memory; sliding window
 * log is exact but
 * can store up to N timestamps per user.
 * - vs Sliding Window Counter: both are O(1) memory, but counter approximates a
 * rolling window
 * whereas token bucket enforces an average rate with burst capacity.
 *
 * Notes:
 * - Time source: uses {@code System.currentTimeMillis()} for simplicity;
 * monotonic time is more
 * robust if the system clock changes.
 * - Concurrency: this implementation is thread-safe and concurrent.
 * - Different users proceed concurrently (no global lock).
 * - Requests for the same user are serialized via a per-user
 * {@link ReentrantLock},
 * making "refill → check tokens → consume" atomic.
 */
public class TokenBucket implements RateLimiter {
	/**
	 * Per-user mutable state.
	 *
	 * Concurrency model:
	 * - The map is a {@link ConcurrentHashMap} so threads can safely look up/create
	 * user state.
	 * - Once a user's state is obtained, a per-user {@link ReentrantLock} protects
	 * the fields so
	 * that refill math and token consumption are atomic for that user.
	 */
	static class UserState {
		private final ReentrantLock lock = new ReentrantLock(); // Serializes updates for this user.
		private long lastRefillTimeMillis; // Last time we accounted for refill (epoch ms).
		private double availableTokens; // Current token balance (can be fractional).

		public UserState(long lastRefillTimeMillis, double initialTokens) {
			this.lastRefillTimeMillis = lastRefillTimeMillis;
			this.availableTokens = initialTokens;
		}

		public void consumeOneToken() {
			this.availableTokens -= 1.0;
		}

		public void setLastRefillTimeMillis(long lastRefillTimeMillis) {
			this.lastRefillTimeMillis = lastRefillTimeMillis;
		}

		public void setAvailableTokens(double availableTokens) {
			this.availableTokens = availableTokens;
		}
	}

	// userId -> per-user token bucket state.
	private final Map<Integer, UserState> userStates;

	public TokenBucket() {
		this.userStates = new ConcurrentHashMap<>();
	}

	@Override
	public boolean canPassThrough(User user) {
		var userId = user.getUserId();
		var tier = user.getUserTier();

		// Current request timestamp (epoch milliseconds).
		long currentTime = System.currentTimeMillis();
		// Capacity is the maximum burst size.
		double capacity = tier.getTokenAllowed();

		// Create or fetch per-user state. New users start with a full bucket.
		UserState userState = userStates.computeIfAbsent(userId, ignored -> new UserState(currentTime, capacity));

		// Lock per-user so the whole sequence is atomic for this user.
		userState.lock.lock();
		try {
			// Bring token balance up-to-date before making the allow/deny decision.
			refill(userState, currentTime, tier);

			// Allow if at least 1 token is available.
			if (userState.availableTokens >= 1.0) {
				// Consume exactly 1 token for this request.
				userState.consumeOneToken();
				// Note: we do not adjust lastRefillTime here; refill() already advanced it.
				return true;
			}

			return false;
		} finally {
			userState.lock.unlock();
		}
	}

	private void refill(UserState userState, long currentTimeMillis, Tier tier) {
		// Continuous refill:
		// tokens += elapsedMillis * (capacity / timeFrameMillis)
		// and cap tokens at capacity.
		long lastRefillTimeMillis = userState.lastRefillTimeMillis;
		long elapsedMillis = currentTimeMillis - lastRefillTimeMillis;
		if (elapsedMillis <= 0) {
			return;
		}

		double capacity = tier.getTokenAllowed();
		double refillRatePerMillis = capacity / (double) tier.getTimeFrame();
		double tokensToAdd = elapsedMillis * refillRatePerMillis;

		double tokens = userState.availableTokens;
		// Cap at capacity to prevent unbounded growth during inactivity.
		tokens = Math.min(capacity, tokens + tokensToAdd);
		userState.setAvailableTokens(tokens);

		// We accounted for the entire elapsed time in tokensToAdd (including
		// fractions),
		// so advance lastRefillTime to currentTime.
		userState.setLastRefillTimeMillis(currentTimeMillis);
	}
}