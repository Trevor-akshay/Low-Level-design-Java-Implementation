package ratelimiter.factory;


import ratelimiter.algorithms.FixedWindow;
import ratelimiter.algorithms.RateLimiter;
import ratelimiter.algorithms.SlidingWindow;
import ratelimiter.algorithms.TokenBucket;
import ratelimiter.enums.RateLimiterAlgorithms;

public class RateLimiterFactory {
	public static RateLimiter createRateLimiter(RateLimiterAlgorithms rateLimiterAlgorithms) {
		return switch (rateLimiterAlgorithms) {
			case SLIDING_WINDOW -> new SlidingWindow();
			case FIXED_WINDOW -> new FixedWindow();
			default -> new TokenBucket();
		};
	}
}
