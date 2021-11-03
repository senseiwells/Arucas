package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.Position;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.Value;

import java.util.List;

public class ScopeNode extends Node {
	public final List<Node> elementNodes;

	public ScopeNode(List<Node> elementNodes, Position posStart, Position posEnd) {
		super(new Token(Token.Type.SCOPE, posStart, posEnd));
		this.elementNodes = elementNodes;
	}

	@Override
	public Value<?> visit(Context context) throws CodeError, ThrowValue {
		context.pushScope(this.startPos);
		
		for (Node elementNode : this.elementNodes) {
			elementNode.visit(context);
		}
		
		context.popScope();
		return new NullValue();
	}
}
