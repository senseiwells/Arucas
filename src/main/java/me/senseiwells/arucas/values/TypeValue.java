package me.senseiwells.arucas.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.ValueTypes;
import me.senseiwells.arucas.values.classes.AbstractClassDefinition;
import me.senseiwells.arucas.values.classes.ArucasClassDefinition;
import me.senseiwells.arucas.values.functions.BuiltInFunction;
import me.senseiwells.arucas.values.functions.FunctionValue;
import me.senseiwells.arucas.values.functions.MemberFunction;

import java.util.List;

public class TypeValue extends Value<AbstractClassDefinition> {
	public TypeValue(AbstractClassDefinition definition) {
		super(definition);
	}

	@Override
	public String getAsString(Context context) throws CodeError {
		return "<Type - " + this.value.getName() + ">";
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
	public TypeValue copy(Context context) throws CodeError {
		return this;
	}

	@Override
	public String getTypeName() {
		return ValueTypes.TYPE;
	}

	/**
	 * Type class for Arucas. <br>
	 * Fully Documented.
	 * @author senseiwells
	 */
	public static class ArucasTypeClass extends ArucasClassExtension {
		public ArucasTypeClass() {
			super(ValueTypes.TYPE);
		}

		@Override
		public ArucasFunctionMap<BuiltInFunction> getDefinedStaticMethods() {
			return ArucasFunctionMap.of(
				new BuiltInFunction("of", "object", this::of)
			);
		}

		/**
		 * Name: <code>Type.of(value)</code> <br>
		 * Description: This gets the specific type of a value <br>
		 * Parameter - Value: the value you want to get the type of <br>
		 * Returns - Type: the type of the value <br>
		 * Example: <code>Type.of(0);</code> <br>
		 */
		private Value<?> of(Context context, BuiltInFunction function) throws CodeError {
			Value<?> object = function.getParameterValue(context, 0);
			return object.getType(context, function.syntaxPosition);
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				new MemberFunction("instanceOf", "type", this::instanceOf),
				new MemberFunction("getName", this::getName),
				new MemberFunction("getConstructor", "parameters", this::getConstructor),
				new MemberFunction("getStaticMethod", List.of("name", "parameters"), this::getStaticMethod)
			);
		}

		/**
		 * Name: <code>&lt;Type>.instanceOf(type)</code> <br>
		 * Description: This checks whether a type is a subtype of another type <br>
		 * Parameter - Type: the other type you want to check against <br>
		 * Returns - Boolean: whether the type is of that type <br>
		 * Example: <code>Type.of("").instanceOf(Type.of(0));</code> <br>
		 */
		private Value<?> instanceOf(Context context, MemberFunction function) throws CodeError {
			TypeValue thisValue = function.getThis(context, TypeValue.class);
			TypeValue otherType = function.getParameterValueOfType(context, TypeValue.class, 1);

			if (thisValue.value instanceof ArucasClassDefinition definition) {
				return BooleanValue.of(definition == otherType.value);
			}
			return BooleanValue.of(otherType.value.getValueClass().isAssignableFrom(thisValue.getClass()));
		}

		/**
		 * Name: <code>&lt;Type>.getName()</code> <br>
		 * Description: This gets the name of the type <br>
		 * Returns - String: the name of the type <br>
		 * Example: <code>String.type.getName();</code> <br>
		 */
		private Value<?> getName(Context context, MemberFunction function) throws CodeError {
			TypeValue thisValue = function.getThis(context, TypeValue.class);
			return StringValue.of(thisValue.value.getName());
		}

		/**
		 * Name: <code>&lt;Type>.getConstructor(parameters)</code> <br>
		 * Description: This gets the constructor of the type <br>
		 * Parameter - Number: the number of parameters for the constructor <br>
		 * Returns - Function: the constructor of the type <br>
		 * Example: <code>String.type.getConstructor(0);</code> <br>
		 */
		private Value<?> getConstructor(Context context, MemberFunction function) throws CodeError {
			TypeValue thisValue = function.getThis(context, TypeValue.class);
			StringValue methodNameValue = function.getParameterValueOfType(context, StringValue.class, 1);
			NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 2);

			FunctionValue delegate = thisValue.value.getConstructors().get(methodNameValue.value, numberValue.value.intValue());
			if (delegate == null) {
				throw new RuntimeError(
					"No such method '%s' with %d parameters".formatted(methodNameValue.value, numberValue.value.intValue()),
					function.syntaxPosition,
					context
				);
			}
			return delegate;
		}

		/**
		 * Name: <code>&lt;Type>.getStaticMethod(name, parameters)</code> <br>
		 * Description: This gets the static method of the type <br>
		 * Parameter - String: the name of the method <br>
		 * Parameter - Number: the number of parameters for the method <br>
		 * Returns - Function: the static method of the type <br>
		 * Example: <code>String.type.getStaticMethod("nonExistent", 0);</code> <br>
		 */
		private Value<?> getStaticMethod(Context context, MemberFunction function) throws CodeError {
			TypeValue typeValue = function.getThis(context, TypeValue.class);
			StringValue methodNameValue = function.getParameterValueOfType(context, StringValue.class, 1);
			NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 2);

			FunctionValue delegate = typeValue.value.getMember(methodNameValue.value, numberValue.value.intValue());
			if (delegate == null) {
				throw new RuntimeError(
					"No such method '%s' with %d parameters".formatted(methodNameValue.value, numberValue.value.intValue()),
					function.syntaxPosition,
					context
				);
			}
			return delegate;
		}

		@Override
		public Class<TypeValue> getValueClass() {
			return TypeValue.class;
		}
	}
}
