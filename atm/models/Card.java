package atm.models;

import java.util.UUID;

public class Card {
	private final UUID cardId;
	Account account;

	public Card(UUID cardId, Account account) {
		this.cardId = cardId;
		this.account = account;
	}

	public UUID getCardId() {
		return cardId;
	}

	public Account getAccount() {
		return this.account;
	}

}
