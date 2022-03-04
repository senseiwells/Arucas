package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.values.NumberValue;

public class NumberNode extends DirectAccessNode<NumberValue> {
	public NumberNode(Token token) {
		super(token, NumberValue.of(Double.parseDouble(token.content)));
	}
}
