package tictactoe.outputStrategy;

import tictactoe.Player;
import tictactoe.enums.Symbol;

public class OutputProcessor {
	OutputStrategy outputStrategy;

	public OutputProcessor(OutputStrategy outputStrategy) {
		this.outputStrategy = outputStrategy;
	}

	public void displayMessages(String message) {
		this.outputStrategy.displayMessages(message);
	}

	public void displayGrid(Symbol[][] grid) {
		this.outputStrategy.displayGrid(grid);
	}

	public void displayWinner(Player winner) {
		this.outputStrategy.displayWinner(winner);
	}

	public void setOutputStrategy(OutputStrategy outputStrategy) {
		this.outputStrategy = outputStrategy;
	}
}
