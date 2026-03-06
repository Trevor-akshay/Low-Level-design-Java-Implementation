package atm.manager;

import java.util.HashMap;
import java.util.Map;

import atm.enums.CashDenominators;
import atm.models.Account;

/**
 * Manages the ATM's physical cash vault.
 *
 * <p>
 * The vault is initialised with 10 notes of every denomination. When a
 * withdrawal
 * is requested the algorithm works in two phases:
 * <ol>
 * <li><b>Dry-run ({@link #withdraw})</b>: greedy note selection — iterate
 * denominations
 * from highest to lowest, use as many notes as possible for each. Returns the
 * note-breakdown map without modifying vault state. Returns {@code null} if the
 * exact amount cannot be satisfied.</li>
 * <li><b>Commit ({@link #enforceInvariants})</b>: called after the dispenser
 * hardware
 * succeeds — decrements the actual denomination counts and total value.</li>
 * </ol>
 * The two-phase approach means vault state only changes after a successful
 * physical
 * dispense, avoiding phantom balance deductions on hardware failure.
 */
public class InventoryManager {
	/**
	 * Total monetary value currently in the vault (sum of all notes ×
	 * denomination).
	 */
	private int totalValue;
	/** Count of notes per denomination currently in the vault. */
	private final Map<CashDenominators, Integer> denominationCounts;

	/**
	 * Initialises the vault with 10 notes of every available denomination.
	 * {@code totalValue} is computed as the sum of (denomination × 10).
	 */
	public InventoryManager() {
		this.denominationCounts = new HashMap<>();
		for (var denominations : CashDenominators.values()) {
			totalValue += denominations.getValue() * 10;
			denominationCounts.put(denominations, 10);
		}
	}

	/**
	 * Greedy dry-run withdrawal: determines which notes to dispense.
	 *
	 * <p>
	 * Iterates denominations from highest to lowest value. For each denomination
	 * it uses as many notes as possible (up to the available count) to reduce the
	 * remaining amount.
	 *
	 * @param requestAmount amount the user wants to withdraw
	 * @param account       the account making the request (checked against its
	 *                      balance)
	 * @return a map of denomination → count to dispense, or {@code null} if:
	 *         <ul>
	 *         <li>account balance &lt; requestAmount</li>
	 *         <li>vault cash &lt; requestAmount</li>
	 *         <li>exact amount cannot be assembled from available
	 *         denominations</li>
	 *         </ul>
	 */
	public Map<CashDenominators, Integer> withdraw(int requestAmount, Account account) {
		var accountBalance = account.getBalance();
		if (accountBalance < requestAmount)
			return null; // insufficient account balance
		if (totalValue < requestAmount)
			return null; // vault doesn't have enough cash

		int calculatedAmount = requestAmount;
		Map<CashDenominators, Integer> map = new HashMap<>();
		for (var denomination : CashDenominators.values()) {
			var cashValue = denomination.getValue();
			var counts = denominationCounts.get(denomination);
			if (cashValue > calculatedAmount)
				continue; // this note is too large for the remaining amount

			int requiredCount = calculatedAmount / cashValue;
			if (counts >= requiredCount) {
				// We have enough notes of this denomination to cover the remaining amount.
				calculatedAmount -= cashValue * requiredCount;
				map.put(denomination, requiredCount);
			} else {
				// Use all available notes of this denomination, then continue.
				calculatedAmount -= cashValue * counts;
				map.put(denomination, counts);
			}

			if (calculatedAmount == 0)
				break; // exact amount satisfied
		}

		// Return the note plan only if we could assemble the exact amount.
		return calculatedAmount == 0 ? map : null;
	}

	/**
	 * Commits the withdrawal by updating vault state after a successful physical
	 * dispense.
	 *
	 * <p>
	 * Decrements denomination counts and reduces the total vault value by
	 * {@code amount}.
	 * This is called <em>after</em> {@link atm.hardware.Dispenser#dispenseCash()}
	 * succeeds
	 * to ensure vault state reflects only cash that has actually left the machine.
	 *
	 * @param account the account (balance decrement is handled by the caller
	 *                separately)
	 * @param map     the note-breakdown returned by {@link #withdraw}
	 * @param amount  the total amount being withdrawn
	 */
	public void enforceInvariants(Account account, Map<CashDenominators, Integer> map, int amount) {
		for (var denomination : map.entrySet()) {
			var cashDenominator = denomination.getKey();
			var count = denomination.getValue();
			// Decrement available note count for this denomination.
			denominationCounts.merge(cashDenominator, -count, Integer::sum);
		}

		decrementTotalValue(amount);
	}

	/**
	 * Reduces the total vault value by {@code amount}, guarded against going
	 * negative.
	 */
	public void decrementTotalValue(int amount) {
		if (amount <= totalValue)
			setTotalValue(totalValue - amount);
	}

	public void setTotalValue(int totalValue) {
		this.totalValue = totalValue;
	}

}
