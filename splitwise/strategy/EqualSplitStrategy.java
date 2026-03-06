package splitwise.strategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import splitwise.models.User;

/**
 * Strategy for splitting expenses equally among all participants.
 * Each user (including the payer) pays an equal share of the total amount.
 * 
 * Algorithm:
 * 1. Calculate share per person = total amount / number of users
 * 2. For the payer: they get credit for the full amount they paid
 * 3. For others: they owe their share (shown as negative value)
 * 
 * Example:
 * Alice pays ₹300 for dinner for 3 people (Alice, Bob, Charlie)
 * Share per person = 300 / 3 = ₹100
 * 
 * Results:
 * - Alice: +200 (she paid 300, owes 100 herself, so net +200)
 * - Bob: -100 (owes ₹100)
 * - Charlie: -100 (owes ₹100)
 * 
 * Total check: 200 + (-100) + (-100) = 0 ✓
 */
public class EqualSplitStrategy implements SplitStrategy {
	@Override
	public Map<User, Double> split(User paidBy, List<User> users, double amount) {
		int totalUsers = users.size();

		// Calculate the share per person
		Double sharePerUser = amount / totalUsers;

		// Map to store balance changes for each user
		Map<User, Double> splitUserMapping = new HashMap<>();

		// For each user, calculate their balance change
		for (var user : users) {
			if (user.equals(paidBy)) {
				// Payer paid the full amount but also owes their share
				// So net balance = amount paid - share owed
				// Example: If Alice pays 300 and owes 100, she gets +200
				splitUserMapping.put(user, amount - sharePerUser);
			} else {
				// Other users owe their share (negative indicates they owe money)
				// Example: If Bob's share is 100, he gets -100
				splitUserMapping.put(user, -sharePerUser);
			}
		}

		return splitUserMapping;
	}
}
