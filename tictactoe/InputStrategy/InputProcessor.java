package tictactoe.InputStrategy;

import tictactoe.enums.Symbol;

public class InputProcessor {
	InputStategy inputStategy;

	public InputProcessor(InputStategy inputStategy) {
		this.inputStategy = inputStategy;
	}

	public int[] getInput(Symbol[][] grid) {
		return this.inputStategy.getInput(grid);
	}

	public void setInputStrategy(InputStategy inputStategy) {
		this.inputStategy = inputStategy;
	}
}
