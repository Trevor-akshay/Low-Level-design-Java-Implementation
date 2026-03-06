package tictactoe;

import tictactoe.InputStrategy.InputStategy;
import tictactoe.enums.Symbol;

public class Player {
	private final Symbol symbol;
	private final String name;
	private final int val;
	private InputStategy inputStategy;

	Player(Symbol symbol, String name, int val, InputStategy inputStategy) {
		this.symbol = symbol;
		this.name = name;
		this.val = val;
		this.inputStategy = inputStategy;
	}

	public Symbol getSymbol() {
		return symbol;
	}

	public String getName() {
		return name;
	}

	public int getValue() {
		return val;
	}

	public InputStategy getInputStategy() {
		return this.inputStategy;
	}
}
