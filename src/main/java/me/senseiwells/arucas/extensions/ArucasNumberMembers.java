package me.senseiwells.arucas.extensions;

import me.senseiwells.arucas.api.IArucasExtension;
import me.senseiwells.arucas.values.NumberValue;
import me.senseiwells.arucas.values.functions.MemberFunction;

import java.util.Set;

public class ArucasNumberMembers implements IArucasExtension {

	@Override
	public Set<MemberFunction> getDefinedFunctions() {
		return this.numberFunctions;
	}

	@Override
	public String getName() {
		return "NumberMemberFunctions";
	}

	private final Set<MemberFunction> numberFunctions = Set.of(
		new MemberFunction("round", (context, function) -> {
			NumberValue numValue = function.getParameterValueOfType(context, NumberValue.class, 0);
			return new NumberValue(Math.round(numValue.value));
		}),

		new MemberFunction("roundUp", (context, function) -> {
			NumberValue numValue = function.getParameterValueOfType(context, NumberValue.class, 0);
			return new NumberValue(Math.ceil(numValue.value));
		}),

		new MemberFunction("roundDown", (context, function) -> {
			NumberValue numValue = function.getParameterValueOfType(context, NumberValue.class, 0);
			return new NumberValue(Math.floor(numValue.value));
		}),

		new MemberFunction("modulus", "otherNumber", (context, function) -> {
			NumberValue numberValue1 = function.getParameterValueOfType(context, NumberValue.class, 0);
			NumberValue numberValue2 = function.getParameterValueOfType(context, NumberValue.class, 1);
			return new NumberValue(numberValue1.value % numberValue2.value);
		})
	);
}
