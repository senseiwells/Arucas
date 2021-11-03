package me.senseiwells.arucas.extensions;

import me.senseiwells.arucas.api.IArucasExtension;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.values.ListValue;
import me.senseiwells.arucas.values.NumberValue;
import me.senseiwells.arucas.values.StringValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.functions.MemberFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class ArucasStringMembers implements IArucasExtension {

	@Override
	public Set<MemberFunction> getDefinedFunctions() {
		return this.stringFunctions;
	}

	@Override
	public String getName() {
		return "StringMemberFunctions";
	}

	private final Set<MemberFunction> stringFunctions = Set.of(
		new MemberFunction("toList", (context, function) -> {
			StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0);
			List<Value<?>> stringList = new ArrayList<>();
			for (char c : stringValue.value.toCharArray()) {
				stringList.add(new StringValue(String.valueOf(c)));
			}
			return new ListValue(stringList);
		}),

		new MemberFunction("replaceAll", List.of("regex", "replace"), (context, function) -> {
			StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0);
			StringValue remove = function.getParameterValueOfType(context, StringValue.class, 1);
			StringValue replace = function.getParameterValueOfType(context, StringValue.class, 2);
			return new StringValue(stringValue.value.replaceAll(remove.value, replace.value));
		}),

		new MemberFunction("uppercase", (context, function) -> {
			StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0);
			return new StringValue(stringValue.value.toUpperCase(Locale.ROOT));
		}),

		new MemberFunction("lowercase", (context, function) -> {
			StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0);
			return new StringValue(stringValue.value.toLowerCase(Locale.ROOT));
		}),

		new MemberFunction("toNumber", (context, function) -> {
			StringValue value = function.getParameterValueOfType(context, StringValue.class, 0);
			try {
				return new NumberValue(Double.parseDouble(value.value));
			}
			catch (NumberFormatException e) {
				throw new RuntimeError("Cannot parse %s as a NumberValue".formatted(value), function.startPos, function.endPos, context);
			}
		})
	);
}
