package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.values.NullValue;

public class NullNode extends DirectAccessNode<NullValue> {
	public NullNode(Token token) {
		super(token, NullValue.NULL);
	}
}
