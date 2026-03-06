package atm;

import atm.enums.ATMStates;
import atm.exceptions.FunctionNotAllowed;
import atm.exceptions.HardwareException;
import atm.factory.ATMStateFactory;
import atm.manager.InventoryManager;
import atm.models.Card;
import atm.states.IAtm;

/**
 * ATM — Context class in the State design pattern.
 *
 * <p>
 * All user-facing operations are forwarded to the current {@link IAtm} state
 * object.
 * Each state allows only a subset of operations; calling a disallowed operation
 * throws
 * {@link FunctionNotAllowed}.
 *
 * <p>
 * State lifecycle:
 * 
 * <pre>
 *   IDLE ──insertCard()──► CARD_INSERTED ──validatePin()──► AUTHENTICATED
 *                                        ↑                          │
 *                          ejectCard() ──┘◄──────── ejectCard() ────┘
 * </pre>
 */
public class Atm {
	private final InventoryManager inventoryManager;
	/** Current behavioural state — swapped on every state transition. */
	private IAtm atmState;
	/** The card currently inserted into the machine; null when idle. */
	private Card currentCard;

	/**
	 * Creates an ATM starting in the {@link ATMStates#IDLE} state and
	 * initialises the cash vault via {@link InventoryManager}.
	 */
	public Atm() {
		this.atmState = ATMStateFactory.createState(ATMStates.IDLE);
		this.inventoryManager = new InventoryManager();
	}

	/**
	 * Validates the PIN for the currently inserted card.
	 * On success, transitions the ATM to {@link ATMStates#AUTHENTICATED}.
	 *
	 * @param pinCode PIN entered by the user
	 * @return {@code true} if the PIN is correct; {@code false} otherwise
	 * @throws FunctionNotAllowed if no card is inserted (state is IDLE)
	 */
	public boolean validatePin(String pinCode) throws FunctionNotAllowed {
		var result = this.atmState.validatePin(currentCard, pinCode);

		if (result) {
			// PIN is correct — advance to AUTHENTICATED so the user can transact.
			var state = ATMStateFactory.createState(ATMStates.AUTHENTICATED);
			setState(state);
		}

		return result;
	}

	/**
	 * Withdraws cash from the ATM vault.
	 * Delegates to {@link InventoryManager} for greedy note selection and
	 * uses {@link atm.hardware.Dispenser} to physically dispense the cash.
	 *
	 * @param requestAmount amount to withdraw (must be expressible as available
	 *                      denominations)
	 * @return {@code true} on success; {@code false} if the vault cannot satisfy
	 *         the request
	 * @throws FunctionNotAllowed if the ATM is not in AUTHENTICATED state
	 * @throws HardwareException  if the dispenser hardware fails
	 */
	public boolean withdraw(int requestAmount) throws FunctionNotAllowed, HardwareException {
		return this.atmState.withdraw(requestAmount, currentCard, inventoryManager);
	}

	/**
	 * Accepts a card and transitions to {@link ATMStates#CARD_INSERTED}.
	 * Only allowed when the ATM is in IDLE state.
	 *
	 * @param card the card being inserted
	 * @throws FunctionNotAllowed if a card is already inserted
	 */
	public void insertCard(Card card) throws FunctionNotAllowed {
		atmState.insertCard(); // guard: throws if not IDLE
		setCard(card);
		setState(ATMStateFactory.createState(ATMStates.CARD_INSERTED));
	}

	/**
	 * Returns the card to the user and resets the ATM to {@link ATMStates#IDLE}.
	 * Allowed in CARD_INSERTED and AUTHENTICATED states.
	 *
	 * @throws FunctionNotAllowed if no card is present (IDLE state)
	 */
	public void ejectCard() throws FunctionNotAllowed {
		atmState.ejectCard(); // guard: throws if no card present
		setCard(null);
		setState(ATMStateFactory.createState(ATMStates.IDLE));
	}

	/**
	 * Returns the account balance for the currently inserted and authenticated
	 * card.
	 *
	 * @return current account balance
	 * @throws FunctionNotAllowed if not in AUTHENTICATED state
	 */
	public int fetchBalance() throws FunctionNotAllowed {
		return this.atmState.fetchBalance(currentCard);
	}

	/** Updates the currently held card reference. */
	public void setCard(Card card) {
		this.currentCard = card;
	}

	/**
	 * Replaces the active state — called after every successful state transition.
	 */
	public void setState(IAtm state) {
		this.atmState = state;
	}
}
