package atm.repo;

import java.util.Map;
import java.util.UUID;

import atm.factory.AccountFactory;
import atm.models.Account;

public class AccountRepo {
	private final Map<UUID, Account> accounts;

	public AccountRepo(Map<UUID, Account> accounts) {
		this.accounts = accounts;
	}

	public void create(String name, String pin, int cash) {
		var account = AccountFactory.createAccount(name, pin, cash);
		accounts.put(account.getAccountId(), account);
	}

	public Account read(UUID accountId) {
		return accounts.get(accountId);
	}

	public void delete(UUID accountId) {
		accounts.remove(accountId);
	}
}
