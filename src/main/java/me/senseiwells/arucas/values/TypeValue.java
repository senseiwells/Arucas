package me.senseiwells.arucas.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
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

import static me.senseiwells.arucas.utils.ValueTypes.*;

public class TypeValue extends GenericValue<AbstractClassDefinition> {
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
	public boolean isEquals(Context context, Value other) throws CodeError {
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
	
	@ClassDoc(
		name = ValueTypes.TYPE,
		desc = "This class lets you get the type of a class or value."
	)
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

		@FunctionDoc(
			isStatic = true,
			name = "of",
			desc = "This gets the specific type of a value",
			params = {ANY, "value", "the value you want to get the type of"},
			returns = {TYPE, "the type of the value"},
			example = "Type.of(0);"
		)
		private Value of(Context context, BuiltInFunction function) throws CodeError {
			Value object = function.getParameterValue(context, 0);
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

		@FunctionDoc(
			name = "instanceOf",
			desc = "This checks whether a type is a subtype of another type",
			params = {TYPE, "type", "the other type you want to check against"},
			returns = {BOOLEAN, "whether the type is of that type"},
			example = "Type.of('').instanceOf(Number.type);"
		)
		private Value instanceOf(Context context, MemberFunction function) throws CodeError {
			TypeValue thisValue = function.getThis(context, TypeValue.class);
			TypeValue otherType = function.getParameterValueOfType(context, TypeValue.class, 1);

			if (thisValue.value instanceof ArucasClassDefinition definition) {
				return BooleanValue.of(definition == otherType.value);
			}
			return BooleanValue.of(otherType.value.getValueClass().isAssignableFrom(thisValue.getClass()));
		}

		@FunctionDoc(
			name = "getName",
			desc = "This gets the name of the type",
			returns = {STRING, "the name of the type"},
			example = "String.type.getName();"
		)
		private Value getName(Context context, MemberFunction function) throws CodeError {
			TypeValue thisValue = function.getThis(context, TypeValue.class);
			return StringValue.of(thisValue.value.getName());
		}

		@FunctionDoc(
			name = "getConstructor",
			desc = "This gets the constructor of the type",
			params = {NUMBER, "parameters", "the number of parameters for the constructor"},
			returns = {FUNCTION, "the constructor of the type"},
			example = "String.type.getConstructor(0);"
		)
		private Value getConstructor(Context context, MemberFunction function) throws CodeError {
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

		@FunctionDoc(
			name = "getStaticMethod",
			desc = "This gets the static method of the type",
			params = {
				STRING, "name", "the name of the method", 
				NUMBER, "parameters", "the number of parameters for the method"
			},
			returns = {FUNCTION, "the static method of the type"},
			example = "String.type.getStaticMethod('nonExistent', 0);"
		)
		private Value getStaticMethod(Context context, MemberFunction function) throws CodeError {
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
