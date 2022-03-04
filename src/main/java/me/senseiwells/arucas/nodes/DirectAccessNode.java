package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.Value;

public class DirectAccessNode<T extends Value<?>> extends Node {
	private final T value;

	public DirectAccessNode(Token token, T value) {
		super(token);
		this.value = value;
	}

	public T getValue() {
		return this.value;
	}

	@Override
	public T visit(Context context) throws CodeError, ThrowValue {
		return this.value;
	}
}
