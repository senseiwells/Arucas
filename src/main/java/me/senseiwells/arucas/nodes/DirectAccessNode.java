package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.Value;

public class DirectAccessNode extends Node {
	public final Value<?> value;

	public DirectAccessNode(Token token, Value<?> value) {
		super(token, token.startPos, token.endPos);
		this.value = value;
	}

	@Override
	public Value<?> visit(Context context) {
		return this.value;
	}
}
