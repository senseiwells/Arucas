package me.senseiwells.arucas.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.ConstructorDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.throwables.ArucasRuntimeError;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.Arguments;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.functions.ConstructorFunction;
import me.senseiwells.arucas.values.functions.MemberFunction;

import static me.senseiwells.arucas.utils.ValueTypes.*;

public class ErrorValue extends GenericValue<Value> {
	private final RuntimeError runtimeError;

	public ErrorValue(String details, ISyntax syntaxPosition, Context context, Value value) {
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
	public boolean isEquals(Context context, Value other) throws CodeError {
		return other == this;
	}

	@Override
	public String getTypeName() {
		return ERROR;
	}

	@Override
	public GenericValue<Value> copy(Context context) throws CodeError {
		return this;
	}

	@Override
	public RuntimeError asJavaValue() {
		return this.runtimeError;
	}

	@ClassDoc(
		name = ERROR,
		desc = "This class is the only type that can be thrown"
	)
	public static class ArucasErrorClass extends ArucasClassExtension {
		public ArucasErrorClass() {
			super(ERROR);
		}

		@Override
		public Class<? extends Value> getValueClass() {
			return ErrorValue.class;
		}

		@Override
		public ArucasFunctionMap<ConstructorFunction> getDefinedConstructors() {
			return ArucasFunctionMap.of(
				ConstructorFunction.of(this::newError),
				ConstructorFunction.of(1, this::newError1),
				ConstructorFunction.of(2, this::newError2)
			);
		}

		@ConstructorDoc(
			desc = "This creates a new Error value with no message",
			example = "new Error();"
		)
		private Value newError(Arguments arguments) {
			return new ErrorValue("", arguments.getPosition(), arguments.getContext(), NullValue.NULL);
		}

		@ConstructorDoc(
			desc = "This creates a new Error value with the given details as a message",
			params = {STRING, "details", "the details of the error"},
			example = "new Error('This is an error');"
		)
		private Value newError1(Arguments arguments) throws CodeError {
			StringValue details = arguments.getNext(StringValue.class);
			return new ErrorValue(details.value, arguments.getPosition(), arguments.getContext(), NullValue.NULL);
		}

		@ConstructorDoc(
			desc = "This creates a new Error value with the given details as a message and the given value",
			params = {
				STRING, "details", "the details of the error",
				ANY, "value", "the value that is related to the error"
			},
			example = "new Error('This is an error', [1, 2, 3]);"
		)
		private Value newError2(Arguments arguments) throws CodeError {
			StringValue details = arguments.getNext(StringValue.class);
			Value value = arguments.getNext();
			return new ErrorValue(details.value, arguments.getPosition(), arguments.getContext(), value);
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				MemberFunction.of("getFormattedDetails", this::getFormattedDetails),
				MemberFunction.of("getDetails", this::getDetails),
				MemberFunction.of("getValue", this::getValue)
			);
		}

		@FunctionDoc(
			name = "getFormattedDetails",
			desc = "This returns the message of the error in a formatted string",
			returns = {STRING, "the details of the error"},
			example = "error.getFormattedDetails();"
		)
		private Value getFormattedDetails(Arguments arguments) throws CodeError {
			ErrorValue error = arguments.getNext(ErrorValue.class);
			return StringValue.of(error.runtimeError.toString(arguments.getContext()));
		}

		@FunctionDoc(
			name = "getDetails",
			desc = "This returns the raw message of the error",
			returns = {STRING, "the details of the error"},
			example = "error.getDetails();"
		)
		private Value getDetails(Arguments arguments) throws CodeError {
			ErrorValue error = arguments.getNext(ErrorValue.class);
			return StringValue.of(error.runtimeError.toString(arguments.getContext(), true));
		}

		@FunctionDoc(
			name = "getValue",
			desc = "This returns the value that is related to the error",
			returns = {ANY, "the value that is related to the error"},
			example = "error.getValue();"
		)
		private Value getValue(Arguments arguments) throws CodeError {
			ErrorValue error = arguments.getNext(ErrorValue.class);
			return error.value;
		}
	}
}
