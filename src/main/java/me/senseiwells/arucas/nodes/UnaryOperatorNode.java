package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.NumberValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.classes.ArucasClassValue;

import java.util.ArrayList;

public class UnaryOperatorNode extends DirectAccessNode<Value<?>> {
	private final Node node;

	public UnaryOperatorNode(Token token, Node node) {
		super(token, null);
		this.node = node;
	}

	@Override
	public Value<?> visit(Context context) throws CodeError, ThrowValue {
		Value<?> value = this.node.visit(context);
		if (value instanceof ArucasClassValue classValue && classValue.hasOperatorMethod(this.token.type, 1)) {
			return classValue.getOperatorMethod(this.token.type, 1).call(context, new ArrayList<>(1));
		}
		switch (this.token.type) {
			case NOT -> value = value.not(context, this.syntaxPosition);
			case MINUS -> value = value.multiplyBy(context, NumberValue.of(-1), this.syntaxPosition);
		}
		return value;
	}

	@Override
	public String toString() {
		return "(%s, %s)".formatted(this.token, this.node);
	}

	@Override
	public Value<?> getValue() {
		if (this.node instanceof NumberNode numberNode) {
			NumberValue number = numberNode.getValue();
			return this.token.type == Token.Type.MINUS ? NumberValue.of(number.value * -1) : number;
		}
		if (this.node instanceof UnaryOperatorNode unaryNode) {
			Value<?> value = unaryNode.getValue();
			if (value instanceof NumberValue number) {
				return this.token.type == Token.Type.MINUS ? NumberValue.of(number.value * -1) : number;
			}
		}
		return null;
	}
}
