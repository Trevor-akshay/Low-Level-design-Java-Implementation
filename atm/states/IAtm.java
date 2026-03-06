package atm.states;

import atm.enums.ATMStates;
import atm.exceptions.FunctionNotAllowed;
import atm.exceptions.HardwareException;
import atm.manager.InventoryManager;
import atm.models.Card;

/**
 * Abstract base class for all ATM states (State design pattern).
 *
 * <p>
 * <b>Default-deny</b>: Every operation throws {@link FunctionNotAllowed} by
 * default.
 * Concrete states override only the operations that are permitted in that
 * state.
 *
 * <p>
 * State summary:
 * <ul>
 * <li>{@link Idle} – only {@link #insertCard()} is allowed</li>
 * <li>{@link CardInserted} – only {@link #validatePin} and {@link #ejectCard()}
 * are allowed</li>
 * <li>{@link Authenticated} – {@link #withdraw}, {@link #fetchBalance},
 * {@link #ejectCard()} allowed</li>
 * </ul>
 */
public abstract class IAtm {

	/**
	 * Validates the user's PIN against the account linked to the inserted card.
	 * Only permitted in {@link CardInserted} state.
	 *
	 * @throws FunctionNotAllowed in all other states
	 */
	public boolean validatePin(Card card, String pincode) throws FunctionNotAllowed {
		throw new FunctionNotAllowed("Please insert card..");
	};

	/**
	 * Returns the account balance for the authenticated card.
	 * Only permitted in {@link Authenticated} state.
	 *
	 * @throws FunctionNotAllowed in all other states
	 */
	public int fetchBalance(Card card) throws FunctionNotAllowed {
		throw new FunctionNotAllowed("Please insert card..");
	}

	/**
	 * Attempts to withdraw {@code requestAmount} from the ATM vault.
	 * Performs two checks: account balance ≥ request, vault cash ≥ request.
	 * Only permitted in {@link Authenticated} state.
	 *
	 * @throws FunctionNotAllowed in all other states
	 * @throws HardwareException  if the dispenser hardware fails
	 */
	public boolean withdraw(int requestAmount,
			Card card, InventoryManager inventoryManager)
			throws FunctionNotAllowed, HardwareException {
		throw new FunctionNotAllowed("Please insert card..");
	}

	/**
	 * Accepts a card into the machine.
	 * Only permitted in {@link Idle} state; if a card is already inserted this
	 * throws.
	 *
	 * @throws FunctionNotAllowed if a card is already present
	 */
	public void insertCard() throws FunctionNotAllowed {
		throw new FunctionNotAllowed("Card already inserted");
	}

	/**
	 * Ejects the card and resets the ATM to IDLE.
	 * Permitted in {@link CardInserted} and {@link Authenticated} states.
	 *
	 * @throws FunctionNotAllowed if no card is present
	 */
	public void ejectCard() throws FunctionNotAllowed {
		throw new FunctionNotAllowed("Please insert card..");
	}

	/** Returns the {@link ATMStates} constant that identifies this state. */
	public ATMStates getStatus() {
		return ATMStates.IDLE;
	}
}
