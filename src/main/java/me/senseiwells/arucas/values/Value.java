package me.senseiwells.arucas.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.classes.ArucasClassValue;
import me.senseiwells.arucas.values.functions.MemberFunction;

public abstract class Value<T> implements ValueIdentifier {
	public final T value;
	
	public Value(T value) {
		this.value = value;
	}
	
	// Shallow copy
	public abstract Value<T> copy();

	// Deep copy
	public Value<T> newCopy() {
		return this.copy();
	}

	public BooleanValue isAnd(Context context, Value<?> other, ISyntax syntaxPosition) throws CodeError {
		throw cannotApplyError(context, "AND", other, syntaxPosition);
	}

	public BooleanValue isOr(Context context, Value<?> other, ISyntax syntaxPosition) throws CodeError {
		throw cannotApplyError(context, "OR", other, syntaxPosition);
	}

	public Value<?> addTo(Context context, Value<?> other, ISyntax syntaxPosition) throws CodeError {
		throw cannotApplyError(context, "ADD", other, syntaxPosition);
	}

	public Value<?> subtractBy(Context context, Value<?> other, ISyntax syntaxPosition) throws CodeError {
		throw cannotApplyError(context, "SUBTRACT", other, syntaxPosition);
	}

	public Value<?> multiplyBy(Context context, Value<?> other, ISyntax syntaxPosition) throws CodeError {
		throw cannotApplyError(context, "MULTIPLY", other, syntaxPosition);
	}

	public Value<?> divideBy(Context context, Value<?> other, ISyntax syntaxPosition) throws CodeError {
		throw cannotApplyError(context, "DIVIDE", other, syntaxPosition);
	}

	public Value<?> powerBy(Context context, Value<?> other, ISyntax syntaxPosition) throws CodeError {
		throw cannotApplyError(context, "POWER", other, syntaxPosition);
	}

	public BooleanValue compareNumber(Context context, Value<?> other, Token.Type type, ISyntax syntaxPosition) throws CodeError {
		throw cannotApplyError(context, type.toString(), other, syntaxPosition);
	}

	public BooleanValue not(Context context, ISyntax syntaxPosition) throws CodeError {
		throw new RuntimeError("The operation 'NOT' cannot be applied to %s".formatted(this.getAsString(context)), syntaxPosition, context);
	}

	public BooleanValue isEqualTo(Value<?> other) {
		return BooleanValue.of(this.equals(other));
	}

	public BooleanValue isNotEqualTo(Value<?> other) {
		return BooleanValue.of(!this.equals(other));
	}

	private RuntimeError cannotApplyError(Context context, String operation, Value<?> other, ISyntax syntaxPosition) throws CodeError {
		return new RuntimeError("The operation '%s' cannot be applied to %s and %s".formatted(
			operation,
			this.getAsString(context),
			other.getAsString(context)),
			syntaxPosition,
			context
		);
	}

	/**
	 * These methods should not be used instead
	 * {@link #getAsString(Context)},
	 * {@link #getHashCode(Context)},
	 * {@link #isEquals(Context, Value)},
	 * should be used
	 */
	@Deprecated
	@Override
	public final boolean equals(Object other) {
		return this == other;
	}

	@Deprecated
	@Override
	public final int hashCode() {
		return this.value.hashCode();
	}

	@Deprecated
	@Override
	public final String toString() {
		return this.value == null ? "null" : this.value.toString();
	}
	
	public static class ArucasBaseClass extends ArucasClassExtension {
		public ArucasBaseClass() {
			super("Object");
		}

		@Override
		public Class<?> getValueClass() {
			return Value.class;
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				new MemberFunction("instanceOf", "class", this::instanceOf),
				new MemberFunction("getValueType", this::getValueType),
				new MemberFunction("copy", this::newCopy),
				new MemberFunction("hashCode", this::hashCode),
				new MemberFunction("equals", "other", this::equals),
				new MemberFunction("toString", this::toString)
			);
		}

		private Value<?> instanceOf(Context context, MemberFunction function) throws CodeError {
			Value<?> thisValue = function.getParameterValue(context, 0);
			StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 1);
			if (stringValue.value.isEmpty()) {
				return BooleanValue.FALSE;
			}

			if (thisValue instanceof ArucasClassValue classValue) {
				return BooleanValue.of(classValue.getName().equals(stringValue.value));
			}

			Class<?> clazz = this.getClass();
			while (clazz != null && clazz != Object.class) {
				if (clazz.getSimpleName().replaceFirst("Value$", "").equals(stringValue.value)) {
					return BooleanValue.TRUE;
				}

				clazz = clazz.getSuperclass();
			}

			return BooleanValue.FALSE;
		}

		private Value<?> getValueType(Context context, MemberFunction function) {
			Value<?> thisValue = function.getParameterValue(context, 0);
			if (thisValue instanceof ArucasClassValue classValue) {
				return StringValue.of(classValue.getName());
			}

			String valueType = this.getClass().getSimpleName().replaceFirst("Value$", "");
			return StringValue.of(valueType);
		}

		private Value<?> newCopy(Context context, MemberFunction function) {
			Value<?> thisValue = function.getParameterValue(context, 0);
			return thisValue.newCopy();
		}

		private NumberValue hashCode(Context context, MemberFunction function) throws CodeError {
			Value<?> thisValue = function.getParameterValue(context, 0);
			return NumberValue.of(thisValue.getHashCode(context));
		}

		private BooleanValue equals(Context context, MemberFunction function) throws CodeError {
			Value<?> thisValue = function.getParameterValue(context, 0);
			Value<?> otherValue = function.getParameterValue(context, 1);
			return BooleanValue.of(thisValue.isEquals(context, otherValue));
		}

		private StringValue toString(Context context, MemberFunction function) throws CodeError {
			Value<?> thisValue = function.getParameterValue(context, 0);
			return StringValue.of(thisValue.getAsString(context));
		}
	}
}
