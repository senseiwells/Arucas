package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.values.BooleanValue;

public class BooleanNode extends DirectAccessNode<BooleanValue> {
	public BooleanNode(Token token) {
		super(token, BooleanValue.of(Boolean.parseBoolean(token.content)));
	}
}
