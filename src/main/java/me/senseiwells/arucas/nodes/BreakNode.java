package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.Value;

public class BreakNode extends Node {
	public BreakNode(ISyntax position) {
		super(new Token(Token.Type.BREAK, position));
	}

	@Override
	public Value<?> visit(Context context) throws ThrowValue {
		// We push a new scope to make StackTraces easier to read
		context.pushScope(this.syntaxPosition);
		throw new ThrowValue.Break();
	}
}
