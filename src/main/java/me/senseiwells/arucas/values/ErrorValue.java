package me.senseiwells.arucas.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.ArucasRuntimeError;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.functions.BuiltInFunction;
import me.senseiwells.arucas.values.functions.ConstructorFunction;
import me.senseiwells.arucas.values.functions.MemberFunction;

import java.util.List;

public class ErrorValue extends Value<Value<?>> {
	private final RuntimeError runtimeError;

	public ErrorValue(String details, ISyntax syntaxPosition, Context context, Value<?> value) {
		super(value);
		this.runtimeError = new ArucasRuntimeError(details, syntaxPosition, context, this);
	}

	public ErrorValue(RuntimeError runtimeError) {
		super(NullValue.NULL);
		this.runtimeError = runtimeError;
	}

	public RuntimeError getRuntimeError() {
		return this.runtimeError;
	}

	@Override
	public String getAsString(Context context) throws CodeError {
		return this.runtimeError.toString(context, true);
	}

	@Override
	public int getHashCode(Context context) throws CodeError {
		return System.identityHashCode(this);
	}

	@Override
	public boolean isEquals(Context context, Value<?> other) throws CodeError {
		return other == this;
	}

	@Override
	public String getTypeName() {
		return "Error";
	}

	@Override
	public Value<Value<?>> copy(Context context) throws CodeError {
		return this;
	}

	@Override
	public RuntimeError asJavaValue() {
		return this.runtimeError;
	}

	/**
	 * Error class for Arucas. <br>
	 * Fully Documented.
	 * @author senseiwells
	 */
	public static class ArucasErrorClass extends ArucasClassExtension {
		public ArucasErrorClass() {
			super("Error");
		}

		@Override
		public Class<? extends BaseValue> getValueClass() {
			return ErrorValue.class;
		}

		@Override
		public ArucasFunctionMap<ConstructorFunction> getDefinedConstructors() {
			return ArucasFunctionMap.of(
				new ConstructorFunction(this::newError),
				new ConstructorFunction(List.of("details"), this::newError1),
				new ConstructorFunction(List.of("details", "value"), this::newError2)
			);
		}

		/**
		 * Name: <code>new Error()</code> <br>
		 * Description: This creates a new Error object with no message <br>
		 * Returns - Error: the new Error object <br>
		 * Example: <code>new Error();</code>
		 */
		private Value<?> newError(Context context, BuiltInFunction function) {
			return new ErrorValue("", function.syntaxPosition, context, NullValue.NULL);
		}

		/**
		 * Name: <code>new Error(details)</code> <br>
		 * Description: This creates a new Error object with the given details as a message <br>
		 * Parameter - String: the details of the error <br>
		 * Returns - Error: the new Error object <br>
		 * Example: <code>new Error("This is an error");</code>
		 */
		private Value<?> newError1(Context context, BuiltInFunction function) throws CodeError {
			StringValue details = function.getParameterValueOfType(context, StringValue.class, 0);
			return new ErrorValue(details.value, function.syntaxPosition, context, NullValue.NULL);
		}

		/**
		 * Name: <code>new Error(details, value)</code> <br>
		 * Description: This creates a new Error object with the given details as a message and the given value <br>
		 * Parameter - String, Value: the details of the error, the value that is related to the error <br>
		 * Returns - Error: the new Error object <br>
		 * Example: <code>new Error("This is an error", "object");</code>
		 */
		private Value<?> newError2(Context context, BuiltInFunction function) throws CodeError {
			StringValue details = function.getParameterValueOfType(context, StringValue.class, 0);
			Value<?> value = function.getParameterValue(context, 1);
			return new ErrorValue(details.value, function.syntaxPosition, context, value);
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				new MemberFunction("getFormattedDetails", this::getFormattedDetails),
				new MemberFunction("getDetails", this::getDetails),
				new MemberFunction("getValue", this::getValue)
			);
		}

		/**
		 * Name: <code>&lt;Error>.getFormattedDetails()</code> <br>
		 * Description: This returns the message of the error in a formatted string <br>
		 * Returns - String: the details of the error <br>
		 * Example: <code>new Error("Error!").getFormattedDetails();</code>
		 */
		private Value<?> getFormattedDetails(Context context, MemberFunction function) throws CodeError {
			ErrorValue error = function.getThis(context, ErrorValue.class);
			return StringValue.of(error.runtimeError.toString(context));
		}

		/**
		 * Name: <code>&lt;Error>.getDetails()</code> <br>
		 * Description: This returns the raw message of the error <br>
		 * Returns - String: the details of the error <br>
		 * Example: <code>new Error("Error!").getDetails();</code>
		 */
		private Value<?> getDetails(Context context, MemberFunction function) throws CodeError {
			ErrorValue error = function.getThis(context, ErrorValue.class);
			return StringValue.of(error.runtimeError.toString(context, true));
		}

		/**
		 * Name: <code>&lt;Error>.getValue()</code> <br>
		 * Description: This returns the value that is related to the error <br>
		 * Returns - Value: the value that is related to the error <br>
		 * Example: <code>new Error("Error!", "object").getValue();</code>
		 */
		private Value<?> getValue(Context context, MemberFunction function) throws CodeError {
			ErrorValue error = function.getThis(context, ErrorValue.class);
			return error.value;
		}
	}
}
