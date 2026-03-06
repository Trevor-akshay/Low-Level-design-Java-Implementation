package ratelimiter.config;

import java.util.Map;

import ratelimiter.model.Tier;

public class TierManager {
	private final Map<String, Tier> tiers;

	public TierManager(Map<String, Tier> tiers) {
		this.tiers = tiers;
	}

	public void addTier(String name, long tokenAllowed, long timeFrame) {
		tiers.put(name, new Tier(name, tokenAllowed, timeFrame));
	}

	public Tier getTierConfig(String tier) {
		if (tier == null || tiers.get(tier) == null)
			throw new Error("Tier not available");

		return tiers.get(tier);
	}

}
