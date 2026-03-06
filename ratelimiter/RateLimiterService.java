package ratelimiter;

import java.util.Map;

import ratelimiter.algorithms.RateLimiter;
import ratelimiter.config.TierManager;
import ratelimiter.enums.RateLimiterAlgorithms;
import ratelimiter.factory.RateLimiterFactory;
import ratelimiter.model.Tier;
import ratelimiter.model.User;

/**
 * RateLimiterService — entry point for the rate-limiting system.
 *
 * <p>
 * Uses the <b>Strategy pattern</b>: the active {@link RateLimiter} algorithm
 * (Token Bucket, Fixed Window, Sliding Window) is injectable and can be swapped
 * at
 * runtime via {@link #setRateLimiter}.
 *
 * <p>
 * Each user belongs to a {@link Tier} that defines their allowed request count
 * and time frame.
 * Tiers are managed by {@link TierManager} and can be added dynamically.
 */
public class RateLimiterService {
	/**
	 * The currently active rate-limiting algorithm — pluggable via
	 * {@link #setRateLimiter}.
	 */
	RateLimiter rateLimiter;
	/** Manages tier configurations (capacity + time window) for each user group. */
	TierManager tierManager;

	/**
	 * Initialises the service with the specified algorithm and pre-configured
	 * tiers.
	 *
	 * @param rateLimiterAlgorithms the algorithm to use (TOKEN_BUCKET,
	 *                              FIXED_WINDOW, SLIDING_WINDOW)
	 * @param tiers                 map of tier name → {@link Tier} configuration
	 */
	public RateLimiterService(RateLimiterAlgorithms rateLimiterAlgorithms, Map<String, Tier> tiers) {
		setRateLimiter(rateLimiterAlgorithms);
		this.tierManager = new TierManager(tiers);
	}

	/**
	 * Decides whether the user's request should be allowed through.
	 *
	 * <p>
	 * Delegates to the active {@link RateLimiter} which evaluates the user's
	 * token/counter
	 * state against their tier's capacity and time window.
	 *
	 * @param user the caller — must have a tier assigned
	 * @return {@code true} if the request is within the rate limit; {@code false}
	 *         if throttled
	 */
	public boolean canPassThrough(User user) {
		return rateLimiter.canPassThrough(user);
	}

	/**
	 * Dynamically adds or updates a tier configuration.
	 *
	 * @param tier         tier identifier (e.g., "free", "pro", "enterprise")
	 * @param tokenAllowed maximum requests allowed in the time frame
	 * @param timeFrame    time window in milliseconds
	 */
	public void addUserTier(String tier, long tokenAllowed, long timeFrame) {
		tierManager.addTier(tier, tokenAllowed, timeFrame);
	}

	/**
	 * Swaps the rate-limiting algorithm — takes effect immediately for all
	 * subsequent requests.
	 * Existing per-user state (counters/tokens) is discarded; users start fresh
	 * with the new algorithm.
	 *
	 * @param rateLimiterAlgorithms the algorithm to switch to
	 */
	public void setRateLimiter(RateLimiterAlgorithms rateLimiterAlgorithms) {
		rateLimiter = RateLimiterFactory.createRateLimiter(rateLimiterAlgorithms);
	}
}
