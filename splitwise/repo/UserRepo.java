package splitwise.repo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import splitwise.exceptions.InvalidUserException;
import splitwise.factory.UserFactory;
import splitwise.models.User;

/**
 * Repository for managing user data and balance tracking.
 * 
 * Balance Tracking Structure:
 * - userBalances: Net balance per user (positive = owed to them, negative =
 * they owe)
 * - userBalancesOwned: Detailed graph of who owes whom
 * Structure: Map<User, Map<User, Double>>
 * Example: userBalancesOwned[alice][bob] = 100 means alice owes bob ₹100
 * - users: Registry of all users by UUID
 * 
 * Example:
 * Alice pays ₹300 for dinner with Bob (2 people)
 * - alice owes herself: ₹150, so net owed BY alice = -150
 * - bob owes alice: ₹150
 * 
 * Data structures after expense:
 * userBalances: alice -> 150, bob -> -150
 * userBalancesOwned: alice -> {bob: 0}, bob -> {alice: 150}
 */
public class UserRepo {
	// Maintains detailed debt information: who owes whom and how much
	// Structure: Map<User, Map<User, Double>>
	// Example: userBalancesOwned.get(alice).get(bob) = 150 means alice owes bob
	// ₹150
	private final ConcurrentHashMap<User, Map<User, Double>> userBalancesOwned;

	// Maintains net balance per user
	// Positive = others owe them money, Negative = they owe others money
	private final ConcurrentHashMap<User, Double> userBalances;

	// Registry of all system users
	private final ConcurrentHashMap<UUID, User> users;

	/**
	 * Initializes the UserRepo with provided data structures.
	 * These are typically initialized as ConcurrentHashMaps for thread safety.
	 * 
	 * @param userBalancesOwned Map tracking individual debts
	 * @param userBalances      Map tracking net balances
	 * @param users             Map registry of users
	 */
	public UserRepo(ConcurrentHashMap<User, Map<User, Double>> userBalancesOwned,
			ConcurrentHashMap<User, Double> userBalances, ConcurrentHashMap<UUID, User> users) {
		this.userBalancesOwned = userBalancesOwned;
		this.userBalances = userBalances;
		this.users = users;
	}

	/**
	 * Creates and registers a new user in the system.
	 * 
	 * Example:
	 * userRepo.saveUser("Alice");
	 * // User with UUID is created and registered
	 * 
	 * @param name Display name for the new user
	 */
	public void saveUser(String name) {
		if (name == null || name.trim().isEmpty()) {
			throw new InvalidUserException("User name cannot be empty");
		}

		var user = UserFactory.createUser(name);
		users.put(user.getUserId(), user);

		// Initialize balance maps for new user
		userBalances.putIfAbsent(user, 0.0);
		userBalancesOwned.putIfAbsent(user, new ConcurrentHashMap<>());
	}

	/**
	 * Retrieves a user by their UUID.
	 * 
	 * @param userId UUID of the user to retrieve
	 * @return User object, or null if not found
	 */
	public User getUser(UUID userId) {
		return users.get(userId);
	}

	/**
	 * Updates balances after an expense split.
	 * 
	 * The balances parameter comes from split calculation and represents
	 * how much each user's balance should change.
	 * 
	 * Example:
	 * If Alice pays ₹300 for 3 people (equal split):
	 * Input balances map:
	 * - alice -> +200 (she paid 300, owes 100 herself)
	 * - bob -> -100 (owes alice ₹100)
	 * - charlie -> -100 (owes alice ₹100)
	 * 
	 * @param balances Map of user to balance change from this expense
	 */
	public void updateBalances(Map<User, Double> balances) {
		if (balances == null || balances.isEmpty()) {
			return;
		}

		for (var balanceEntry : balances.entrySet()) {
			var user = balanceEntry.getKey();
			var balanceAmount = balanceEntry.getValue();

			// Initialize user if not exists
			userBalancesOwned.putIfAbsent(user, new ConcurrentHashMap<>());
			userBalances.putIfAbsent(user, 0.0);

			// Update net balance
			userBalances.merge(user, balanceAmount, Double::sum);

			// Update detailed balance tracking
			// This is a simplified implementation - in production you'd need
			// more complex logic to determine specific creditors/debtors
			userBalancesOwned.get(user).merge(user, balanceAmount, Double::sum);
		}
	}

