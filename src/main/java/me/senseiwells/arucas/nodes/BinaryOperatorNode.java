package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.values.BooleanValue;
import me.senseiwells.arucas.values.NumberValue;
import me.senseiwells.arucas.values.Value;

public class BinaryOperatorNode extends Node {
	public final Node leftNode;
	public final Node rightNode;

	public BinaryOperatorNode(Node leftNode, Token operatorToken, Node rightNode) {
		super(operatorToken, leftNode.startPos, rightNode.endPos);
		this.leftNode = leftNode;
		this.rightNode = rightNode;
	}

	@Override
	public Value<?> visit(Context context) throws CodeError, ThrowValue {
		Value<?> left = this.leftNode.visit(context);
		Value<?> right = null;
		try {
			Value<?> result = switch (this.token.type) {
				case AND -> {
					BooleanValue leftBoolean = (BooleanValue) left;
					yield (!leftBoolean.value) ? new BooleanValue(false) : leftBoolean.isAnd((BooleanValue) (right = this.rightNode.visit(context)));
				}
				case OR -> {
					BooleanValue leftBoolean = (BooleanValue) left;
					yield leftBoolean.value ? new BooleanValue(true) : leftBoolean.isOr((BooleanValue) (right = this.rightNode.visit(context)));
				}
				default -> {
					right = this.rightNode.visit(context);
					yield switch (this.token.type) {
						case PLUS -> left.addTo(right);
						case MINUS -> ((NumberValue) left).subtractBy((NumberValue) right);
						case MULTIPLY -> ((NumberValue) left).multiplyBy((NumberValue) right);
						case DIVIDE -> ((NumberValue) left).divideBy((NumberValue) right);
						case POWER -> ((NumberValue) left).powerBy((NumberValue) right);
						case EQUALS -> left.isEqual(right);
						case NOT_EQUALS -> left.isNotEqual(right);
						case LESS_THAN, LESS_THAN_EQUAL, MORE_THAN, MORE_THAN_EQUAL -> ((NumberValue) left).compareNumber((NumberValue) right, this.token.type);
						default -> throw new CodeError(CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR, "Expected an operator", this.startPos, this.endPos);
					};
				}
			};
			return result.setPos(this.startPos, this.endPos);
		}
		catch (ClassCastException classCastException) {
			right = right == null ? this.rightNode.visit(context) : right;
			throw new RuntimeError("The operation '%s' cannot be applied to '%s' and '%s'".formatted(this.token.type, left.value, right.value), this.startPos, this.endPos, context);
		}
		catch (RuntimeError e) {
			throw e.setContext(context);
		}
	}

	@Override
	public String toString() {
		return "(%s %s %s)".formatted(this.leftNode, this.token, this.rightNode);
	}
}
