package me.senseiwells.arucas.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.Arguments;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.classes.AbstractClassDefinition;
import me.senseiwells.arucas.values.classes.ArucasClassValue;
import me.senseiwells.arucas.values.functions.MemberFunction;

import static me.senseiwells.arucas.utils.ValueTypes.*;

public abstract class GenericValue<T> extends Value {
	public final T value;

	public GenericValue(T value) {
		this.value = value;
	}

	@Override
	public abstract GenericValue<T> copy(Context context) throws CodeError;

	@Override
	public GenericValue<T> newCopy(Context context) throws CodeError {
		return this.copy(context);
	}

	@Override
	public final T getValue() {
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
		public Class<? extends Value> getValueClass() {
			return GenericValue.class;
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				MemberFunction.of("instanceOf", 1, this::instanceOf),
				MemberFunction.of("getValueType", this::getValueType, "Use 'Type.of(<Value>).getName()'"),
				MemberFunction.of("copy", this::newCopy),
				MemberFunction.of("hashCode", this::hashCode),
				MemberFunction.of("uniqueHash", this::uniqueHash),
				MemberFunction.of("equals", 1, this::equals, "Use the '==' operator"),
				MemberFunction.of("compareTo", 1, this::compareTo),
				MemberFunction.of("toString", this::toString)
			);
		}

		@FunctionDoc(
			name = "instanceOf",
			desc = "This checks whether this value is an instance of another type",
			params = {TYPE, "type", "the other type you want to check against"},
			returns = {BOOLEAN, "whether the value is of that type"},
			example = "10.instanceOf(String.type);"
		)
		private Value instanceOf(Arguments arguments) throws RuntimeError {
			Value thisValue = arguments.getNext();
			Value ofValue = arguments.getNext();

			AbstractClassDefinition classDefinition = null;
			if (ofValue instanceof StringValue stringValue) {
				classDefinition = arguments.getContext().getClassDefinition(stringValue.value);

			}
			else if (ofValue instanceof TypeValue typeValue) {
				classDefinition = typeValue.value;
			}

			if (classDefinition == null) {
				return BooleanValue.FALSE;
			}
			if (thisValue instanceof ArucasClassValue classValue) {
				if (classValue.value == classDefinition) {
					return BooleanValue.TRUE;
				}
				return BooleanValue.of(classValue.value.getCastMethod(classDefinition) != null);
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
		private Value getValueType(Arguments arguments) throws RuntimeError {
			Value thisValue = arguments.getNext();
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
		private Value newCopy(Arguments arguments) throws CodeError {
			Value thisValue = arguments.getNext();
			return thisValue.newCopy(arguments.getContext());
		}

		@FunctionDoc(
			name = "hashCode",
			desc = "This returns the hashcode of the value, this is mainly used for maps and sets",
			returns = {NUMBER, "the hashcode of the value"},
			example = "'thing'.hashCode();"
		)
		private NumberValue hashCode(Arguments arguments) throws CodeError {
			Value thisValue = arguments.getNext();
			return NumberValue.of(thisValue.getHashCode(arguments.getContext()));
		}

		@FunctionDoc(
			name = "uniqueHash",
			desc = "This returns the unique hashcode of the value, this is different for every instance of a value",
			returns = {NUMBER, "the unique hashcode of the value"},
			example = "'thing'.uniqueHash();"
		)
		private NumberValue uniqueHash(Arguments arguments) throws CodeError {
			Value thisValue = arguments.getNext();
			return NumberValue.of(System.identityHashCode(thisValue));
		}

		@FunctionDoc(
			deprecated = "You should use '=='",
			name = "equals",
			desc = "This checks whether the value is equal to another value",
			params = {ANY, "other", "the other value you want to check against"},
			returns = {BOOLEAN, "whether the values are equal"},
			example = "10.equals(20);"
		)
		private BooleanValue equals(Arguments arguments) throws CodeError {
			Value thisValue = arguments.getNext();
			Value otherValue = arguments.getNext();
			return BooleanValue.of(thisValue.isEquals(arguments.getContext(), otherValue));
		}

		@FunctionDoc(
			name = "compareTo",
			desc = {
				"This compares the value to another value",
				"returns -1 if this is less than the other value, 0 if they are equal, 1 if this is greater than the other value"
			},
			params = {ANY, "other", "the other value you want to compare against"},
			returns = {NUMBER, "the result of the comparison"},
			example = "10.compareTo(20);"
		)
		private NumberValue compareTo(Arguments arguments) throws CodeError {
			Value thisValue = arguments.getNext();
			Value otherValue = arguments.getNext();
			return NumberValue.of(thisValue.compareTo(arguments.getContext(), otherValue));
		}

		@FunctionDoc(
			name = "toString",
			desc = "This returns the string representation of the value",
			returns = {STRING, "the string representation of the value"},
			example = "[10, 11, 12].toString();"
		)
		private StringValue toString(Arguments arguments) throws CodeError {
			Value thisValue = arguments.get(0);
			return StringValue.of(thisValue.getAsString(arguments.getContext()));
		}
	}
}
