package me.senseiwells.arucas.extensions;

import me.senseiwells.arucas.api.IArucasValueExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.ArucasValueList;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.MemberFunction;

import java.util.*;

public class ArucasStringMembers implements IArucasValueExtension {

	@Override
	public Set<MemberFunction> getDefinedFunctions() {
		return this.stringFunctions;
	}
	
	@Override
	public Class<StringValue> getValueType() {
		return StringValue.class;
	}
	
	@Override
	public String getName() {
		return "StringMemberFunctions";
	}

	private final Set<MemberFunction> stringFunctions = Set.of(
		new MemberFunction("toList", this::stringToList),
		new MemberFunction("replaceAll", List.of("regex", "replace"), this::stringReplaceAll),
		new MemberFunction("uppercase", this::stringUppercase),
		new MemberFunction("lowercase", this::stringLowercase),
		new MemberFunction("toNumber", this::stringToNumber),
		new MemberFunction("formatted", "values", this::stringFormatted),
		new MemberFunction("containsString", "otherString", this::stringContainsString)
	);

	private Value<?> stringToList(Context context, MemberFunction function) throws CodeError {
		StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0);
		ArucasValueList stringList = new ArucasValueList();
		for (char c : stringValue.value.toCharArray()) {
			stringList.add(new StringValue(String.valueOf(c)));
		}
		return new ListValue(stringList);
	}

	private Value<?> stringReplaceAll(Context context, MemberFunction function) throws CodeError {
		StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0);
		StringValue remove = function.getParameterValueOfType(context, StringValue.class, 1);
		StringValue replace = function.getParameterValueOfType(context, StringValue.class, 2);
		return new StringValue(stringValue.value.replaceAll(remove.value, replace.value));
	}

	private Value<?> stringUppercase(Context context, MemberFunction function) throws CodeError {
		StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0);
		return new StringValue(stringValue.value.toUpperCase(Locale.ROOT));
	}

	private Value<?> stringLowercase(Context context, MemberFunction function) throws CodeError {
		StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0);
		return new StringValue(stringValue.value.toLowerCase(Locale.ROOT));
	}

	private Value<?> stringToNumber(Context context, MemberFunction function) throws CodeError {
		StringValue value = function.getParameterValueOfType(context, StringValue.class, 0);
		try {
			return new NumberValue(Double.parseDouble(value.value));
		}
		catch (NumberFormatException e) {
			throw new RuntimeError("Cannot parse %s as a NumberValue".formatted(value), function.syntaxPosition, context);
		}
	}

	private Value<?> stringFormatted(Context context, MemberFunction function) throws CodeError {
		String string = function.getParameterValueOfType(context, StringValue.class, 0).value;
		final Value<?>[] array = function.getParameterValueOfType(context, ListValue.class, 1).value.toArray(Value<?>[]::new);
		int i = 0;
		while (string.contains("%s")) {
			try {
				string = string.replaceFirst("%s", array[i].toString());
			}
			catch (IndexOutOfBoundsException e) {
				throw new RuntimeError("You are missing values to be formatted!", function.syntaxPosition, context);
			}
			i++;
		}
		return new StringValue(string);
	}

	private Value<?> stringContainsString(Context context, MemberFunction function) throws CodeError {
		String string = function.getParameterValueOfType(context, StringValue.class, 0).value;
		String otherString = function.getParameterValueOfType(context, StringValue.class, 1).value;
		return new BooleanValue(string.contains(otherString));
	}
}
