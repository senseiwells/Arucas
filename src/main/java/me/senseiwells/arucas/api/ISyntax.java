package me.senseiwells.arucas.api;

import me.senseiwells.arucas.utils.Position;
import me.senseiwells.arucas.utils.ImmutableSyntaxImpl;

/**
 * Syntax position interface.
 */
public interface ISyntax {
	/**
	 * Returns the start position of the syntax.
	 */
	Position getStartPos();
	
	/**
	 * Returns the end position of the syntax.
	 */
	Position getEndPos();
	
	static ISyntax of(Position start, Position end) {
		return new ImmutableSyntaxImpl(start, end);
	}
	
	static ISyntax of(Position position) {
		return new ImmutableSyntaxImpl(position, position);
	}
	
	static ISyntax lastOf(ISyntax syntax) {
		return new ImmutableSyntaxImpl(syntax.getEndPos(), syntax.getEndPos());
	}
	
	static ISyntax empty() {
		// TODO: Remove empty allocations
		return ISyntax.of(Position.empty(), Position.empty());
	}
	
	static ISyntax emptyOf(String name) {
		Position empty = new Position(0, 0, 0, name);
		return ISyntax.of(empty, empty);
	}
}
