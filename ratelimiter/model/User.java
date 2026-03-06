package ratelimiter.model;

public class User {
	private final int userId;
	private Tier userTier;

	public User(int userId, Tier userTier) {
		this.userId = userId;
		this.userTier = userTier;
	}

	public void setUserTier(Tier userTier) {
		this.userTier = userTier;
	}

	public Tier getUserTier() {
		return userTier;
	}

	public int getUserId() {
		return userId;
	}
}
