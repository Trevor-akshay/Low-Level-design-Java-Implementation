package tictactoe;

import tictactoe.enums.Symbol;

public class Board {
	private final Symbol[][] grid;

	private final int N;
	private final int[] rows;
	private final int[] cols;

	private int mainDiagonal;
	private int antiDiagonal;

	private int movesMade;

	public Board(int N) {
		this.N = N;
		this.grid = new Symbol[N][N];
		this.intializeGrid();

		this.rows = new int[N];
		this.cols = new int[N];

		this.mainDiagonal = 0;
		this.antiDiagonal = 0;

		this.movesMade = 0;
	}

	private void intializeGrid() {
		for (int i = 0; i < N; ++i) {
			for (int j = 0; j < N; ++j) {
				this.grid[i][j] = Symbol.EMPTY;
			}
		}
	}

	public boolean makeMove(int x, int y, Symbol symbol,int val) {
		if (!isValidMove(x, y))
			return false;

		rows[x] += val;
		cols[y] += val;

		if (x == y)
			mainDiagonal += val;
		if (x + y == N - 1)
			antiDiagonal += val;

		grid[x][y] = symbol;
		movesMade += 1;
		return true;
	}

	public boolean hasPlayerWon(int x, int y) {
		return Math.abs(rows[x]) == N || Math.abs(cols[y]) == N || Math.abs(mainDiagonal) == N
				|| Math.abs(antiDiagonal) == N;
	}

	public boolean isDraw() {
		return movesMade == N * N;
	}

	private boolean isValidMove(int x, int y) {
		return x >= 0 && x < N && y >= 0 && y < N && grid[x][y] == Symbol.EMPTY;
	}

	public Symbol[][] getGrid() {
		return grid;
	}

	public int getN() {
		return N;
	}


	public int getMovesMade() {
		return movesMade;
	}
}
