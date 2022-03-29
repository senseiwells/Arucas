package me.senseiwells.arucas.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.StringUtils;
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
	public StringValue copy(Context context) {
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

	@Override
	public String getTypeName() {
		return "String";
	}

	/**
	 * String class for Arucas. <br>
	 * Fully Documented.
	 * @author senseiwells
	 */
	public static class ArucasStringClass extends ArucasClassExtension {
		public ArucasStringClass() {
			super("String");
		}

		@Override
		public Class<StringValue> getValueClass() {
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
				new MemberFunction("formatted", "values", this::stringFormatted, "Use '<String>.format(...)'"),
				new MemberFunction("containsString", List.of("otherString"), this::stringContainsString, "Use '<String>.contains(otherString)'"),
				new MemberFunction("contains", "otherString", this::stringContainsString),
				new MemberFunction("strip", this::strip),
				new MemberFunction("capitalise", this::capitalise),
				new MemberFunction("split", "delimiter", this::split),
				new MemberFunction("subString", List.of("from", "to"), this::subString),
				new MemberFunction.Arbitrary("format", this::stringFormatted)
			);
		}

		/**
		 * Name: <code>&lt;String>.toList()</code> <br>
		 * Description: This makes a list of all the characters in the string <br>
		 * Returns - List: the list of characters <br>
		 * Example: <code>"hello".toList();</code>
		 */
		private Value<?> stringToList(Context context, MemberFunction function) throws CodeError {
			StringValue thisValue = function.getParameterValueOfType(context, StringValue.class, 0);
			ArucasList stringList = new ArucasList();
			for (char c : thisValue.value.toCharArray()) {
				stringList.add(new StringValue(String.valueOf(c)));
			}
			return new ListValue(stringList);
		}

		/**
		 * Name: <code>&lt;String>.replaceAll(regex, replace)</code> <br>
		 * Description: This replaces all the instances of a regex with the replace string <br>
		 * Parameter - String, String: the regex you want to replace, the string you want to replace it with <br>
		 * Returns - String: the modified string <br>
		 * Example: <code>"hello".replaceAll("l", "x");</code>
		 */
		private Value<?> stringReplaceAll(Context context, MemberFunction function) throws CodeError {
			StringValue thisValue = function.getParameterValueOfType(context, StringValue.class, 0);
			StringValue remove = function.getParameterValueOfType(context, StringValue.class, 1);
			StringValue replace = function.getParameterValueOfType(context, StringValue.class, 2);
			return new StringValue(thisValue.value.replaceAll(remove.value, replace.value));
		}

		/**
		 * Name: <code>&lt;String>.uppercase()</code> <br>
		 * Description: This makes the string uppercase <br>
		 * Returns - String: the uppercase string <br>
		 * Example: <code>"hello".uppercase();</code>
		 */
		private Value<?> stringUppercase(Context context, MemberFunction function) throws CodeError {
			StringValue thisValue = function.getParameterValueOfType(context, StringValue.class, 0);
			return new StringValue(thisValue.value.toUpperCase(Locale.ROOT));
		}

		/**
		 * Name: <code>&lt;String>.lowercase()</code> <br>
		 * Description: This makes the string lowercase <br>
		 * Returns - String: the lowercase string <br>
		 * Example: <code>"HELLO".lowercase();</code>
		 */
		private Value<?> stringLowercase(Context context, MemberFunction function) throws CodeError {
			StringValue thisValue = function.getParameterValueOfType(context, StringValue.class, 0);
			return new StringValue(thisValue.value.toLowerCase(Locale.ROOT));
		}

		/**
		 * Name: <code>&lt;String>.toNumber()</code> <br>
		 * Description: This tries to convert the string to a number <br>
		 * Returns - Number: the number value <br>
		 * Example: <code>"0xFF".toNumber();</code>
		 */
		private Value<?> stringToNumber(Context context, MemberFunction function) throws CodeError {
			StringValue thisValue = function.getParameterValueOfType(context, StringValue.class, 0);
			try {
				return NumberValue.of(StringUtils.parseNumber(thisValue.value));
			}
			catch (NumberFormatException e) {
				throw new RuntimeError(
					"Cannot parse %s as a NumberValue".formatted(thisValue.getAsString(context)),
					function.syntaxPosition,
					context
				);
			}
		}

		/**
		 * Name: <code>&lt;String>.format(...)</code> <br>
		 * Description: This formats the string with the given parameters <br>
		 * Returns - String: the formatted string <br>
		 * Throws - Error: <code>"You are missing values to be formatted!"</code> if there are not enough parameters <br>
		 * Example: <code>"%s %s".format("hello", "world");</code>
		 */
		private Value<?> stringFormatted(Context context, MemberFunction function) throws CodeError {
			StringValue thisValue = function.getParameterValueOfType(context, StringValue.class, 0);
			final Value<?>[] array = function.getParameterValueOfType(context, ListValue.class, 1).value.toArray();
			int i = 0;
			String string = thisValue.value;
			while (string.contains("%s")) {
				if (i >= array.length) {
					throw new RuntimeError("You are missing values to be formatted!", function.syntaxPosition, context);
				}
				string = string.replaceFirst("%s", array[i].getAsString(context));
				i++;
			}
			return new StringValue(string);
		}

		/**
		 * Name: <code>&lt;String>.contains(string)</code> <br>
		 * Description: This checks if the string contains the given string <br>
		 * Parameter - String: the string you want to check for <br>
		 * Returns - Boolean: true if the string contains the given string <br>
		 * Example: <code>"hello".contains("he");</code>
		 */
		private Value<?> stringContainsString(Context context, MemberFunction function) throws CodeError {
			StringValue thisValue = function.getParameterValueOfType(context, StringValue.class, 0);
			String otherString = function.getParameterValueOfType(context, StringValue.class, 1).value;
			return BooleanValue.of(thisValue.value.contains(otherString));
		}

		/**
		 * Name: <code>&lt;String>.strip()</code> <br>
		 * Description: This strips the whitespace from the string <br>
		 * Returns - String: the stripped string <br>
		 * Example: <code>"  hello  ".strip();</code>
		 */
		private Value<?> strip(Context context, MemberFunction function) throws CodeError {
			StringValue thisValue = function.getParameterValueOfType(context, StringValue.class, 0);
			return new StringValue(thisValue.value.strip());
		}

		/**
		 * Name: <code>&lt;String>.capitalise()</code> <br>
		 * Description: This capitalises the first letter of the string <br>
		 * Returns - String: the capitalised string <br>
		 * Example: <code>"foo".capitalise();</code>
		 */
		private Value<?> capitalise(Context context, MemberFunction function) throws CodeError {
			StringValue thisValue = function.getParameterValueOfType(context, StringValue.class, 0);
			if (thisValue.value.isEmpty()) {
				return thisValue;
			}
			char[] chars = thisValue.value.toCharArray();
			chars[0] = Character.toUpperCase(chars[0]);
			return new StringValue(new String(chars));
		}

		/**
		 * Name: <code>&lt;String>.split(regex)</code> <br>
		 * Description: This splits the string into a list of strings based on a regex <br>
		 * Parameter - String: the regex to split the string with <br>
		 * Returns - List: the list of strings <br>
		 * Example: <code>"foo/bar/baz".split("/");</code>
		 */
		private Value<?> split(Context context, MemberFunction function) throws CodeError {
			StringValue thisValue = function.getParameterValueOfType(context, StringValue.class, 0);
			String otherString = function.getParameterValueOfType(context, StringValue.class, 1).value;
			ArucasList list = new ArucasList();
			for (String string : thisValue.value.split(otherString)) {
				list.add(new StringValue(string));
			}
			return new ListValue(list);
		}

		/**
		 * Name: <code>&lt;String>.subString(from, to)</code> <br>
		 * Description: This returns a substring of the string <br>
		 * Parameters - Number, Number: the start index, the end index <br>
		 * Returns - String: the substring <br>
		 * Example: <code>"hello".subString(1, 3);</code>
		 */
		private Value<?> subString(Context context, MemberFunction function) throws CodeError {
			StringValue thisValue = function.getParameterValueOfType(context, StringValue.class, 0);
			int fromIndex = function.getParameterValueOfType(context, NumberValue.class, 1).value.intValue();
			int toIndex = function.getParameterValueOfType(context, NumberValue.class, 2).value.intValue();
			if (fromIndex < 0 || toIndex > thisValue.value.length()) {
				throw new RuntimeError("Index out of bounds", function.syntaxPosition, context);
			}
			return StringValue.of(thisValue.value.substring(fromIndex, toIndex));
		}
	}
}
