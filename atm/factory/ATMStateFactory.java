package atm.factory;

import atm.enums.ATMStates;
import atm.states.Authenticated;
import atm.states.CardInserted;
import atm.states.IAtm;
import atm.states.Idle;

public class ATMStateFactory {
	public static IAtm createState(ATMStates state) {
		switch (state) {
			case IDLE:
				return new Idle();
			case AUTHENTICATED:
				return new Authenticated();
			case CARD_INSERTED:
				return new CardInserted();
			default:
				return new Idle();
		}
	}
}
