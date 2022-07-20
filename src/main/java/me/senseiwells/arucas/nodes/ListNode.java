package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.values.ListValue;

import java.util.List;

public class ListNode extends Node {
	private final List<Node> elementNodes;

	public ListNode(List<Node> elementNodes, ISyntax startPos, ISyntax endPos) {
		super(new Token(Token.Type.LIST, startPos, endPos));
		this.elementNodes = elementNodes;
	}

	@Override
	public ListValue visit(Context context) throws CodeError, ThrowValue {
		ArucasList elements = new ArucasList();
		for (Node elementNode : this.elementNodes) {
			elements.add(elementNode.visit(context));
		}
		this.elementNodes.clear();

		return new ListValue(elements);
	}
}
