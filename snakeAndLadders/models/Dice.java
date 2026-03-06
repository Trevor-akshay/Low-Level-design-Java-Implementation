package snakeAndLadders.models;

import java.util.Random;

public class Dice {
	private int count;
	private int side;
	private Random random;

	public Dice(int count, int side) {
		this.count = Math.max(1, count);
		this.side = Math.max(6, Math.min(side, 20));
		random = new Random();
	}

	public int getDiceValue() {
		int totalValue = 0;
		for (int i = 0; i < count; ++i) {
			var diceSide = random.nextInt(side) + 1;
			totalValue += diceSide;
		}

		return totalValue;
	}
}
