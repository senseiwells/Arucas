package me.senseiwells.arucas.utils;

import me.senseiwells.arucas.api.ISyntax;

public class ImmutableSyntaxImpl implements ISyntax {
	public final Position start;
	public final Position end;
	
	public ImmutableSyntaxImpl(Position start, Position end) {
		this.start = start;
		this.end = end;
	}
	
	@Override
	public Position getStartPos() {
		return this.start;
	}
	
	@Override
	public Position getEndPos() {
		return this.end;
	}
}
