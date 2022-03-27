package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.ErrorValue;
import me.senseiwells.arucas.values.Value;

public class ThrowNode extends Node {
	private final Node throwExpression;

	public ThrowNode(Node expression, ISyntax startPos, ISyntax endPos) {
		super(new Token(Token.Type.THROW, startPos), startPos, endPos);
		this.throwExpression = expression;
	}

	@Override
	public Value<?> visit(Context context) throws CodeError, ThrowValue {
		this.keepRunning();

		Value<?> throwable = this.throwExpression.visit(context);
		if (throwable instanceof ErrorValue errorValue) {
			throw errorValue.getRuntimeError();
		}
		throw new RuntimeError(
			"Tried to throw a non Error value '%s'".formatted(throwable.getAsString(context)),
			this.throwExpression.syntaxPosition,
			context
		);
	}
}
