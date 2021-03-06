package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.Value;

public class BinaryOperatorNode extends Node {
	private final Node leftNode;
	private final Node rightNode;

	public BinaryOperatorNode(Node leftNode, Token operatorToken, Node rightNode) {
		super(operatorToken, leftNode.syntaxPosition, rightNode.syntaxPosition);
		this.leftNode = leftNode;
		this.rightNode = rightNode;
	}

	@Override
	public Value visit(Context context) throws CodeError, ThrowValue {
		Value left = this.leftNode.visit(context);
		return left.onBinaryOperation(context, this.rightNode::visit, this.token.type, this.syntaxPosition);
	}

	@Override
	public String toString() {
		return "(%s %s %s)".formatted(this.leftNode, this.token, this.rightNode);
	}
}