	/**
	 * Retrieves the net balance for a user.
	 * 
	 * Positive = others owe this user money
	 * Negative = this user owes others money
	 * 
	 * @param user User to get balance for
	 * @return Net balance amount, or 0.0 if user not found
	 */
	public double getBalanceByUser(User user) {
		return userBalances.getOrDefault(user, 0.0);
	}

	/**
	 * Retrieves detailed debts for a specific user.
	 * Shows exactly who this user owes money to and how much.
	 * 
	 * Example:
	 * User alice = ...
	 * Map<User, Double> debts = userRepo.getDebtsForUser(alice);
	 * // Results:
	 * // bob -> 100 (alice owes bob ₹100)
	 * // charlie -> 50 (alice owes charlie ₹50)
	 * 
	 * @param user User to get debts for
	 * @return Map of creditors to debt amounts
	 */
	public Map<User, Double> getDebtsForUser(User user) {
		return new HashMap<>(userBalancesOwned.getOrDefault(user, new HashMap<>()));
	}

	/**
	 * Records a payment/settlement between two users.
	 * Reduces the debt of payer to payee by the settlement amount.
	 * 
	 * Example:
	 * settleDebt(bob, alice, 100)
	 * // Bob's debt to Alice reduces by ₹100
	 * 
	 * @param payer  User who is paying
	 * @param payee  User who is receiving payment
	 * @param amount Amount being settled
	 */
	public void settleDebt(User payer, User payee, double amount) {
		if (amount <= 0) {
			throw new InvalidUserException("Settlement amount must be positive");
		}

		userBalancesOwned.putIfAbsent(payer, new ConcurrentHashMap<>());

		// Reduce payer's debt to payee
		userBalancesOwned.get(payer).merge(payee, -amount, Double::sum);

		// Update net balances
		userBalances.merge(payer, amount, Double::sum);
		userBalances.merge(payee, -amount, Double::sum);
	}

	/**
	 * Calculates the minimum number of settlements needed to clear all debts.
	 * Uses a greedy algorithm to simplify payment chains.
	 * 
	 * Example:
	 * Network:
	 * - Alice owes Bob ₹100
	 * - Bob owes Charlie ₹100
	 * 
	 * Optimized settlements:
	 * - Alice pays Charlie ₹100 (instead of Alice->Bob->Charlie)
	 * 
	 * @return List of settlement transactions needed
	 */
	public List<Map<String, Object>> getMinimumSettlements() {
		List<Map<String, Object>> settlements = new ArrayList<>();

		// Create a working copy of balances
		Map<User, Double> balances = new HashMap<>(userBalances);

		// Greedy algorithm: match creditors with debtors
		while (true) {
			// Find user with maximum credit (positive balance)
			User maxCreditor = null;
			double maxCredit = 0;

			for (var entry : balances.entrySet()) {
				if (entry.getValue() > maxCredit) {
					maxCredit = entry.getValue();
					maxCreditor = entry.getKey();
				}
			}

			// Find user with maximum debt (negative balance)
			User maxDebtor = null;
			double maxDebt = 0;

			for (var entry : balances.entrySet()) {
				if (entry.getValue() < -maxDebt) {
					maxDebt = -entry.getValue();
					maxDebtor = entry.getKey();
				}
			}

			// If no one owes or is owed, we're done
			if (maxCreditor == null || maxDebtor == null || maxCredit == 0 || maxDebt == 0) {
				break;
			}

			// Settle the minimum amount
			double settleAmount = Math.min(maxCredit, maxDebt);

			Map<String, Object> settlement = new HashMap<>();
			settlement.put("payer", maxDebtor);
			settlement.put("payee", maxCreditor);
			settlement.put("amount", settleAmount);
			settlements.add(settlement);

			// Update balances
			balances.put(maxCreditor, balances.get(maxCreditor) - settleAmount);
			balances.put(maxDebtor, balances.get(maxDebtor) + settleAmount);
		}

		return settlements;
	}

	/**
	 * Retrieves all registered users in the system.
	 * 
	 * @return Map of UUID to User
	 */
	public Map<UUID, User> getUsers() {
		return new HashMap<>(users);
	}

}
