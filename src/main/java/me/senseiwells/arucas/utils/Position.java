package me.senseiwells.arucas.utils;

public class Position {
	private static final Position emptyPosition = new Position(0, 0, 0, "Arucas");
	
	public final String fileName;
	public final int index;
	public final int line;
	public final int column;
	
	public Position(int index, int line, int column, String fileName) {
		this.index = index;
		this.line = line;
		this.column = column;
		this.fileName = fileName;
	}
	
	public static Position empty() {
		return Position.emptyPosition;
	}
}
