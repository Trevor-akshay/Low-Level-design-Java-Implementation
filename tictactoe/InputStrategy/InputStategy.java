package tictactoe.InputStrategy;

import tictactoe.enums.Symbol;

public interface InputStategy {
	public int[] getInput(Symbol[][] grid);
}
