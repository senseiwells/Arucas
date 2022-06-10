package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.Value;

public class VariableAssignNode extends Node {
	private final Node node;
	private Value newValue;
	private boolean isLocal;

	public VariableAssignNode(Token token, Node node) {
		super(token);
		this.node = node;
	}

	public VariableAssignNode(Token token, ISyntax start, ISyntax end, Node node) {
		super(token, start, end);
		this.node = node;
	}

	public void setValue(Value value) {
		this.newValue = value;
	}

	protected final Value getNewValue(Context context) throws ThrowValue, CodeError {
		return this.newValue != null ? this.newValue : this.node.visit(context);
	}

	public final void setLocal(boolean isLocal) {
		this.isLocal = isLocal;
	}

	@Override
	public Value visit(Context context) throws CodeError, ThrowValue {
		String name = this.token.content;
		context.throwIfStackNameTaken(name, this.syntaxPosition);

		Value value = this.getNewValue(context);

		if (this.isLocal) {
			if (!context.isExperimental()) {
				throw new RuntimeError(
					"Explicit local variable assigning is not supported in this version of Arucas",
					this.syntaxPosition,
					context
				);
			}
			context.setLocal(name, value);
			return value;
		}
		context.setVariable(name, value);
		return value;
	}
}
