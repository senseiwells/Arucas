package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.Value;

import java.util.List;

public class ScopeNode extends Node {
	private final List<Node> elementNodes;

	public ScopeNode(List<Node> elementNodes, ISyntax startPos, ISyntax endPos) {
		super(new Token(Token.Type.SCOPE, startPos, endPos));
		this.elementNodes = elementNodes;
	}

	@Override
	public Value visit(Context context) throws CodeError, ThrowValue {
		context.pushScope(this.syntaxPosition);

		for (Node elementNode : this.elementNodes) {
			elementNode.visit(context);
		}

		context.popScope();
		return NullValue.NULL;
	}
}
