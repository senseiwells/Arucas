package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.NumberValue;
import me.senseiwells.arucas.values.Value;

public class UnaryOperatorNode extends Node {
	private final Node node;

	public UnaryOperatorNode(Token token, Node node) {
		super(token);
		this.node = node;
	}

	@Override
	public Value<?> visit(Context context) throws CodeError, ThrowValue {
		Value<?> value = this.node.visit(context);
		try {
			switch (this.token.type) {
				case MINUS -> value = value.multiplyBy(new NumberValue(-1), this.syntaxPosition);
				case NOT -> value = value.not(this.syntaxPosition);
			}
			return value;
		}
		catch (RuntimeError e) {
			throw e.setContext(context);
		}
	}

	@Override
	public String toString() {
		return "(%s, %s)".formatted(this.token, this.node);
	}
}
