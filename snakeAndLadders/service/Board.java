package snakeAndLadders.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import snakeAndLadders.models.Cell;
import snakeAndLadders.models.Player;
import snakeAndLadders.obstacles.Obstacle;

public class Board {
	private final Cell[] grid;
	private final int N;

	Board(int N, List<Obstacle> obstacles) {
		this.N = N;
		this.grid = new Cell[N * N + 1];

		initialiseGrid(obstacles);
	}

	private final void initialiseGrid(List<Obstacle> obstacles) {
		for (int i = 0; i < N * N + 1; ++i)
			grid[i] = new Cell(i);

		for (var obstacle : obstacles) {
			int start = obstacle.getStartIndex();

			grid[start].addObstacle(obstacle);
		}
	}

	public boolean makeMove(Player player, int diceValue) {
		int playerCurrentPosition = player.getCurrentPosition();
		int nextPosition = playerCurrentPosition + diceValue;
		if (!isValidMove(nextPosition))
			return false;

		int actualMovePosition = grid[nextPosition].getNextIndex();

		Set<Integer> visitedPositions = new HashSet<>();
		visitedPositions.add(playerCurrentPosition);

		while (grid[actualMovePosition].getObstacle() != null && !visitedPositions.contains(actualMovePosition)) {
			visitedPositions.add(actualMovePosition);
			actualMovePosition = grid[actualMovePosition].getNextIndex();
		}

		player.setCurrentPosition(actualMovePosition);
		return true;
	}

	private final boolean isValidMove(int index) {
		return index <= N * N;
	}
}
