package splitwise;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import splitwise.enums.SplitType;
import splitwise.exceptions.InvalidAmountException;
import splitwise.exceptions.InvalidUserException;
import splitwise.exceptions.InsufficientBalanceException;
import splitwise.models.Expense;
import splitwise.models.Group;
import splitwise.models.User;
import splitwise.repo.ExpenseRepo;
import splitwise.repo.GroupRepo;
import splitwise.repo.UserRepo;
import splitwise.strategy.SplitStrategy;

/**
 * Main service class for managing Splitwise operations.
 * Handles expense tracking, settlements, group management, and balance
 * calculations.
 * 
 * Thread-Safe: This service uses ReentrantReadWriteLock to ensure thread-safe
 * operations
 * for concurrent users.
 * 
 * Key Features:
 * 1. Record expenses and split them among users
 * 2. Track individual balances and who owes whom
 * 3. Settle debts between users
 * 4. Create and manage groups
 * 5. Query expenses by group or user
 * 
 * Example Usage:
 * // Create users
 * User alice = new User(UUID.randomUUID(), "Alice");
 * User bob = new User(UUID.randomUUID(), "Bob");
 * 
 * // Initialize repositories and service
 * SplitwiseService service = new SplitwiseService(userRepo, expenseRepo,
 * groupRepo,
 * new EqualSplitStrategy());
 * 
 * // Record an expense
 * service.recordExpense(alice, List.of(alice, bob), 500, "Pizza",
 * SplitType.EQUAL);
 * 
 * // Check balance
 * Double aliceBalance = service.getBalanceByUser(alice); // Should be positive
 * (she paid more)
 * 
 * // Settle payment
 * service.settlePayment(bob, alice, 250); // Bob pays Alice ₹250
 */
public class SplitwiseService {
	private final UserRepo userRepo;
	private final ExpenseRepo expenseRepo;
	private final GroupRepo groupRepo;
	private SplitStrategy splitStrategy;

	private final ReentrantReadWriteLock locks;

	/**
	 * Initializes the Splitwise service with necessary repositories and split
	 * strategy.
	 * 
	 * @param userRepo      User repository for managing user balances
	 * @param expenseRepo   Expense repository for storing expenses
	 * @param groupRepo     Group repository for managing groups
	 * @param splitStrategy Strategy for splitting expenses (EQUAL, PERCENTAGE, etc)
	 */
	public SplitwiseService(UserRepo userRepo, ExpenseRepo expenseRepo, GroupRepo groupRepo,
			SplitStrategy splitStrategy) {
		this.userRepo = userRepo;
		this.expenseRepo = expenseRepo;
		this.splitStrategy = splitStrategy;
		this.groupRepo = groupRepo;
		this.locks = new ReentrantReadWriteLock();
	}

	/**
	 * Records an expense that will be split among specified users within a group.
	 * 
	 * Example:
	 * // Alice pays ₹600 for hotel for her, Bob, and Charlie (3 people)
	 * service.recordExpense(groupId, alice, List.of(alice, bob, charlie), 600,
	 * "Hotel", SplitType.EQUAL);
	 * // Each person owes ₹200
	 * 
	 * @param groupId     UUID of the group this expense belongs to
	 * @param paidBy      User who paid the full amount
	 * @param users       List of users who will share the expense (must include
	 *                    paidBy)
	 * @param amount      Total expense amount (must be positive)
	 * @param description Description of the expense
	 * @param splitType   How to split the expense (EQUAL, PERCENTAGE)
	 * 
	 * @throws InvalidAmountException if amount is negative or zero
	 * @throws InvalidUserException   if users list is empty or paidBy is not in
	 *                                users list
	 */
	public void recordExpense(UUID groupId, User paidBy, List<User> users, double amount, String description,
			SplitType splitType) {
		// Validate inputs
		validateExpenseInputs(paidBy, users, amount);

		locks.writeLock().lock();

		try {
			var expense = expenseRepo.saveExpense(paidBy, amount, description, users, splitType);
			groupRepo.saveGroup(description, expense, users);

			var balances = splitStrategy.split(paidBy, users, amount);

			userRepo.updateBalances(balances);
		} finally {
			locks.writeLock().unlock();
		}
	}

	/**
	 * Records an expense without associating it to a specific group.
	 * Useful for personal expenses or informal splits.
	 * 
	 * Example:
	 * // Alice and Bob go for lunch, Alice pays ₹400
	 * service.recordExpense(alice, List.of(alice, bob), 400, "Lunch",
	 * SplitType.EQUAL);
	 * // Expense is split 50-50 without being associated to a group
	 * 
	 * @param paidBy      User who paid the full amount
	 * @param users       List of users who will share the expense (must include
	 *                    paidBy)
	 * @param amount      Total expense amount (must be positive)
	 * @param description Description of the expense
	 * @param splitType   How to split the expense (EQUAL, PERCENTAGE)
	 * 
	 * @throws InvalidAmountException if amount is negative or zero
	 * @throws InvalidUserException   if users list is empty or paidBy is not in
	 *                                users list
	 */
	public void recordExpense(User paidBy, List<User> users, double amount, String description,
			SplitType splitType) {
		// Validate inputs
		validateExpenseInputs(paidBy, users, amount);

		locks.writeLock().lock();

		try {
			expenseRepo.saveExpense(paidBy, amount, description, users, splitType);

			var balances = splitStrategy.split(paidBy, users, amount);

			userRepo.updateBalances(balances);
		} finally {
			locks.writeLock().unlock();
		}
	}

