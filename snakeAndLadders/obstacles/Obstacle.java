package snakeAndLadders.obstacles;

public abstract class Obstacle {
	int start;
	int end;

	Obstacle(int start, int end) {
		this.start = start;
		this.end = end;
	}

	public int getStartIndex() {
		return start;
	}
	
	public int getNextIndex() {
		return end;
	}
}
