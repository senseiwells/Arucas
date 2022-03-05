package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.classes.ArucasClassDefinition;

public class ArucasClassNode extends Node {
	private final ArucasClassDefinition definition;

	public ArucasClassNode(ArucasClassDefinition definition, ISyntax startPos, ISyntax endPos) {
		super(new Token(Token.Type.CLASS, startPos, endPos));
		this.definition = definition;
	}

	@Override
	public Value<?> visit(Context context) throws CodeError, ThrowValue {
		// Class definition must come before statics
		context.addClassDefinition(this.definition);
		this.definition.init(context);
		return NullValue.NULL;
	}
}
