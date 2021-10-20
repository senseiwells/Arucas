package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.Position;
import me.senseiwells.arucas.values.ListValue;
import me.senseiwells.arucas.values.Value;

import java.util.ArrayList;
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
		
		List<Value<?>> elements = new ArrayList<>();
		for (Node elementNode : this.elementNodes)
			elements.add(elementNode.visit(context));
		
		context.popScope();
		return new ListValue(elements).setPos(this.startPos, this.endPos).setContext(context);
	}
}
