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

		private Value<?> newError(Context context, BuiltInFunction function) {
			return new ErrorValue("", function.syntaxPosition, context, NullValue.NULL);
		}

		private Value<?> newError1(Context context, BuiltInFunction function) throws CodeError {
			StringValue details = function.getParameterValueOfType(context, StringValue.class, 0);
			return new ErrorValue(details.value, function.syntaxPosition, context, NullValue.NULL);
		}

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

		private Value<?> getFormattedDetails(Context context, MemberFunction function) throws CodeError {
			ErrorValue error = function.getThis(context, ErrorValue.class);
			return StringValue.of(error.runtimeError.toString(context));
		}

		private Value<?> getDetails(Context context, MemberFunction function) throws CodeError {
			ErrorValue error = function.getThis(context, ErrorValue.class);
			return StringValue.of(error.runtimeError.toString(context, true));
		}

		private Value<?> getValue(Context context, MemberFunction function) throws CodeError {
			ErrorValue error = function.getThis(context, ErrorValue.class);
			return error.value;
		}
	}
}
