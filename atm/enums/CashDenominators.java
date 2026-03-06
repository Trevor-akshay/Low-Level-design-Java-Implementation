package atm.enums;

public enum CashDenominators {
	HUNDRED(100),
	FIFTY(50),
	TWEENTY(20),
	TEN(10);

	private final int value;

	CashDenominators(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

}
