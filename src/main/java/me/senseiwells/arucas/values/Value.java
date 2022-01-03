package me.senseiwells.arucas.values;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.classes.ArucasClassValue;
import me.senseiwells.arucas.values.functions.FunctionValue;
import me.senseiwells.arucas.values.functions.MemberFunction;
import me.senseiwells.arucas.values.functions.MemberOperations;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public abstract class Value<T> implements ValueOperations, MemberOperations {
	public final T value;
	public final Set<MemberFunction> memberFunctions;
	
	public Value(T value) {
		this.value = value;
		this.memberFunctions = this.getDefinedFunctions();
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
	public boolean isAssignable(String name) {
		return false;
	}

	@Override
	public boolean setMember(String name, Value<?> value) {
		return false;
	}

	@Override
	public Value<?> getMember(String name) {
		return null;
	}

	@Override
	public Iterable<? extends FunctionValue> getAllMembers() {
		return this.memberFunctions;
	}

	/**
	 * We only care about comparing the value not the position
	 * So overriding the equals and hashCode methods for maps
	 */
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


	protected Set<MemberFunction> getDefinedFunctions() {
		Set<MemberFunction> memberFunctions = new HashSet<>();
		memberFunctions.addAll(Set.of(
			new MemberFunction("instanceOf", "class", this::instanceOf),
			new MemberFunction("getValueType", this::getValueType),
			new MemberFunction("copy", (context, function) -> this.newCopy()),
			new MemberFunction("equals", "other", this::equals),
			new MemberFunction("toString", (context, function) -> new StringValue(this.getStringValue(context)))
		));
		return memberFunctions;
	}

	private Value<?> instanceOf(Context context, MemberFunction function) throws CodeError {
		StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0);
		if (stringValue.value.isEmpty()) {
			return BooleanValue.FALSE;
		}

		if (this instanceof ArucasClassValue classValue) {
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
		if (this instanceof ArucasClassValue classValue) {
			return new StringValue(classValue.getName());
		}

		String valueType = this.getClass().getSimpleName().replaceFirst("Value$", "");
		return new StringValue(valueType);
	}

	private BooleanValue equals(Context context, MemberFunction function) {
		Value<?> otherValue = function.getParameterValue(context, 0);
		return this.isEqual(otherValue);
	}
}
