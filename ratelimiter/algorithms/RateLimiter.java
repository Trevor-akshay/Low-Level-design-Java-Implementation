package ratelimiter.algorithms;

import ratelimiter.model.User;

public interface RateLimiter {
	boolean canPassThrough(User user);
}
