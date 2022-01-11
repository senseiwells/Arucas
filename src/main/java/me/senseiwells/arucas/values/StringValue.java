package me.senseiwells.arucas.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.values.functions.MemberFunction;

import java.util.List;
import java.util.Locale;

public class StringValue extends Value<String> {
	private StringValue(String value) {
		super(value);
	}
	
	public static StringValue of(String value) {
		return new StringValue(value);
	}
	
	@Override
	public StringValue addTo(Context context, Value<?> other, ISyntax syntaxPosition) throws CodeError {
		return new StringValue(this.value + other.getAsString(context));
	}

	@Override
	public StringValue copy() {
		return this;
	}
	
	@Override
	public int getHashCode(Context context) {
		return this.value.hashCode();
	}
	
	@Override
	public String getAsString(Context context) {
		return this.value;
	}
	
	@Override
	public boolean isEquals(Context context, Value<?> other) {
		return (other instanceof StringValue that) && this.value.equals(that.value);
	}
	
	public static class ArucasStringClass extends ArucasClassExtension {
		public ArucasStringClass() {
			super("String");
		}

		@Override
		public Class<?> getValueClass() {
			return StringValue.class;
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
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
			);
		}

		private Value<?> stringToList(Context context, MemberFunction function) throws CodeError {
			StringValue thisValue = function.getParameterValueOfType(context, StringValue.class, 0);
			ArucasList stringList = new ArucasList();
			for (char c : thisValue.value.toCharArray()) {
				stringList.add(new StringValue(String.valueOf(c)));
			}
			return new ListValue(stringList);
		}

		private Value<?> stringReplaceAll(Context context, MemberFunction function) throws CodeError {
			StringValue thisValue = function.getParameterValueOfType(context, StringValue.class, 0);
			StringValue remove = function.getParameterValueOfType(context, StringValue.class, 1);
			StringValue replace = function.getParameterValueOfType(context, StringValue.class, 2);
			return new StringValue(thisValue.value.replaceAll(remove.value, replace.value));
		}

		private Value<?> stringUppercase(Context context, MemberFunction function) throws CodeError {
			StringValue thisValue = function.getParameterValueOfType(context, StringValue.class, 0);
			return new StringValue(thisValue.value.toUpperCase(Locale.ROOT));
		}

		private Value<?> stringLowercase(Context context, MemberFunction function) throws CodeError {
			StringValue thisValue = function.getParameterValueOfType(context, StringValue.class, 0);
			return new StringValue(thisValue.value.toLowerCase(Locale.ROOT));
		}

		private Value<?> stringToNumber(Context context, MemberFunction function) throws CodeError {
			StringValue thisValue = function.getParameterValueOfType(context, StringValue.class, 0);
			try {
				return NumberValue.of(Double.parseDouble(thisValue.value));
			}
			catch (NumberFormatException e) {
				throw new RuntimeError(
					"Cannot parse %s as a NumberValue".formatted(thisValue.getAsString(context)),
					function.syntaxPosition,
					context
				);
			}
		}

		private Value<?> stringFormatted(Context context, MemberFunction function) throws CodeError {
			StringValue thisValue = function.getParameterValueOfType(context, StringValue.class, 0);
			final Value<?>[] array = function.getParameterValueOfType(context, ListValue.class, 1).value.toArray();
			int i = 0;
			String string = thisValue.value;
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
			StringValue thisValue = function.getParameterValueOfType(context, StringValue.class, 0);
			String otherString = function.getParameterValueOfType(context, StringValue.class, 1).value;
			return BooleanValue.of(thisValue.value.contains(otherString));
		}

		private Value<?> strip(Context context, MemberFunction function) throws CodeError {
			StringValue thisValue = function.getParameterValueOfType(context, StringValue.class, 0);
			return new StringValue(thisValue.value.strip());
		}

		private Value<?> capitalise(Context context, MemberFunction function) throws CodeError {
			StringValue thisValue = function.getParameterValueOfType(context, StringValue.class, 0);
			if (thisValue.value.isEmpty()) {
				return thisValue;
			}
			char[] chars = thisValue.value.toCharArray();
			chars[0] = Character.toUpperCase(chars[0]);
			return new StringValue(new String(chars));
		}

		private Value<?> split(Context context, MemberFunction function) throws CodeError {
			StringValue thisValue = function.getParameterValueOfType(context, StringValue.class, 0);
			String otherString = function.getParameterValueOfType(context, StringValue.class, 1).value;
			ArucasList list = new ArucasList();
			for (String string : thisValue.value.split(otherString)) {
				list.add(new StringValue(string));
			}
			return new ListValue(list);
		}
	}
}
