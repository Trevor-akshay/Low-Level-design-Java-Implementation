package splitwise.strategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import splitwise.models.User;
import splitwise.exceptions.InvalidAmountException;

/**
 * Strategy for splitting expenses based on percentage shares.
 * Each user pays a specified percentage of the total amount.
 * 
 * This is useful when expenses are split unequally.
 * Each user should have an associated percentage that determines their share.
 * 
 * Example:
 * Alice pays ₹300 for groceries
 * Percentages: Alice 40%, Bob 30%, Charlie 30%
 * 
 * Calculation:
 * - Alice's share: 300 * 40% = ₹120
 * - Bob's share: 300 * 30% = ₹90
 * - Charlie's share: 300 * 30% = ₹90
 * 
 * Results:
 * - Alice: +180 (she paid 300, owes 120, so net +180)
 * - Bob: -90 (owes ₹90)
 * - Charlie: -90 (owes ₹90)
 * 
 * Total check: 180 + (-90) + (-90) = 0 ✓
 * 
 * Note: This is a basic implementation. In a real system, you might want to:
 * 1. Store percentages in a separate data structure (e.g., Expense model)
 * 2. Validate that percentages sum to 100%
 * 3. Handle rounding errors for decimal amounts
 */
public class PercentageSplitStrategy implements SplitStrategy {

	/**
	 * Splits an expense based on predefined percentages.
	 * 
	 * IMPORTANT: This method currently requires percentages to be configured
	 * in the Expense model. This is a placeholder implementation that needs
	 * to be extended with actual percentage data retrieval.
	 * 
	 * @param paidBy User who paid the full amount
	 * @param users  List of users sharing the expense
	 * @param amount Total expense amount
	 * @return Map of users to their balance change (positive = owed to them,
	 *         negative = they owe)
	 * @throws UnsupportedOperationException if percentages are not configured in
	 *                                       Expense
	 */
	@Override
	public Map<User, Double> split(User paidBy, List<User> users, double amount) {
		// Validate inputs
		if (amount <= 0) {
			throw new InvalidAmountException("Amount must be positive for percentage split");
		}

		if (users == null || users.isEmpty()) {
			throw new InvalidAmountException("At least one user is required for splitting");
		}

		Map<User, Double> splitUserMapping = new HashMap<>();

		// TODO: This needs to be extended to include percentage mapping
		// In a real implementation, you would:
		// 1. Get percentage data from the Expense model or a separate configuration
		// 2. Example structure: Map<User, Double> percentages =
		// getPercentagesFromExpense(expense)
		// 3. Validate that percentages sum to 100%
		// 4. Calculate each user's share based on their percentage

		// For now, throw exception indicating this needs proper configuration
		throw new UnsupportedOperationException(
				"PercentageSplitStrategy requires percentage configuration. " +
						"Please extend the Expense model to include a Map<User, Double> percentages field " +
						"and update this method to retrieve and use those percentages. " +
						"Example: If User A gets 40%, User B gets 30%, User C gets 30%, " +
						"their shares of a ₹300 expense would be ₹120, ₹90, ₹90 respectively.");

		// FUTURE IMPLEMENTATION EXAMPLE:
		/*
		 * // Get percentages from expense (to be added to Expense model)
		 * Map<User, Double> percentages = expense.getPercentages();
		 * 
		 * // Validate percentages sum to 100
		 * double totalPercentage = percentages.values().stream()
		 * .mapToDouble(Double::doubleValue)
		 * .sum();
		 * 
		 * if (Math.abs(totalPercentage - 100.0) > 0.01) {
		 * throw new InvalidAmountException(
		 * "Percentages must sum to 100%, but got " + totalPercentage + "%"
		 * );
		 * }
		 * 
		 * // Calculate shares based on percentages
		 * for (User user : users) {
		 * double percentage = percentages.getOrDefault(user, 0.0);
		 * double userShare = (amount * percentage) / 100.0;
		 * 
		 * if (user.equals(paidBy)) {
		 * splitUserMapping.put(user, amount - userShare);
		 * } else {
		 * splitUserMapping.put(user, -userShare);
		 * }
		 * }
		 * 
		 * return splitUserMapping;
		 */
	}
}
