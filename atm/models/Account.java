package atm.models;

import java.util.UUID;

public class Account {
	private final UUID accountId;
	private String name;
	private String pincode;
	private int balance;

	public Account(UUID accountId, String name, String pincode, int balance) {
		this.accountId = accountId;
		this.name = name;
		this.pincode = pincode;
		this.balance = balance;
	}

	public UUID getAccountId() {
		return accountId;
	}

	public String getPincode() {
		return pincode;
	}

	public void setPincode(String pincode) {
		this.pincode = pincode;
	}

	public void decrementAccountBalance(int amount) {
		if (amount <= this.balance)
			setBalance(this.balance - amount);
	}

	public int getBalance() {
		return balance;
	}

	public void setBalance(int balance) {
		this.balance = balance;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
