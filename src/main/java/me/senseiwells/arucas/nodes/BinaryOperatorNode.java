package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.BooleanValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.classes.ArucasClassValue;

import java.util.ArrayList;
import java.util.List;

public class BinaryOperatorNode extends Node {
	private final Node leftNode;
	private final Node rightNode;

	public BinaryOperatorNode(Node leftNode, Token operatorToken, Node rightNode) {
		super(operatorToken, leftNode.syntaxPosition, rightNode.syntaxPosition);
		this.leftNode = leftNode;
		this.rightNode = rightNode;
	}

	@Override
	public Value<?> visit(Context context) throws CodeError, ThrowValue {
		Value<?> left = this.leftNode.visit(context);
		Value<?> right = null;
		Value<?> result = null;

		switch (this.token.type) {
			case AND -> {
				if (!(left instanceof BooleanValue leftBoolean)) {
					throw new RuntimeError("The operation 'AND' cannot be applied to %s".formatted(left), this.syntaxPosition);
				}
				result = (!leftBoolean.value) ? new BooleanValue(false) : leftBoolean.isAnd(context, (right = this.rightNode.visit(context)), this.syntaxPosition);
			}
			case OR -> {
				if (!(left instanceof BooleanValue leftBoolean)) {
					throw new RuntimeError("The operation 'OR' cannot be applied to %s".formatted(left), this.syntaxPosition);
				}
				result = leftBoolean.value ? new BooleanValue(true) : leftBoolean.isOr(context, (right = this.rightNode.visit(context)), this.syntaxPosition);
			}
			// AND, OR has a special property that the right hand side is not evaluated
			// unless the value we read is either true or false. This means that we need
			// to specify this value after we have checked for AND, OR
			default -> right = this.rightNode.visit(context);
		}

		if (this.token.type != Token.Type.NOT && left instanceof ArucasClassValue classValue && classValue.hasOperatorMethod(this.token.type)) {
			List<Value<?>> parameters = new ArrayList<>();
			parameters.add(right);
			return classValue.getOperatorMethod(this.token.type).call(context, parameters);
		}

		switch (this.token.type) {
			case PLUS -> result = left.addTo(context, right, this.syntaxPosition);
			case MINUS -> result = left.subtractBy(context, right, this.syntaxPosition);
			case MULTIPLY -> result = left.multiplyBy(context, right, this.syntaxPosition);
			case DIVIDE -> result = left.divideBy(context, right, this.syntaxPosition);
			case POWER -> result = left.powerBy(context, right, this.syntaxPosition);
			case EQUALS -> result = left.isEqual(right);
			case NOT_EQUALS -> result = left.isNotEqual(right);
			case LESS_THAN, LESS_THAN_EQUAL, MORE_THAN, MORE_THAN_EQUAL -> result = left.compareNumber(context, right, this.token.type, this.syntaxPosition);
		}

		if (result == null) {
			throw new CodeError(CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR, "Expected an operator", this.syntaxPosition);
		}

		return result;
	}

	@Override
	public String toString() {
		return "(%s %s %s)".formatted(this.leftNode, this.token, this.rightNode);
	}
}
