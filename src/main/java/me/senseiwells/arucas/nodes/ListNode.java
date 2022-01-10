package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.impl.ArucasValueListCustom;
import me.senseiwells.arucas.values.ListValue;
import me.senseiwells.arucas.values.Value;

import java.util.List;

public class ListNode extends Node {
	private final List<Node> elementNodes;

	public ListNode(List<Node> elementNodes, ISyntax startPos, ISyntax endPos) {
		super(new Token(Token.Type.LIST, startPos, endPos));
		this.elementNodes = elementNodes;
	}

	@Override
	public Value<?> visit(Context context) throws CodeError, ThrowValue {
		ArucasValueListCustom elements = new ArucasValueListCustom();
		for (Node elementNode : this.elementNodes) {
			elements.add(elementNode.visit(context));
		}
		
		return new ListValue(elements);
	}
}
