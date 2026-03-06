package atm.service;

import atm.models.Account;

public class AccountService {
	public static boolean validatePin(Account account, String pincode) {
		return account.getPincode().equals(pincode);
	}
}
