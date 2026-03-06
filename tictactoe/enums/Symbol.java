package tictactoe.enums;

public enum Symbol {
	X('X'),
	O('O'),
	EMPTY('-');

	private char c;
	Symbol(char c){
		this.c = c;
	}

	public char getChar(){
		return c;
	}
}
