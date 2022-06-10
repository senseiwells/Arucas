package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.Value;

public class BracketAssignNode extends VariableAssignNode {
	private final Node firstNode;
	private final Node secondNode;

	public BracketAssignNode(Node firstNode, Token token, Node secondNode, Node thirdNode) {
		super(token, firstNode.syntaxPosition, thirdNode.syntaxPosition, thirdNode);
		this.firstNode = firstNode;
		this.secondNode = secondNode;
	}

	@Override
	public Value visit(Context context) throws CodeError {
		Value first = this.firstNode.visit(context);
		Value second = this.secondNode.visit(context);
		return first.bracketAssign(context, second, this.getNewValue(context), this.syntaxPosition);
	}
}