	/**
	 * Retrieves the net balance for a user.
	 * Positive balance = others owe this user money
	 * Negative balance = this user owes others money
	 * 
	 * Example:
	 * If Alice's balance is +500, she is owed ₹500 total (across all users)
	 * If Bob's balance is -300, he owes ₹300 total
	 * 
	 * @param user The user to check balance for
	 * @return Net balance amount
	 */
	public Double getBalanceByUser(User user) {
		locks.readLock().lock();
		try {
			return userRepo.getBalanceByUser(user);
		} finally {
			locks.readLock().unlock();
		}
	}

	/**
	 * Retrieves the detailed breakdown of debts for a specific user.
	 * Shows exactly who this user owes money to and how much.
	 * 
	 * Example:
	 * User alice = ...
	 * Map<User, Double> debts = service.getDebtOf(alice);
	 * // Results:
	 * // bob -> 100 (alice owes bob ₹100)
	 * // charlie -> 50 (alice owes charlie ₹50)
	 * // alice -> 0 (alice doesn't owe herself)
	 * 
	 * @param user The user to get debts for
	 * @return Map of creditors to debt amounts (key = who is owed money, value =
	 *         amount owed)
	 */
	public Map<User, Double> getDebtOf(User user) {
		locks.readLock().lock();
		try {
			return userRepo.getDebtsForUser(user);
		} finally {
			locks.readLock().unlock();
		}
	}

	/**
	 * Settles a payment between two users.
	 * Reduces or eliminates the debt between payer and payee.
	 * 
	 * Example:
	 * // Bob owes Alice ₹250
	 * service.settlePayment(bob, alice, 250);
	 * // Now Bob no longer owes Alice anything
	 * 
	 * @param payer  User who is paying
	 * @param payee  User who is receiving the payment
	 * @param amount Amount to settle (must be positive and not exceed debt)
	 * 
	 * @throws InvalidAmountException       if amount is negative or zero
	 * @throws InsufficientBalanceException if payer doesn't owe that much to payee
	 * @throws InvalidUserException         if either user not found
	 */
	public void settlePayment(User payer, User payee, double amount) {
		if (amount <= 0) {
			throw new InvalidAmountException("Settlement amount must be positive, got: " + amount);
		}

		if (!payer.equals(payee)) {
			throw new InvalidUserException("Payer and payee must be different users");
		}

		locks.writeLock().lock();
		try {
			// Get what payer owes to payee
			Map<User, Double> payerDebts = userRepo.getDebtsForUser(payer);
			Double owed = payerDebts.getOrDefault(payee, 0.0);

			if (owed < amount) {
				throw new InsufficientBalanceException(
						String.format("Payer owes ₹%.2f to payee, but trying to settle ₹%.2f", owed, amount));
			}

			// Update balances
			userRepo.settleDebt(payer, payee, amount);
		} finally {
			locks.writeLock().unlock();
		}
	}

	/**
	 * Calculates the minimum number of settlements needed to clear all debts.
	 * Uses a greedy algorithm to optimize payment transactions.
	 * 
	 * Example:
	 * If Alice owes Bob ₹100 and Bob owes Charlie ₹100,
	 * Instead of: Alice->Bob(₹100) and Bob->Charlie(₹100)
	 * Simplify to: Alice->Charlie(₹100)
	 * 
	 * @return List of settlements, each containing [payer, payee, amount]
	 */
	public List<Map<String, Object>> getMinimumSettlements() {
		locks.readLock().lock();
		try {
			return userRepo.getMinimumSettlements();
		} finally {
			locks.readLock().unlock();
		}
	}

	/**
	 * Creates a new group for tracking group expenses.
	 * 
	 * Example:
	 * UUID groupId = service.createGroup("European Trip");
	 * // Can now use groupId in recordExpense() for group-specific expenses
	 * 
	 * @param groupName Name of the group
	 * @return UUID of the newly created group
	 */
	public UUID createGroup(String groupName) {
		if (groupName == null || groupName.trim().isEmpty()) {
			throw new InvalidUserException("Group name cannot be empty");
		}

		locks.writeLock().lock();
		try {
			Group group = new Group(UUID.randomUUID(), groupName);
			groupRepo.saveGroup(group);
			return group.getGroupId();
		} finally {
			locks.writeLock().unlock();
		}
	}

