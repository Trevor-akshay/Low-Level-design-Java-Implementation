package tictactoe;

import tictactoe.InputStrategy.AIStrategy;
import tictactoe.InputStrategy.ScannerStrategy;
import tictactoe.enums.Symbol;
import tictactoe.outputStrategy.ConsoleStrategy;
import tictactoe.outputStrategy.OutputProcessor;

public class TicTacToe {
	public static void main(String[] args) {
		Player p1 = new Player(Symbol.X, "Bruce Wayne", 1, new ScannerStrategy());
		Player p2 = new Player(Symbol.O, "Joker", -1, new AIStrategy());

		OutputProcessor outputProcessor = new OutputProcessor(new ConsoleStrategy());
		Game game = new Game(3, p1, p2, outputProcessor);

		game.startGame();

		if (game.getIsDraw()) {
			outputProcessor.displayMessages("Game Draw");
		}

		var winner = game.getWinner();
		if (winner != null) {
			outputProcessor.displayWinner(winner);
		}
	}
}
