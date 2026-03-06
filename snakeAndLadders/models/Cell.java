package snakeAndLadders.models;

import snakeAndLadders.obstacles.Obstacle;

public class Cell {
	private int nextIndex;
	private Obstacle obstacle;

	public Cell(int nextIndex) {
		this.nextIndex = nextIndex;
		this.obstacle = null;
	}

	Cell(Obstacle obstacle) {
		this.obstacle = obstacle;
		nextIndex = getNextIndex();
	}

	public void addObstacle(Obstacle obstacle) {
		this.obstacle = obstacle;
	}

	public void removeObstacle() {
		this.obstacle = null;
	}

	public Obstacle getObstacle() {
		return this.obstacle;
	}

	public int getNextIndex() {
		if (obstacle != null)
			return obstacle.getNextIndex();

		return nextIndex;
	}
}
