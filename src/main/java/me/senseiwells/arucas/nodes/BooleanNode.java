package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.values.BooleanValue;
import me.senseiwells.arucas.values.Value;

public class BooleanNode extends Node {
	private final BooleanValue value;

	public BooleanNode(Token token) {
		super(token);
		this.value = BooleanValue.of(Boolean.parseBoolean(token.content));
	}

	@Override
	public Value<?> visit(Context context) {
		return this.value;
	}
}