	/**
	 * Removes a group and all its associated expenses.
	 * 
	 * @param groupId UUID of the group to delete
	 * @return true if deleted successfully, false if group not found
	 */
	public boolean deleteGroup(UUID groupId) {
		locks.writeLock().lock();
		try {
			return groupRepo.deleteGroup(groupId);
		} finally {
			locks.writeLock().unlock();
		}
	}

	/**
	 * Removes a user from a group.
	 * This does not settle their debts; it only removes them from future expense
	 * sharing.
	 * 
	 * Note: Existing expenses they were part of remain recorded.
	 * 
	 * @param groupId UUID of the group
	 * @param user    User to remove from the group
	 * @return true if user was removed, false if user not in group or group not
	 *         found
	 */
	public boolean removeUserFromGroup(UUID groupId, User user) {
		locks.writeLock().lock();
		try {
			return groupRepo.removeUserFromGroup(groupId, user);
		} finally {
			locks.writeLock().unlock();
		}
	}

	/**
	 * Retrieves all expenses for a specific group.
	 * 
	 * Example:
	 * UUID tripGroupId = ...
	 * List<Expense> tripExpenses = service.getExpensesByGroup(tripGroupId);
	 * // Returns all expenses recorded for this group
	 * 
	 * @param groupId UUID of the group
	 * @return List of expenses in the group, or empty list if group not found
	 */
	public List<Expense> getExpensesByGroup(UUID groupId) {
		locks.readLock().lock();
		try {
			Group group = groupRepo.getGroup(groupId);
			return group != null ? group.getExpenses() : List.of();
		} finally {
			locks.readLock().unlock();
		}
	}

	/**
	 * Retrieves all expenses paid by or involving a specific user.
	 * 
	 * Example:
	 * User alice = ...
	 * List<Expense> aliceExpenses = service.getExpensesByUser(alice);
	 * // Returns all expenses where alice is involved (either paid or share in)
	 * 
	 * @param user The user to get expenses for
	 * @return List of expenses involving this user
	 */
	public List<Expense> getExpensesByUser(User user) {
		locks.readLock().lock();
		try {
			return expenseRepo.getExpenses().values().stream()
					.filter(expense -> expense.getPaidBy().equals(user) ||
							expense.getUsers().contains(user))
					.collect(Collectors.toList());
		} finally {
			locks.readLock().unlock();
		}
	}

	/**
	 * Modifies an existing expense.
	 * Updates the expense details and recalculates balances.
	 * 
	 * Note: This is a simplified implementation. A real system should:
	 * 1. Reverse the original split
	 * 2. Apply the new split
	 * 3. Handle complex recalculations
	 * 
	 * @param expenseId      UUID of the expense to modify
	 * @param newAmount      New amount (pass null to keep same)
	 * @param newDescription New description (pass null to keep same)
	 * @return true if modification successful, false if expense not found
	 */
	public boolean modifyExpense(UUID expenseId, Double newAmount, String newDescription) {
		locks.writeLock().lock();
		try {
			Expense expense = expenseRepo.getExpense(expenseId);
			if (expense == null) {
				return false;
			}

			// Validate new amount if provided
			if (newAmount != null && newAmount <= 0) {
				throw new InvalidAmountException("New amount must be positive");
			}

			// Update expense details
			if (newAmount != null) {
				expense.setAmount(newAmount);
			}
			if (newDescription != null && !newDescription.trim().isEmpty()) {
				expense.setDescription(newDescription);
			}

			return true;
		} finally {
			locks.writeLock().unlock();
		}
	}

	/**
	 * Changes the split strategy for future expenses.
	 * Does not affect already recorded expenses.
	 * 
	 * Example:
	 * service.setSplitStrategy(new EqualSplitStrategy());
	 * // From now on, all new expenses will use equal split
	 * 
	 * @param splitStrategy The new split strategy to use
	 */
	public void setSplitStrategy(SplitStrategy splitStrategy) {
		if (splitStrategy == null) {
			throw new InvalidUserException("Split strategy cannot be null");
		}
		this.splitStrategy = splitStrategy;
	}

	// ==================== PRIVATE HELPER METHODS ====================

	/**
	 * Validates expense input parameters before recording.
	 * 
	 * @param paidBy User who paid
	 * @param users  List of users in expense
	 * @param amount Expense amount
	 * 
	 * @throws InvalidAmountException if amount is invalid
	 * @throws InvalidUserException   if user list is invalid
	 */
	private void validateExpenseInputs(User paidBy, List<User> users, double amount) {
		// Validate amount
		if (amount <= 0) {
			throw new InvalidAmountException(
					String.format("Expense amount must be positive, got: ₹%.2f", amount));
		}

		// Validate users list
		if (users == null || users.isEmpty()) {
			throw new InvalidUserException("Expense must have at least one user");
		}

		// Validate paidBy is in users list
		if (!users.contains(paidBy)) {
			throw new InvalidUserException(
					"Payer must be included in the list of users sharing the expense");
		}

		// Validate all users are non-null
		for (User user : users) {
			if (user == null) {
				throw new InvalidUserException("Users list cannot contain null values");
			}
		}
	}
}
