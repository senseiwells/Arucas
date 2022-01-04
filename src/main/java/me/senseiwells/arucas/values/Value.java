package me.senseiwells.arucas.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.classes.ArucasClassValue;
import me.senseiwells.arucas.values.functions.MemberFunction;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public abstract class Value<T> implements ValueOperations {
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

	public String getStringValue(Context context) throws CodeError {
		return this.value.toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Value<?> otherValue)) {
			return false;
		}
		
		// Object.equals takes null values into perspective.
		return Objects.equals(this.value, otherValue.value);
	}

	@Override
	public int hashCode() {
		return this.value.hashCode();
	}

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

		private BooleanValue equals(Context context, MemberFunction function) {
			Value<?> thisValue = function.getParameterValue(context, 0);
			Value<?> otherValue = function.getParameterValue(context, 1);
			return thisValue.isEqual(otherValue);
		}

		private StringValue toString(Context context, MemberFunction function) throws CodeError {
			Value<?> thisValue = function.getParameterValue(context, 0);
			return StringValue.of(thisValue.getStringValue(context));
		}
	}
}
