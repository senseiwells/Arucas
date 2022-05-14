package me.senseiwells.arucas.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.ValueTypes;
import me.senseiwells.arucas.values.classes.AbstractClassDefinition;
import me.senseiwells.arucas.values.classes.ArucasClassValue;
import me.senseiwells.arucas.values.functions.MemberFunction;

import static me.senseiwells.arucas.utils.ValueTypes.*;

public abstract class Value<T> extends BaseValue {
	public final T value;

	public Value(T value) {
		this.value = value;
	}

	@Override
	public abstract Value<T> copy(Context context) throws CodeError;

	@Override
	public Value<T> newCopy(Context context) throws CodeError {
		return this.copy(context);
	}

	@Override
	protected final T getValue() {
		return this.value;
	}

	@ClassDoc(
		name = "Object",
		desc = "This is the base class for every other class in Arucas."
	)
	public static class ArucasBaseClass extends ArucasClassExtension {
		public ArucasBaseClass() {
			super("Object");
		}

		@Override
		public Class<? extends BaseValue> getValueClass() {
			return Value.class;
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				new MemberFunction("instanceOf", "class", this::instanceOf),
				new MemberFunction("getValueType", this::getValueType, "Use 'Type.of(<Value>).getName()'"),
				new MemberFunction("copy", this::newCopy),
				new MemberFunction("hashCode", this::hashCode),
				new MemberFunction("equals", "other", this::equals, "Use the '==' operator"),
				new MemberFunction("toString", this::toString)
			);
		}


		@FunctionDoc(
			name = "instanceOf",
			desc = "This checks whether this value is an instance of another type",
			params = {TYPE, "type", "the other type you want to check against"},
			returns = {BOOLEAN, "whether the value is of that type"},
			example = "10.instanceOf(String.type);"
		)
		private Value<?> instanceOf(Context context, MemberFunction function) {
			Value<?> thisValue = function.getParameterValue(context, 0);
			Value<?> ofValue = function.getParameterValue(context, 1);

			AbstractClassDefinition classDefinition = null;
			if (ofValue instanceof StringValue stringValue) {
				classDefinition = context.getClassDefinition(stringValue.value);

			}
			else if (ofValue instanceof TypeValue typeValue) {
				classDefinition = typeValue.value;
			}

			if (classDefinition == null) {
				return BooleanValue.FALSE;
			}
			if (thisValue instanceof ArucasClassValue classValue) {
				return BooleanValue.of(classValue.value == classDefinition);
			}
			return BooleanValue.of(classDefinition.getValueClass().isAssignableFrom(thisValue.getClass()));
		}

		@FunctionDoc(
			deprecated = "You should use 'Type.of(<Value>).getName()'",
			name = "getValueType",
			desc = "This returns the name of the type of the value",
			returns = {STRING, "the name of the type of value"},
			example = "10.getValueType();"
		)
		@Deprecated
		private Value<?> getValueType(Context context, MemberFunction function) {
			Value<?> thisValue = function.getParameterValue(context, 0);
			if (thisValue instanceof ArucasClassValue classValue) {
				return StringValue.of(classValue.getName());
			}

			String valueType = thisValue.getTypeName();
			return StringValue.of(valueType);
		}

		@FunctionDoc(
			name = "copy",
			desc = "This returns a copy of the value, some values might just return themselves",
			returns = {ANY, "the copy of the value"},
			example = "10.copy();"
		)
		private Value<?> newCopy(Context context, MemberFunction function) throws CodeError {
			Value<?> thisValue = function.getParameterValue(context, 0);
			return thisValue.newCopy(context);
		}

		@FunctionDoc(
			name = "hashCode",
			desc = "This returns the hashcode of the value, this is mainly used for maps and sets",
			returns = {NUMBER, "the hashcode of the value"},
			example = "'thing'.hashCode();"
		)
		private NumberValue hashCode(Context context, MemberFunction function) throws CodeError {
			Value<?> thisValue = function.getParameterValue(context, 0);
			return NumberValue.of(thisValue.getHashCode(context));
		}

		@FunctionDoc(
			deprecated = "You should use '=='",
			name = "equals",
			desc = "This checks whether the value is equal to another value",
			params = {ANY, "other", "the other value you want to check against"},
			returns = {BOOLEAN, "whether the values are equal"},
			example = "10.equals(20);"
		)
		@Deprecated
		private BooleanValue equals(Context context, MemberFunction function) throws CodeError {
			Value<?> thisValue = function.getParameterValue(context, 0);
			Value<?> otherValue = function.getParameterValue(context, 1);
			return BooleanValue.of(thisValue.isEquals(context, otherValue));
		}

		@FunctionDoc(
			name = "toString",
			desc = "This returns the string representation of the value",
			returns = {STRING, "the string representation of the value"},
			example = "[10, 11, 12].toString();"
		)
		private StringValue toString(Context context, MemberFunction function) throws CodeError {
			Value<?> thisValue = function.getParameterValue(context, 0);
			return StringValue.of(thisValue.getAsString(context));
		}
	}
}
