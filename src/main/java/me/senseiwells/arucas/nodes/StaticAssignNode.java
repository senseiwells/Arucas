package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.classes.AbstractClassDefinition;

public class StaticAssignNode extends VariableAssignNode {
	private final AbstractClassDefinition classDefinition;

	public StaticAssignNode(Token token, AbstractClassDefinition classDefinition, Node valueNode) {
		super(token, valueNode);
		this.classDefinition = classDefinition;
	}

	@Override
	public Value visit(Context context) throws CodeError, ThrowValue {
		Value newValue = this.getNewValue(context);

		if (!this.classDefinition.hasMember(this.token.content) || !this.classDefinition.setMember(this.token.content, newValue)) {
			throw new RuntimeError(
				"The member '%s' cannot be set for '%s'".formatted(this.token.content, this.classDefinition.getName()),
				this.syntaxPosition,
				context
			);
		}

		return newValue;
	}
}
