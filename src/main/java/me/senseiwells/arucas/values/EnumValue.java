package me.senseiwells.arucas.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Arguments;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.values.classes.ArucasClassDefinition;
import me.senseiwells.arucas.values.classes.ArucasClassValue;
import me.senseiwells.arucas.values.functions.FunctionValue;
import me.senseiwells.arucas.values.functions.MemberFunction;

import static me.senseiwells.arucas.utils.ValueTypes.*;

public class EnumValue extends ArucasClassValue {
	private final String enumName;
	private final int ordinal;

	public EnumValue(ArucasClassDefinition arucasClass, String name, int ordinal) {
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
			return memberFunction.call(context, ArucasList.arrayListOf(this)).getAsString(context);
		}

		return "<enum " + this.getName() + " - " + this.getEnumName() + ">";
	}

	@Override
	public boolean isEquals(Context context, Value other) throws CodeError {
		return this == other;
	}

	@ClassDoc(
		name = ENUM,
		desc = "All enums extends this class."
	)
	public static class ArucasEnumClass extends ArucasClassExtension {
		public ArucasEnumClass() {
			super(ENUM);
		}

		@Override
		public Class<EnumValue> getValueClass() {
			return EnumValue.class;
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				MemberFunction.of("getName", this::getName),
				MemberFunction.of("ordinal", this::ordinal)
			);
		}

		@FunctionDoc(
			name = "getName",
			desc = "This allows you to get the name of the enum value",
			returns = {STRING, "the name of the enum value"},
			example = "enum.getName();"
		)
		private Value getName(Arguments arguments) throws CodeError {
			EnumValue enumValue = arguments.getNext(EnumValue.class);
			return StringValue.of(enumValue.enumName);
		}

		@FunctionDoc(
			name = "ordinal",
			desc = "This allows you to get the ordinal of the enum value",
			returns = {NUMBER, "the ordinal of the enum value"},
			example = "enum.ordinal();"
		)
		private Value ordinal(Arguments arguments) throws CodeError {
			EnumValue enumValue = arguments.getNext(EnumValue.class);
			return NumberValue.of(enumValue.ordinal);
		}
	}
}
