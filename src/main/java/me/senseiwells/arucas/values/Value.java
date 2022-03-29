package me.senseiwells.arucas.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.classes.AbstractClassDefinition;
import me.senseiwells.arucas.values.classes.ArucasClassValue;
import me.senseiwells.arucas.values.functions.MemberFunction;

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

	/**
	 * Object class for Arucas. <br>
	 * Fully Documented.
	 * @author senseiwells
	 */
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


		/**
		 * Name: <code>&lt;Value>.instanceOf(type)</code> <br>
		 * Description: This checks whether a value is an instance of another type <br>
		 * Parameter - Type: the other type you want to check against <br>
		 * Returns - Boolean: whether the value is of that type <br>
		 * Example: <code>10.instanceOf(String.type);</code>
		 */
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

		/**
		 * Deprecated: You should use <code>Type.of(&lt;Value>).getName()</code> <br>
		 * Name: <code>&lt;Value>.getValueType()</code> <br>
		 * Description: This returns the name of the type of the value <br>
		 * Returns - Type: the type of the value <br>
		 * Example: <code>10.getValueType();</code>
		 */
		@Deprecated
		private Value<?> getValueType(Context context, MemberFunction function) {
			Value<?> thisValue = function.getParameterValue(context, 0);
			if (thisValue instanceof ArucasClassValue classValue) {
				return StringValue.of(classValue.getName());
			}

			String valueType = thisValue.getTypeName();
			return StringValue.of(valueType);
		}

		/**
		 * Name: <code>&lt;Value>.copy()</code> <br>
		 * Description: This returns a copy of the value, some values might just return themselves <br>
		 * Returns - Value: the copy of the value <br>
		 * Example: <code>10.copy();</code>
		 */
		private Value<?> newCopy(Context context, MemberFunction function) throws CodeError {
			Value<?> thisValue = function.getParameterValue(context, 0);
			return thisValue.newCopy(context);
		}

		/**
		 * Name: <code>&lt;Value>.hashCode()</code> <br>
		 * Description: This returns the hashcode of the value <br>
		 * Returns - Number: the hashcode of the value <br>
		 * Example: <code>"thing".hashCode();</code>
		 */
		private NumberValue hashCode(Context context, MemberFunction function) throws CodeError {
			Value<?> thisValue = function.getParameterValue(context, 0);
			return NumberValue.of(thisValue.getHashCode(context));
		}

		/**
		 * Deprecated: You should use <code>==</code> <br>
		 * Name: <code>&lt;Value>.equals(other)</code> <br>
		 * Description: This checks whether the value is equal to another value <br>
		 * Parameter - Value: the other value you want to check against <br>
		 * Returns - Boolean: whether the values are equal <br>
		 * Example: <code>10.equals(20);</code>
		 */
		@Deprecated
		private BooleanValue equals(Context context, MemberFunction function) throws CodeError {
			Value<?> thisValue = function.getParameterValue(context, 0);
			Value<?> otherValue = function.getParameterValue(context, 1);
			return BooleanValue.of(thisValue.isEquals(context, otherValue));
		}

		/**
		 * Name: <code>&lt;Value>.toString()</code> <br>
		 * Description: This returns the string representation of the value <br>
		 * Returns - String: the string representation of the value <br>
		 * Example: <code>[10, 11, 12].toString();</code>
		 */
		private StringValue toString(Context context, MemberFunction function) throws CodeError {
			Value<?> thisValue = function.getParameterValue(context, 0);
			return StringValue.of(thisValue.getAsString(context));
		}
	}
}
