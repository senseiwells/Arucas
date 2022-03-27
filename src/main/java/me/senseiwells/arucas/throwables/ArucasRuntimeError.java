package me.senseiwells.arucas.throwables;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.ErrorValue;

public class ArucasRuntimeError extends RuntimeError {
	private final ErrorValue errorValue;

	public ArucasRuntimeError(String details, ISyntax syntaxHolder, Context context, ErrorValue errorValue) {
		super(details, syntaxHolder, context);
		this.errorValue = errorValue;
	}

	public ErrorValue getErrorValue() {
		return this.errorValue;
	}
}
