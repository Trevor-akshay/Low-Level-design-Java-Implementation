package vending_machine;

import vending_machine.enums.MachineStates;
import vending_machine.exceptions.ActionNotAllowedException;
import vending_machine.exceptions.HardwareFailureException;
import vending_machine.exceptions.ItemOutOfStockException;
import vending_machine.factory.StateFactory;
import vending_machine.manager.InventoryManager;
import vending_machine.models.Aisle;
import vending_machine.models.Item;
import vending_machine.payment.IPaymentStrategy;
import vending_machine.state.State;

/**
 * VendingMachine — Context class in the State design pattern.
 *
 * <p>
 * Every user-facing operation is delegated to the current {@link State} object.
 * Each state permits only a subset of operations; unsupported operations throw
 * {@link ActionNotAllowedException}.
 *
 * <p>
 * <b>State lifecycle (happy path)</b>:
 * 
 * <pre>
 *   IDLE ──initializeSelection()──► SELECTION ──selectAisle()──► PAYMENT ──pay()──► DISPENSE
 *    ▲                                                                                   │
 *    └──────────────────────────── dispense() — returns to IDLE ──────────────────────── ┘
 * </pre>
 *
 * <p>
 * <b>Payment strategy</b> is pluggable — swap at runtime via
 * {@link #setPaymentStrategy}
 * (e.g., switch from cash to credit card mode).
 */
public class VendingMachine {
	/** Current behavioural state — transitions on every successful user action. */
	private State currentState;
	private final InventoryManager inventoryManager;
	/**
	 * Currently active payment method — injectable via {@link #setPaymentStrategy}.
	 */
	private IPaymentStrategy paymentStrategy;
	/**
	 * The aisle (product slot) selected in the current session; cleared on IDLE
	 * reset.
	 */
	private Aisle currentAisle;

	public VendingMachine(InventoryManager inventoryManager, IPaymentStrategy paymentStrategy) {
		this.inventoryManager = inventoryManager;
		this.paymentStrategy = paymentStrategy;
		this.currentState = StateFactory.createState(MachineStates.IDLE);
		setCurrentAisle(null);
	}

	/**
	 * Unlocks the selection panel, transitioning from IDLE → SELECTION.
	 *
	 * @throws ActionNotAllowedException if the machine is not in IDLE state
	 */
	public void intializeSelection() throws ActionNotAllowedException {
		currentState.intializeSelection();
		setCurrentState(MachineStates.SELECTION);
	}

	/**
	 * User selects an aisle/product; transitions SELECTION → PAYMENT.
	 *
	 * @param aisleId the identifier of the aisle to select
	 * @throws ActionNotAllowedException if not in SELECTION state
	 * @throws ItemOutOfStockException   if the selected aisle has no inventory
	 * @throws HardwareFailureException  if communication with the inventory system
	 *                                   fails
	 */
	public void selectAisle(String aisleId) throws ActionNotAllowedException, ItemOutOfStockException,
			HardwareFailureException {
		var aisle = currentState.selectAisle(inventoryManager, aisleId);
		setCurrentAisle(aisle);
		setCurrentState(MachineStates.PAYMENT);
	}

	/**
	 * Processes payment for the selected aisle's price; transitions PAYMENT →
	 * DISPENSE on success.
	 *
	 * @return {@code true} if payment succeeded; {@code false} if payment was
	 *         declined
	 * @throws ActionNotAllowedException if not in PAYMENT state
	 * @throws HardwareFailureException  if the payment hardware fails
	 */
	public boolean pay() throws ActionNotAllowedException, HardwareFailureException {
		if (currentAisle == null)
			return false; // safety guard: no aisle selected

		var result = currentState.pay(paymentStrategy, currentAisle.getPrice());
		if (result)
			setCurrentState(MachineStates.DISPENSE); // payment ok — allow dispense

		return result;
	}

	/**
	 * Dispenses the item from the selected aisle; transitions DISPENSE → IDLE on
	 * success.
	 *
	 * @param aisleId the aisle from which to dispense the item
	 * @return the dispensed {@link Item}
	 * @throws HardwareFailureException  if the dispenser motor or mechanism fails
	 * @throws IllegalArgumentException  if the aisleId is invalid
	 * @throws ActionNotAllowedException if not in DISPENSE state
	 */
	public Item dispense(String aisleId)
			throws HardwareFailureException, IllegalArgumentException, ActionNotAllowedException {
		var item = currentState.dispense(inventoryManager, aisleId);

		if (item != null)
			setCurrentState(MachineStates.IDLE); // item dispensed — reset machine
		return item;
	}

	/** Transitions the machine to a new state using the {@link StateFactory}. */
	public void setCurrentState(MachineStates state) {
		currentState = StateFactory.createState(state);
	}

	public MachineStates getCurrentState() {
		return currentState.getMachineState();
	}

	public InventoryManager getInventoryManager() {
		return inventoryManager;
	}

	public IPaymentStrategy getPaymentStrategy() {
		return paymentStrategy;
	}

	/**
	 * Swaps the payment strategy at runtime (e.g., switch from cash to credit
	 * card).
	 */
	public void setPaymentStrategy(IPaymentStrategy paymentStrategy) {
		this.paymentStrategy = paymentStrategy;
	}

	public void setCurrentAisle(Aisle aisle) {
		this.currentAisle = aisle;
	}
}
