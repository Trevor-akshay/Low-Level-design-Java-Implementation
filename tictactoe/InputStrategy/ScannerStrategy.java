package tictactoe.InputStrategy;

import java.util.Scanner;

import tictactoe.enums.Symbol;

public class ScannerStrategy implements InputStategy {
	Scanner sc;

	public ScannerStrategy() {
		sc = new Scanner(System.in);
	}

	@Override
	public int[] getInput(Symbol[][] grid) {
		System.out.println(
				" please enter the input for x cordinate within the limits of  0 - " + grid.length);

		int x = sc.nextInt();
		System.out.println(" please enter the input for y cordinate within the limits of 0 - " + grid.length);

		int y = sc.nextInt();

		return new int[] { x, y };
	}

}
