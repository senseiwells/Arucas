package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.values.classes.AbstractClassDefinition;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.Value;

public class StaticAssignNode extends Node {
	private final AbstractClassDefinition classDefinition;
	private final Node valueNode;

	public StaticAssignNode(Token token, AbstractClassDefinition classDefinition, Node valueNode) {
		super(token);
		this.classDefinition = classDefinition;
		this.valueNode = valueNode;
	}

	@Override
	public Value<?> visit(Context context) throws CodeError, ThrowValue {
		Value<?> newValue = this.valueNode.visit(context);

		if (!this.classDefinition.hasMember(this.token.content) || !this.classDefinition.setMember(this.token.content, newValue)) {
			throw new RuntimeError(
				"The class '%s' does not contain the member variable '%s'".formatted(this.classDefinition.getName(), this.token.content),
				this.syntaxPosition,
				context
			);
		}

		return newValue;
	}
}
