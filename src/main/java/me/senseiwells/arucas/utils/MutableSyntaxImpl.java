package me.senseiwells.arucas.utils;

import me.senseiwells.arucas.api.ISyntax;

public class MutableSyntaxImpl implements ISyntax {
	public Position start;
	public Position end;
	
	public MutableSyntaxImpl(Position start, Position end) {
		this.start = start;
		this.end = end;
	}
	
	@Override
	public Position getStartPos() {
		return start;
	}
	
	@Override
	public Position getEndPos() {
		return end;
	}
}
