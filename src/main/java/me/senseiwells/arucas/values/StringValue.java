package me.senseiwells.arucas.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.ArucasValueList;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.functions.MemberFunction;

import java.util.List;
import java.util.Locale;
import java.util.Set;

public class StringValue extends Value<String> {
	public StringValue(String value) {
		super(value);
	}

	@Override
	public StringValue addTo(Context context, Value<?> other, ISyntax syntaxPosition) throws CodeError {
		return new StringValue(this.value + other.getStringValue(context));
	}

	@Override
	public StringValue copy() {
		return this;
	}
	
	@Override
	public int hashCode() {
		return this.value.hashCode();
	}

	@Override
	protected Set<MemberFunction> getDefinedFunctions() {
		Set<MemberFunction> memberFunctions = super.getDefinedFunctions();
		memberFunctions.addAll(Set.of(
			new MemberFunction("toList", this::stringToList),
			new MemberFunction("replaceAll", List.of("regex", "replace"), this::stringReplaceAll),
			new MemberFunction("uppercase", this::stringUppercase),
			new MemberFunction("lowercase", this::stringLowercase),
			new MemberFunction("toNumber", this::stringToNumber),
			new MemberFunction("formatted", "values", this::stringFormatted),
			new MemberFunction("containsString", List.of("otherString"), this::stringContainsString, "Use '<String>.contains(otherString)'"),
			new MemberFunction("contains", "otherString", this::stringContainsString),
			new MemberFunction("strip", this::strip),
			new MemberFunction("capitalise", this::capitalise),
			new MemberFunction("split", "delimited", this::split)
		));
		return memberFunctions;
	}

	private Value<?> stringToList(Context context, MemberFunction function) {
		ArucasValueList stringList = new ArucasValueList();
		for (char c : this.value.toCharArray()) {
			stringList.add(new StringValue(String.valueOf(c)));
		}
		return new ListValue(stringList);
	}

	private Value<?> stringReplaceAll(Context context, MemberFunction function) throws CodeError {
		StringValue remove = function.getParameterValueOfType(context, StringValue.class, 0);
		StringValue replace = function.getParameterValueOfType(context, StringValue.class, 1);
		return new StringValue(this.value.replaceAll(remove.value, replace.value));
	}

	private Value<?> stringUppercase(Context context, MemberFunction function) {
		return new StringValue(this.value.toUpperCase(Locale.ROOT));
	}

	private Value<?> stringLowercase(Context context, MemberFunction function) {
		return new StringValue(this.value.toLowerCase(Locale.ROOT));
	}

	private Value<?> stringToNumber(Context context, MemberFunction function) throws CodeError {
		try {
			return new NumberValue(Double.parseDouble(this.value));
		}
		catch (NumberFormatException e) {
			throw new RuntimeError(
				"Cannot parse %s as a NumberValue".formatted(this.getStringValue(context)),
				function.syntaxPosition,
				context
			);
		}
	}

	private Value<?> stringFormatted(Context context, MemberFunction function) throws CodeError {
		final Value<?>[] array = function.getParameterValueOfType(context, ListValue.class, 0).value.toArray(Value<?>[]::new);
		int i = 0;
		String string = this.value;
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
		String otherString = function.getParameterValueOfType(context, StringValue.class, 0).value;
		return BooleanValue.of(this.value.contains(otherString));
	}

	private Value<?> strip(Context context, MemberFunction function) {
		return new StringValue(this.value.strip());
	}

	private Value<?> capitalise(Context context, MemberFunction function) {
		if (this.value.isEmpty()) {
			return this;
		}
		char[] chars = this.value.toCharArray();
		chars[0] = Character.toUpperCase(chars[0]);
		return new StringValue(new String(chars));
	}

	private Value<?> split(Context context, MemberFunction function) throws CodeError {
		String otherString = function.getParameterValueOfType(context, StringValue.class, 0).value;
		ArucasValueList list = new ArucasValueList();
		for (String string : this.value.split(otherString)) {
			list.add(new StringValue(string));
		}
		return new ListValue(list);
	}

	public static class ArucasStringClass extends ArucasClassExtension {
		public ArucasStringClass() {
			super("String");
		}
	}
}
