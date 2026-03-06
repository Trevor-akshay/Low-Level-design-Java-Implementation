package tictactoe;

import tictactoe.enums.GameStatus;
import tictactoe.outputStrategy.OutputProcessor;

/**
 * Game — controller for a Tic-Tac-Toe match between two players.
 *
 * <p>
 * <b>Game loop</b>: Alternates turns between {@code p1} and {@code p2} until
 * the game
 * ends in a WIN or DRAW. Each turn:
 * <ol>
 * <li>The current player's {@link tictactoe.InputStrategy.InputStrategy}
 * provides coordinates.</li>
 * <li>{@link Board#makeMove} validates bounds and occupancy, then marks the
 * cell.</li>
 * <li>If the move is valid, {@link Board#hasPlayerWon} checks rows, columns,
 * and diagonals.</li>
 * <li>If the board is full with no winner, {@link Board#isDraw} returns
 * {@code true}.</li>
 * </ol>
 *
 * <p>
 * <b>Input flexibility</b>: Either player can use a
 * {@link tictactoe.InputStrategy.ScannerStrategy}
 * (keyboard) or {@link tictactoe.InputStrategy.AIStrategy} (computer) — decided
 * at construction.
 */
public class Game {
	/** Board dimension — the board is N × N. */
	int N;
	Player p1;
	Player p2;

	GameStatus gameStatus;
	/** Non-null only after the game ends with a winner. */
	Player winner;
	boolean isDraw;

	Board board;
	/** The player whose turn it currently is. */
	private Player currentPlayer;

	OutputProcessor outputProcessor;

	Game(int N, Player p1, Player p2, OutputProcessor outputProcessor) {
		this.gameStatus = GameStatus.IN_PROGRESS;
		this.N = N;
		this.p1 = p1;
		this.p2 = p2;
		this.winner = null;
		this.isDraw = false;
		this.currentPlayer = p1; // p1 always goes first

		this.board = new Board(N);

		this.outputProcessor = outputProcessor;
	}

	/**
	 * Starts and runs the game loop until the game ends (WIN or DRAW).
	 *
	 * <p>
	 * Invalid inputs (out of bounds or already occupied) are handled gracefully —
	 * the player is prompted to retry without advancing the turn.
	 */
	public void startGame() {
		Outer: while (gameStatus == GameStatus.IN_PROGRESS) {

			// Ask the current player for a move (keyboard or AI).
			int[] coordinates = currentPlayer.getInputStategy().getInput(board.getGrid());
			int x = coordinates[0];
			int y = coordinates[1];

			// Validate and apply the move; retry if invalid.
			if (!board.makeMove(x, y, getCurrentPlayer().getSymbol(), getCurrentPlayer().getValue())) {
				outputProcessor.displayMessages("Please try again, it is either out of bounds or already taken");
				continue Outer;
			}

			// Render the updated board.
			outputProcessor.displayGrid(board.getGrid());

			// Check for win (row, column, or diagonal from the last move).
			if (board.hasPlayerWon(x, y)) {
				setWinner(getCurrentPlayer());
				setGameStatus(GameStatus.WoN);
			} else if (board.isDraw()) {
				// Board full; no winner.
				setIsDraw(true);
				setGameStatus(GameStatus.DRAW);
			}

			switchPlayer();
		}
	}

	/** Alternates {@code currentPlayer} between p1 and p2. */
	public void switchPlayer() {
		if (currentPlayer == p1) {
			currentPlayer = p2;
		} else {
			currentPlayer = p1;
		}
	}

	public Player getWinner() {
		return winner;
	}

	public boolean getIsDraw() {
		return isDraw;
	}

	public Player getCurrentPlayer() {
		return currentPlayer;
	}

	private void setWinner(Player p) {
		this.winner = p;
	}

	private void setIsDraw(boolean val) {
		this.isDraw = true;
	}

	private void setGameStatus(GameStatus gameStatus) {
		this.gameStatus = gameStatus;
	}
}
