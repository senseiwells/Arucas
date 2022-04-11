package me.senseiwells.arucas.values.classes;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.ExceptionUtils;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.Value;

import java.lang.invoke.MethodHandle;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class ArucasMethodHandle {
	private final MethodHandle methodHandle;
	private final ReturnType returnType;

	public ArucasMethodHandle(MethodHandle methodHandle, ReturnType returnType) {
		this.methodHandle = methodHandle;
		this.returnType = returnType == null ? ReturnType.VALUE : returnType;
	}

	public Value<?> call(Object[] args, ArucasClassValue thisValue, ISyntax syntaxPosition, Context context) throws CodeError {
		return invokeMethodHandle(() -> {
			switch (this.returnType) {
				case THIS -> {
					this.methodHandle.invokeWithArguments(args);
					if (thisValue == null) {
						throw new RuntimeError("Could not return 'this' in wrapper class", syntaxPosition, context);
					}
					return thisValue;
				}
				case VOID -> {
					this.methodHandle.invokeWithArguments(args);
					return NullValue.NULL;
				}
				default -> {
					return (Value<?>) this.methodHandle.invokeWithArguments(args);
				}
			}
		}, syntaxPosition, context);
	}

	public static <T extends Value<?>> T invokeMethodHandle(ExceptionUtils.ThrowableSupplier<T> supplier, ISyntax syntaxPosition, Context context) throws CodeError {
		try {
			return supplier.get();
		}
		catch (CodeError codeError) {
			throw codeError;
		}
		catch (ClassCastException e) {
			throw new RuntimeError(formatCastException(e.getMessage()), syntaxPosition, context);
		}
		catch (Throwable t) {
			String message = t.getMessage();
			message = message == null ? "" : message.strip();
			throw new RuntimeError(message, syntaxPosition, context);
		}
	}

	private static String formatCastException(String message) {
		String[] matches = Pattern.compile("[a-zA-Z]+(?=Value(?!\\.))")
			.matcher(message).results().map(MatchResult::group)
			.toArray(String[]::new);
		if (matches.length != 2) {
			return message;
		}
		return "Invalid parameter types: Expected: %s, Found: %s".formatted(matches[1], matches[0]);
	}

	public enum ReturnType {
		THIS,
		VOID,
		VALUE
	}
}
