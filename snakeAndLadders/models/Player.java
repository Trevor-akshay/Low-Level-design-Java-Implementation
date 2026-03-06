package snakeAndLadders.models;

public class Player {
	private final int id;

	private final String name;

	private int currentPosition;

	Player(int id, String name) {
		this.id = id;
		this.name = name;
		this.currentPosition = 1;
	}

	public int getCurrentPosition() {
		return currentPosition;
	}

	public void setCurrentPosition(int index) {
		this.currentPosition = index;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}
}
