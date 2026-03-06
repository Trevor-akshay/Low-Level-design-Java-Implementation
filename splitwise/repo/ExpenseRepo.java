package splitwise.repo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import splitwise.enums.SplitType;
import splitwise.exceptions.InvalidUserException;
import splitwise.factory.ExpenseFactory;
import splitwise.models.Expense;
import splitwise.models.User;

/**
 * Repository for storing and retrieving expenses.
 * 
 * Manages the persistence of expense records and provides
 * access to historical expense data.
 * 
 * Example:
 * Expense dinner = expenseRepo.getExpense(expenseId);
 * // Retrieves an expense by its UUID
 */
public class ExpenseRepo {
	private final Map<UUID, Expense> expenses;

	/**
	 * Initializes the ExpenseRepo with an expense storage map.
	 * 
	 * @param expenses Map to store expenses, typically a ConcurrentHashMap for
	 *                 thread-safety
	 */
	public ExpenseRepo(Map<UUID, Expense> expenses) {
		this.expenses = expenses;
	}

	/**
	 * Creates and saves a new expense.
	 * 
	 * Example:
	 * Expense dinner = expenseRepo.saveExpense(alice, 300.0, "Dinner",
	 * List.of(alice, bob, charlie),
	 * SplitType.EQUAL);
	 * // Creates ₹300 dinner expense for 3 people split equally
	 * 
	 * @param paidBy      User who paid for the expense
	 * @param amount      Total expense amount (should be positive)
	 * @param description Description of the expense
	 * @param users       List of users involved in the expense
	 * @param splitType   How the expense should be split
	 * @return The newly created and saved Expense
	 * 
	 * @throws InvalidUserException if paidBy or users is null
	 */
	public Expense saveExpense(User paidBy, double amount, String description, List<User> users, SplitType splitType) {
		if (paidBy == null) {
			throw new InvalidUserException("Payer cannot be null");
		}
		if (users == null || users.isEmpty()) {
			throw new InvalidUserException("Users list cannot be empty");
		}

		var expense = ExpenseFactory.createExpense(paidBy, amount, description, users, splitType);
		expenses.put(expense.getExpenseId(), expense);

		return expense;
	}

	/**
	 * Retrieves an expense by its UUID.
	 * 
	 * Example:
	 * Expense expense = expenseRepo.getExpense(expenseId);
	 * // Returns the expense or null if not found
	 * 
	 * @param expenseId UUID of the expense to retrieve
	 * @return Expense object, or null if not found
	 */
	public Expense getExpense(UUID expenseId) {
		return expenses.get(expenseId);
	}

	/**
	 * Retrieves all expenses stored in the repository.
	 * 
	 * @return Map of UUID to Expense containing all expenses
	 */
	public Map<UUID, Expense> getExpenses() {
		return new HashMap<>(expenses);
	}

}
