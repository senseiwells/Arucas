package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.extensions.util.LazyValue;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.Value;

public class LazyNode extends Node {
	private final Node statements;

	public LazyNode(Token token, Node statements) {
		super(token);
		this.statements = statements;
	}

	@Override
	public Value visit(Context context) throws CodeError {
		Context branch = context.createBranch();
		return LazyValue.of(() -> {
			try {
				return this.statements.visitSafe(branch);
			}
			catch (ThrowValue.Return throwReturn) {
				return throwReturn.getReturnValue();
			}
		});
	}
}
