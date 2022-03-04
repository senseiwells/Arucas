package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasSet;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.Value;

import java.util.List;
import java.util.Set;

public class SwitchNode extends Node {
	private final Node valueNode;
	private final Node defaultNode;
	private final List<Set<Node>> nodeCases;
	private final List<ArucasSet> valueCases;
	private final List<Node> statements;

	public SwitchNode(Node valueNode, Node defaultNode, List<Set<Node>> nodeCases, List<ArucasSet> valueCases, List<Node> statements, ISyntax startPos, ISyntax endPos) {
		super(new Token(Token.Type.SWITCH, startPos, endPos));
		this.valueNode = valueNode;
		this.defaultNode = defaultNode;
		this.nodeCases = nodeCases;
		this.valueCases = valueCases;
		this.statements = statements;
	}

	@Override
	public Value<?> visit(Context context) throws CodeError, ThrowValue {
		context.pushSwitchScope(this.syntaxPosition);
		Value<?> value = this.valueNode.visit(context);

		try {
			for (int i = 0; i < this.nodeCases.size(); i++) {
				ArucasSet set = this.valueCases.get(i);
				if (set != null && set.contains(context, value)) {
					this.statements.get(i).visit(context);
					context.popScope();
					return NullValue.NULL;
				}

				Set<Node> nodes = this.nodeCases.get(i);
				if (nodes == null) {
					continue;
				}
				for (Node node : nodes) {
					Value<?> nodeValue = node.visit(context);
					if (!value.isEquals(context, nodeValue)) {
						continue;
					}
					this.statements.get(i).visit(context);
					context.popScope();
					return NullValue.NULL;
				}
			}

			if (this.defaultNode != null) {
				this.defaultNode.visit(context);
			}
		}
		catch (ThrowValue.Break throwValue) {
			context.moveScope(context.getBreakScope());
		}

		context.popScope();
		return NullValue.NULL;
	}
}
