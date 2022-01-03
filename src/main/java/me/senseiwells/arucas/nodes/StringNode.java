package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.values.StringValue;
import me.senseiwells.arucas.values.Value;

public class StringNode extends Node {
	private final StringValue value;

	public StringNode(Token token, StringValue value) {
		super(token);
		this.value = value;
	}

	@Override
	public Value<?> visit(Context context) {
		return this.value;
	}
}
