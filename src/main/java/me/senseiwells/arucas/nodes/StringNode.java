package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.values.StringValue;

public class StringNode extends DirectAccessNode<StringValue> {
	public StringNode(Token token, StringValue value) {
		super(token, value);
	}
}
