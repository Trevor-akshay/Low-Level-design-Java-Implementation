package atm.states;

import atm.enums.ATMStates;
import atm.exceptions.FunctionNotAllowed;
import atm.models.Card;
import atm.service.AccountService;

public class CardInserted extends IAtm {
	@Override
	public boolean validatePin(Card card, String pincode) throws FunctionNotAllowed {
		if (card == null)
			throw new FunctionNotAllowed("Please insert card..");
		var account = card.getAccount();
		return AccountService.validatePin(account, pincode);
	};

	@Override
	public ATMStates getStatus() {
		return ATMStates.CARD_INSERTED;
	}

	@Override
	public void ejectCard() throws FunctionNotAllowed {
	}

}
