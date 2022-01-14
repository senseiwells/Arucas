package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.values.Value;

public class VariableAccessNode extends Node {
	public VariableAccessNode(Token token) {
		super(token);
	}

	@Override
	public Value<?> visit(Context context) throws CodeError {
		Value<?> value = context.getVariable(this.token.content);
		if (value == null) {
			throw new CodeError(CodeError.ErrorType.UNKNOWN_IDENTIFIER, "%s is not defined".formatted(this.token.content), this.syntaxPosition);
		}

		return value;
	}
}
