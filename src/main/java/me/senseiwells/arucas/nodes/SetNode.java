package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasSet;
import me.senseiwells.arucas.values.SetValue;
import me.senseiwells.arucas.values.Value;

import java.util.List;

public class SetNode extends Node {
	private final List<Node> elementNodes;

	public SetNode(List<Node> elementNodes, ISyntax startPos, ISyntax endPos) {
		super(new Token(Token.Type.SET, startPos, endPos));
		this.elementNodes = elementNodes;
	}

	@Override
	public Value<?> visit(Context context) throws CodeError, ThrowValue {
		ArucasSet elements = new ArucasSet();
		for (Node elementNode : this.elementNodes) {
			elements.add(context, elementNode.visit(context));
		}

		return new SetValue(elements);
	}
}
