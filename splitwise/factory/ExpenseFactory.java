package splitwise.factory;

import java.util.List;
import java.util.UUID;

import splitwise.enums.SplitType;
import splitwise.models.Expense;
import splitwise.models.User;

/**
 * Factory for creating Expense instances.
 * Generates unique UUIDs for each new expense and captures creation details.
 * 
 * Example:
 * Expense dinner = ExpenseFactory.createExpense(alice, 300.0, "Dinner",
 * List.of(alice, bob, charlie),
 * SplitType.EQUAL);
 * // Creates a ₹300 dinner expense split equally among 3 people
 */
public class ExpenseFactory {

	/**
	 * Creates a new Expense with a randomly generated UUID.
	 * 
	 * The expense automatically records the current date as creation date.
	 * 
	 * @param paidBy      User who paid the full amount
	 * @param amount      Total expense amount
	 * @param description Description of what the expense is for
	 * @param users       List of users who will share this expense
	 * @param splitType   How the expense should be split (EQUAL, PERCENTAGE, etc)
	 * @return A new Expense instance with unique UUID
	 */
	public static Expense createExpense(User paidBy, double amount, String description, List<User> users,
			SplitType splitType) {
		UUID expenseId = UUID.randomUUID();

		return new Expense(expenseId, paidBy, amount, description, null, users, splitType);
	}
}
