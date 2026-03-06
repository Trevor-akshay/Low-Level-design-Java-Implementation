package atm.states;

import atm.enums.ATMStates;
import atm.exceptions.FunctionNotAllowed;
import atm.exceptions.HardwareException;
import atm.hardware.Dispenser;
import atm.manager.InventoryManager;
import atm.models.Card;

public class Authenticated extends IAtm {
	@Override
	public int fetchBalance(Card card) throws FunctionNotAllowed {
		if (card == null)
			throw new FunctionNotAllowed("Please insert card..");

		var account = card.getAccount();
		return account.getBalance();
	}

	@Override
	public boolean withdraw(int requestAmount, Card card, InventoryManager inventoryManager)
			throws FunctionNotAllowed, HardwareException {
		if (card == null)
			throw new FunctionNotAllowed("Please insert card..");
		var account = card.getAccount();
		var requiredNotes = inventoryManager.withdraw(requestAmount, account);
		if (requiredNotes == null)
			return false;

		try {
			Dispenser.dispenseCash();
			inventoryManager.enforceInvariants(account, requiredNotes, requestAmount);
			card.getAccount().decrementAccountBalance(requestAmount);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	@Override
	public ATMStates getStatus() {
		return ATMStates.AUTHENTICATED;
	}

	@Override
	public void ejectCard() throws FunctionNotAllowed {

	}

}
