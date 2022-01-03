package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.Value;

public class ContinueNode extends Node {
	public ContinueNode(ISyntax position) {
		super(new Token(Token.Type.CONTINUE, position));
	}

	@Override
	public Value<?> visit(Context context) throws CodeError, ThrowValue {
		// We push a scope to make StackTraces easier to read
		context.pushScope(this.syntaxPosition);
		throw new ThrowValue.Continue();
	}
}
