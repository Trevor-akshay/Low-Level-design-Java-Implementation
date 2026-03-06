package tictactoe.outputStrategy;

import tictactoe.Player;
import tictactoe.enums.Symbol;

public interface OutputStrategy {
	public void displayMessages(String message);

	public void displayGrid(Symbol[][] grid);

	public void displayWinner(Player winner);
}
