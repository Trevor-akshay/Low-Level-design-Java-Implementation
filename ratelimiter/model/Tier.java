package ratelimiter.model;

public class Tier {
	private String tier;

	private long tokenAllowed;
	private long timeFrame;

	public Tier(String tier, long tokenAllowed, long timeFrame) {
		this.tier = tier;
		this.tokenAllowed = tokenAllowed;
		this.timeFrame = timeFrame;
	}

	public String getTier() {
		return tier;
	}

	public long getTokenAllowed() {
		return tokenAllowed;
	}

	public long getTimeFrame() {
		return timeFrame;
	}

	public void setTier(String tier) {
		this.tier = tier;
	}

	public void setTokenAllowed(long tokenAllowed) {
		this.tokenAllowed = tokenAllowed;
	}

	public void setTimeFrame(long timeFrame) {
		this.timeFrame = timeFrame;
	}
}
