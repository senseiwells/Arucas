package me.senseiwells.arucas.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
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
		return "Type";
	}

	public static class ArucasTypeClass extends ArucasClassExtension {
		public ArucasTypeClass() {
			super("Type");
		}

		@Override
		public ArucasFunctionMap<BuiltInFunction> getDefinedStaticMethods() {
			return ArucasFunctionMap.of(
				new BuiltInFunction("of", "object", this::of)
			);
		}

		private Value<?> of(Context context, BuiltInFunction function) throws CodeError {
			Value<?> object = function.getParameterValue(context, 0);
			return object.getType(context, function.syntaxPosition);
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				new MemberFunction("instanceOf", "type", this::instanceOf),
				new MemberFunction("hasEmbed", "embedType", this::hasEmbed),
				new MemberFunction("getName", this::getName),
				new MemberFunction("getConstructor", "parameters", this::getConstructor),
				new MemberFunction("getStaticMethod", List.of("name", "parameters"), this::getStaticMethod)
			);
		}

		private Value<?> instanceOf(Context context, MemberFunction function) throws CodeError {
			TypeValue thisValue = function.getThis(context, TypeValue.class);
			TypeValue otherType = function.getParameterValueOfType(context, TypeValue.class, 1);

			if (thisValue.value instanceof ArucasClassDefinition definition) {
				return BooleanValue.of(definition == otherType.value);
			}
			return BooleanValue.of(otherType.value.getValueClass().isAssignableFrom(thisValue.getClass()));
		}

		private Value<?> hasEmbed(Context context, MemberFunction function) throws CodeError {
			TypeValue thisValue = function.getThis(context, TypeValue.class);
			TypeValue embedType = function.getParameterValueOfType(context, TypeValue.class, 1);

			if (thisValue.value instanceof ArucasClassDefinition definition) {
				return BooleanValue.of(definition.hasEmbeddedClass(embedType.value));
			}

			return BooleanValue.FALSE;
		}

		private Value<?> getName(Context context, MemberFunction function) throws CodeError {
			TypeValue thisValue = function.getThis(context, TypeValue.class);
			return StringValue.of(thisValue.value.getName());
		}

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
