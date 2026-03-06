package tictactoe.outputStrategy;

import tictactoe.Player;
import tictactoe.enums.Symbol;

public class ConsoleStrategy implements OutputStrategy {

	@Override
	public void displayMessages(String message) {
		System.out.println(message);
	}

	@Override
	public void displayGrid(Symbol[][] grid) {
		for (int i = 0; i < grid.length; ++i) {
			for (int j = 0; j < grid.length; ++j) {
				System.out.print(grid[i][j].getChar() + " ");
			}

			System.out.println();
		}
	}

	@Override
	public void displayWinner(Player winner) {
		System.out.println(
				"Player: " + winner.getName() + " with Symbol: " + winner.getSymbol().getChar() + "won the game");
	}
}
