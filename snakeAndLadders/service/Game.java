package snakeAndLadders.service;

import java.util.List;

import snakeAndLadders.models.Dice;
import snakeAndLadders.models.Player;
import snakeAndLadders.obstacles.Obstacle;

/**
 * Game — controller for a Snake and Ladders match.
 *
 * <p>
 * <b>Turn loop</b>: Players rotate turns via {@link #switchPlayer()}.
 * Each turn the current player rolls the dice, the board applies any obstacle
 * on the
 * landing cell (snake, ladder, or portal), and the game checks for a win
 * condition.
 *
 * <p>
 * <b>Win condition</b>: A player wins by landing exactly on cell N×N (the last
 * cell).
 * Invalid moves (would overshoot or go backwards off the start) are handled by
 * {@link Board#makeMove} — if the move is not valid, it returns {@code false}
 * and no
 * win check is performed.
 *
 * <p>
 * The game loop runs until a winner is found — there is no draw in standard
 * rules.
 */
public class Game {
	Player[] players;
	Player currentPlayer;
	/** Board dimension — the board is N×N and cells are numbered 1..N*N. */
	int N;
	Board board;
	/**
	 * Round-robin player index — advances modulo player count on each turn switch.
	 */
	int playerIndex;
	Dice dice;

	/**
	 * Sets up the game: creates the board with obstacles and positions the first
	 * player.
	 *
	 * @param players   array of players participating in the game
	 * @param N         board dimension (board is N × N)
	 * @param obstacles list of snakes, ladders, and portals to place on the board
	 */
	public Game(Player[] players, int N, List<Obstacle> obstacles) {
		this.N = N;
		this.board = new Board(N, obstacles);

		this.playerIndex = 0;
		this.players = players;

		switchPlayer(); // set currentPlayer to the first player
		this.dice = new Dice(1, 6); // standard 6-sided die
	}

	/**
	 * Starts and runs the game loop until a player wins.
	 *
	 * <p>
	 * On each iteration:
	 * <ol>
	 * <li>The current player rolls the dice.</li>
	 * <li>{@link Board#makeMove} advances their position and applies any
	 * obstacle.</li>
	 * <li>If the move landed on the winning cell, the game ends.</li>
	 * <li>Otherwise, the next player takes their turn.</li>
	 * </ol>
	 */
	public void startGame() {
		while (true) {
			int diceValue = dice.getDiceValue();
			var wasValidMove = board.makeMove(currentPlayer, diceValue);

			if (wasValidMove && hasPlayerWon()) {
				// Notify player won..
				return;
			}

			switchPlayer();
		}
	}

	/** Advances to the next player in round-robin order. */
	private final void switchPlayer() {
		currentPlayer = players[playerIndex];
		playerIndex = (playerIndex + 1) % players.length;
	}

	/**
	 * Checks if the current player has won by reaching the last cell (N×N).
	 *
	 * @return {@code true} if the current player's position equals N×N
	 */
	public boolean hasPlayerWon() {
		return currentPlayer.getCurrentPosition() == N * N;
	}
}
