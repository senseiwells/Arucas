package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.classes.AbstractClassDefinition;

public class StaticAccessNode extends Node {
	private final AbstractClassDefinition classDefinition;

	public StaticAccessNode(Token token, AbstractClassDefinition classDefinition) {
		super(token);
		this.classDefinition = classDefinition;
	}

	@Override
	public Value visit(Context context) throws CodeError, ThrowValue {
		Value staticValue = this.classDefinition.getMember(this.token.content);
		if (staticValue == null) {
			throw new RuntimeError("Static member variable '%s' was not defined for the value type '%s'".formatted(
				this.token.content,
				this.classDefinition.getName()
			), this.syntaxPosition, context);
		}
		return staticValue;
	}
}
