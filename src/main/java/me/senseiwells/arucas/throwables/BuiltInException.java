package me.senseiwells.arucas.throwables;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.utils.Context;

/**
 * This Exception type can be thrown inside built-in
 * functions without context or syntax position and
 * will be added outside the function call.
 * <p>
 * This is mostly useful when inside of wrapper
 * classes, or methods where syntax position and/or
 * context is not available.
 */
public class BuiltInException extends RuntimeException {
	public BuiltInException(String message) {
		super(message);
	}

	public RuntimeError asRuntimeError(Context context, ISyntax syntaxPosition) {
		return new RuntimeError(this.getMessage(), syntaxPosition, context);
	}

	@Override
	public synchronized Throwable fillInStackTrace() {
		return this;
	}
}
