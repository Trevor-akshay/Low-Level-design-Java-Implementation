package splitwise.strategy;

import java.util.List;
import java.util.Map;

import splitwise.models.User;

/**
 * Interface for defining expense splitting algorithms.
 * Implements the Strategy design pattern to allow various ways of splitting
 * expenses.
 * 
 * The split() method calculates how much each user should pay and returns
 * balance changes for each participant.
 * 
 * Return Value Semantics:
 * - Positive value: User is owed money (or paid more than their share)
 * - Negative value: User owes money (paid less than their share)
 * - Zero: Balanced (paid exactly their share)
 * 
 * The sum of all balance changes should equal zero (meaning the expense is
 * fully covered).
 * 
 * Example Implementation (EqualSplitStrategy):
 * Alice pays ₹300 for 3 people (equal split)
 * 
 * Calculation:
 * - Share per person = 300 / 3 = ₹100
 * - Alice: paid ₹300, owes ₹100 = +200 (she's owed ₹200)
 * - Bob: paid ₹0, owes ₹100 = -100 (he owes ₹100)
 * - Charlie: paid ₹0, owes ₹100 = -100 (he owes ₹100)
 * 
 * Total: 200 + (-100) + (-100) = 0 ✓
 */
public interface SplitStrategy {

	/**
	 * Calculates how to split an expense among users.
	 * 
	 * @param paidBy The user who paid the full amount
	 * @param users  List of users who will share the expense
	 * @param amount Total expense amount to be split
	 * 
	 * @return Map of users to their balance change
	 *         - Positive: they are owed money
	 *         - Negative: they owe money
	 *         - Sum must equal zero
	 */
	public Map<User, Double> split(User paidBy, List<User> users, double amount);
}
