package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasValueListCustom;
import me.senseiwells.arucas.values.NumberValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.classes.ArucasClassValue;

import java.util.ArrayList;

public class UnaryOperatorNode extends Node {
	private final Node node;

	public UnaryOperatorNode(Token token, Node node) {
		super(token);
		this.node = node;
	}

	@Override
	public Value<?> visit(Context context) throws CodeError, ThrowValue {
		Value<?> value = this.node.visit(context);
		switch (this.token.type) {
			case NOT -> {
				if (value instanceof ArucasClassValue classValue && classValue.hasOperatorMethod(Token.Type.NOT)) {
					// TODO: Remove empty allocations
					return classValue.getOperatorMethod(Token.Type.NOT).call(context, new ArucasValueListCustom());
				}
				value = value.not(context, this.syntaxPosition);
			}
			case MINUS -> value = value.multiplyBy(context, NumberValue.of(-1), this.syntaxPosition);
		}
		return value;
	}

	@Override
	public String toString() {
		return "(%s, %s)".formatted(this.token, this.node);
	}
}
