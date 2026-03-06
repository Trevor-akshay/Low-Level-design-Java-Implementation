package tictactoe.InputStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import tictactoe.enums.Symbol;

public class AIStrategy implements InputStategy {

	@Override
	public int[] getInput(Symbol[][] grid) {
		List<int[]> possibleCoordinates = new ArrayList<>();

		for (int i = 0; i < grid.length; ++i) {
			for (int j = 0; j < grid.length; ++j) {
				if (grid[i][j] == Symbol.EMPTY)
					possibleCoordinates.add(new int[] { i, j });
			}
		}

		if (possibleCoordinates.size() == 0)
			throw new IllegalAccessError("Invalid State, but does not happen");
		
		Random rand = new Random();
		int idx = rand.nextInt(possibleCoordinates.size());

		return possibleCoordinates.get(idx);
	}

}
