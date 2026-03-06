package atm.factory;

import java.util.UUID;

import atm.models.Account;

public class AccountFactory {
	public static Account createAccount(String name, String pinCode, int balance) {
		UUID acccountId = UUID.randomUUID();
		return new Account(acccountId, name, pinCode, balance);
	}
}
