package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.Position;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.values.Value;

public abstract class Node {
	public final Token token;
	public final ISyntax syntaxPosition;

	Node(Token token, Position startPos, Position endPos) {
		this.token = token;
		this.syntaxPosition = ISyntax.of(startPos, endPos);
	}
	
	Node(Token token, ISyntax startPos, ISyntax endPos) {
		this(token, startPos.getStartPos(), endPos.getEndPos());
	}
	
	Node(Token token, ISyntax syntaxPosition) {
		this.token = token;
		this.syntaxPosition = syntaxPosition;
	}

	Node(Token token) {
		this(token, token.syntaxPosition);
	}

	public abstract Value<?> visit(Context context) throws CodeError, ThrowValue;

	@Override
	public String toString() {
		return this.token.toString();
	}
}
