package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.NumberValue;
import me.senseiwells.arucas.values.Value;

public class UnaryOperatorNode extends DirectAccessNode<Value> {
	private final Node node;

	public UnaryOperatorNode(Token token, Node node) {
		super(token, null);
		this.node = node;
	}

	@Override
	public Value visit(Context context) throws CodeError, ThrowValue {
		Value value = this.node.visit(context);
		return value.onUnaryOperation(context, this.token.type, this.syntaxPosition);
	}

	@Override
	public String toString() {
		return "(%s, %s)".formatted(this.token, this.node);
	}

	@Override
	public Value getValue() {
		// Technically we could also return booleans here, but why would you write !false instead of true?
		if (this.node instanceof NumberNode numberNode) {
			NumberValue number = numberNode.getValue();
			return this.token.type == Token.Type.MINUS ? NumberValue.of(number.value * -1) : number;
		}
		if (this.node instanceof UnaryOperatorNode unaryNode) {
			Value value = unaryNode.getValue();
			if (value instanceof NumberValue number) {
				return this.token.type == Token.Type.MINUS ? NumberValue.of(number.value * -1) : number;
			}
		}
		return null;
	}
}
