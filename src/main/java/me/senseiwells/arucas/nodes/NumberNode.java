package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.StringUtils;
import me.senseiwells.arucas.values.NumberValue;

public class NumberNode extends DirectAccessNode<NumberValue> {
	public NumberNode(Token token) {
		super(token, NumberValue.of(StringUtils.parseNumber(token.content)));
	}

	public NumberNode(Token token, double value) {
		super(token, NumberValue.of(value));
	}
}
