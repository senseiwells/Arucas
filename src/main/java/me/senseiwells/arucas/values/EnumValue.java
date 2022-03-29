package me.senseiwells.arucas.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.classes.AbstractClassDefinition;
import me.senseiwells.arucas.values.classes.ArucasClassValue;
import me.senseiwells.arucas.values.functions.FunctionValue;
import me.senseiwells.arucas.values.functions.MemberFunction;

import java.util.ArrayList;

public class EnumValue extends ArucasClassValue {
	private final String enumName;
	private final int ordinal;

	public EnumValue(AbstractClassDefinition arucasClass, String name, int ordinal) {
		super(arucasClass);
		this.enumName = name;
		this.ordinal = ordinal;
	}

	public String getEnumName() {
		return this.enumName;
	}

	@Override
	public final int getHashCode(Context context) throws CodeError {
		return System.identityHashCode(this);
	}

	@Override
	public String getAsString(Context context) throws CodeError {
		// If 'toString' is overridden we should use that here
		FunctionValue memberFunction = this.getMember("toString", 1);
		if (memberFunction != null) {
			return memberFunction.call(context, new ArrayList<>()).getAsString(context);
		}

		return "<enum " + this.getName() + " - " + this.enumName + ">";
	}

	@Override
	public boolean isEquals(Context context, Value<?> other) throws CodeError {
		return this == other;
	}

	/**
	 * Enum class for Arucas. <br>
	 * Fully Documented.
	 * @author senseiwells
	 */
	public static class ArucasEnumClass extends ArucasClassExtension {
		public ArucasEnumClass() {
			super("Enum");
		}

		@Override
		public Class<EnumValue> getValueClass() {
			return EnumValue.class;
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				new MemberFunction("getName", this::getName),
				new MemberFunction("ordinal", this::ordinal)
			);
		}

		/**
		 * Name: <code>&lt;Enum>.getName()</code> <br>
		 * Description: this allows you to get the name of the enum value <br>
		 * Returns - String: the name of the enum value <br>
		 * Example: <code>enum.getName();</code>
		 */
		private Value<?> getName(Context context, MemberFunction function) throws CodeError {
			EnumValue enumValue = function.getThis(context, EnumValue.class);
			return StringValue.of(enumValue.enumName);
		}

		/**
		 * Name: <code>&lt;Enum>.ordinal()</code> <br>
		 * Description: this allows you to get the ordinal of the enum value <br>
		 * Returns - Number: the ordinal of the enum value <br>
		 * Example: <code>enum.ordinal();</code>
		 */
		private Value<?> ordinal(Context context, MemberFunction function) throws CodeError {
			EnumValue enumValue = function.getThis(context, EnumValue.class);
			return NumberValue.of(enumValue.ordinal);
		}
	}
}
