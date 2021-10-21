package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.values.NumberValue;
import me.senseiwells.arucas.values.Value;

public class NumberNode extends Node {
	public final NumberValue value;

	public NumberNode(Token token) {
		super(token);
		this.value = new NumberValue(Double.parseDouble(token.content));
		this.value.setPos(this.startPos, this.endPos);
	}

	@Override
	public Value<?> visit(Context context) {
		return this.value;
	}
}
