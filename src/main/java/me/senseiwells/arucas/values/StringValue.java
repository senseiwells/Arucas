package me.senseiwells.arucas.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Arguments;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.StringUtils;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.values.functions.MemberFunction;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.senseiwells.arucas.utils.ValueTypes.*;

public class StringValue extends GenericValue<String> {
	private StringValue(String value) {
		super(value);
	}

	public static StringValue of(String value) {
		return new StringValue(value);
	}

	@Override
	public StringValue addTo(Context context, Value other, ISyntax syntaxPosition) throws CodeError {
		return new StringValue(this.value + other.getAsString(context));
	}

	@Override
	public int compareTo(Context context, Value other) {
		if (other instanceof StringValue stringValue) {
			return this.value.compareTo(stringValue.value);
		}
		return super.compareTo(context, other);
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
	public boolean isEquals(Context context, Value other) {
		return (other instanceof StringValue that) && this.value.equals(that.value);
	}

	@Override
	public String getTypeName() {
		return STRING;
	}

	@ClassDoc(
		name = STRING,
		desc = "This class cannot be constructed since strings have a literal. Strings are immutable."
	)
	public static class ArucasStringClass extends ArucasClassExtension {
		public ArucasStringClass() {
			super(STRING);
		}

		@Override
		public Class<StringValue> getValueClass() {
			return StringValue.class;
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				MemberFunction.of("toList", this::stringToList),
				MemberFunction.of("replaceAll", 2, this::stringReplaceAll),
				MemberFunction.of("uppercase", this::stringUppercase),
				MemberFunction.of("lowercase", this::stringLowercase),
				MemberFunction.of("toNumber", this::stringToNumber),
				MemberFunction.of("containsString", 1, this::stringContainsString, "Use '<String>.contains(otherString)'"),
				MemberFunction.of("contains", 1, this::stringContainsString),
				MemberFunction.of("strip", this::strip),
				MemberFunction.of("capitalise", this::capitalise),
				MemberFunction.of("split", 1, this::split),
				MemberFunction.of("subString", 2, this::subString),
				MemberFunction.of("matches", 1, this::matches),
				MemberFunction.of("find", 1, this::find),
				MemberFunction.of("startsWith", 1, this::startsWith),
				MemberFunction.of("endsWith", 1, this::endsWith),
				MemberFunction.arbitrary("format", this::stringFormatted)
			);
		}

		@FunctionDoc(
			name = "toList",
			desc = "This makes a list of all the characters in the string",
			returns = {LIST, "the list of characters"},
			example = "'hello'.toList();"
		)
		private Value stringToList(Arguments arguments) throws CodeError {
			StringValue thisValue = arguments.getNext(StringValue.class);
			ArucasList stringList = new ArucasList();
			for (char c : thisValue.value.toCharArray()) {
				stringList.add(new StringValue(String.valueOf(c)));
			}
			return new ListValue(stringList);
		}

		@FunctionDoc(
			name = "replaceAll",
			desc = "This replaces all the instances of a regex with the replace string",
			params = {
				STRING, "regex", "the regex you want to replace",
				STRING, "replace", "the string you want to replace it with"
			},
			returns = {STRING, "the modified string"},
			example = "'hello'.replaceAll('l', 'x');"
		)
		private Value stringReplaceAll(Arguments arguments) throws CodeError {
			StringValue thisValue = arguments.getNext(StringValue.class);
			StringValue remove = arguments.getNext(StringValue.class);
			StringValue replace = arguments.getNext(StringValue.class);
			return new StringValue(thisValue.value.replaceAll(remove.value, replace.value));
		}

		@FunctionDoc(
			name = "uppercase",
			desc = "This makes the string uppercase",
			returns = {STRING, "the uppercase string"},
			example = "'hello'.uppercase();"
		)
		private Value stringUppercase(Arguments arguments) throws CodeError {
			StringValue thisValue = arguments.getNext(StringValue.class);
			return new StringValue(thisValue.value.toUpperCase(Locale.ROOT));
		}

		@FunctionDoc(
			name = "lowercase",
			desc = "This makes the string lowercase",
			returns = {STRING, "the lowercase string"},
			example = "'HELLO'.lowercase();"
		)
		private Value stringLowercase(Arguments arguments) throws CodeError {
			StringValue thisValue = arguments.getNext(StringValue.class);
			return new StringValue(thisValue.value.toLowerCase(Locale.ROOT));
		}

		@FunctionDoc(
			name = "toNumber",
			desc = "This tries to convert the string to a number",
			throwMsgs = "Cannor parse ... as a number",
			returns = {NUMBER, "the number value"},
			example = "'0xFF'.toNumber();"
		)
		private Value stringToNumber(Arguments arguments) throws CodeError {
			StringValue thisValue = arguments.getNext(StringValue.class);
			try {
				return NumberValue.of(StringUtils.parseNumber(thisValue.value));
			}
			catch (IllegalArgumentException e) {
				throw arguments.getError("Cannot parse %s as a number", thisValue);
			}
		}

		@FunctionDoc(
			isVarArgs = true,
			name = "format",
			desc = "This formats the string with the given parameters, which replace '%s' in the string",
			params = {ANY, "values...", "the values to add, these will be converted to strings"},
			returns = {STRING, "the formatted string"},
			throwMsgs = "You are missing values to be formatted",
			example = "'%s %s'.format('hello', 'world');"
		)
		private Value stringFormatted(Arguments arguments) throws CodeError {
			StringValue thisValue = arguments.getNext(StringValue.class);
			final Value[] array = arguments.getAll().toArray(Value[]::new);
			int i = 1;
			String string = thisValue.value;
			while (string.contains("%s")) {
				if (i >= array.length) {
					throw arguments.getError("You are missing values to be formatted");
				}
				string = string.replaceFirst("%s", array[i].getAsString(arguments.getContext()));
				i++;
			}
			return new StringValue(string);
		}

		@FunctionDoc(
			name = "contains",
			desc = "This checks if the string contains the given string",
			params = {STRING, "string", "the string you want to check for"},
			returns = {BOOLEAN, "true if the string contains the given string"},
			example = "'hello'.contains('he');"
		)
		private Value stringContainsString(Arguments arguments) throws CodeError {
			StringValue thisValue = arguments.getNext(StringValue.class);
			String otherString = arguments.getNextGeneric(StringValue.class);
			return BooleanValue.of(thisValue.value.contains(otherString));
		}

		@FunctionDoc(
			name = "strip",
			desc = "This strips the whitespace from the string",
			returns = {STRING, "the stripped string"},
			example = "'  hello  '.strip();"
		)
		private Value strip(Arguments arguments) throws CodeError {
			StringValue thisValue = arguments.getNext(StringValue.class);
			return new StringValue(thisValue.value.strip());
		}

		@FunctionDoc(
			name = "capitalise",
			desc = "This capitalises the first letter of the string",
			returns = {STRING, "the capitalised string"},
			example = "'foo'.capitalise();"
		)
		private Value capitalise(Arguments arguments) throws CodeError {
			StringValue thisValue = arguments.getNext(StringValue.class);
			if (thisValue.value.isEmpty()) {
				return thisValue;
			}
			char[] chars = thisValue.value.toCharArray();
			chars[0] = Character.toUpperCase(chars[0]);
			return new StringValue(new String(chars));
		}

		@FunctionDoc(
			name = "split",
			desc = "This splits the string into a list of strings based on a regex",
			params = {STRING, "regex", "the regex to split the string with"},
			returns = {LIST, "the list of strings"},
			example = "'foo/bar/baz'.split('/');"
		)
		private Value split(Arguments arguments) throws CodeError {
			StringValue thisValue = arguments.getNext(StringValue.class);
			String otherString = arguments.getNextGeneric(StringValue.class);
			ArucasList list = new ArucasList();
			for (String string : thisValue.value.split(otherString)) {
				list.add(new StringValue(string));
			}
			return new ListValue(list);
		}

		@FunctionDoc(
			name = "subString",
			desc = "This returns a substring of the string",
			params = {
				NUMBER, "from", "the start index",
				NUMBER, "to", "the end index"
			},
			returns = {STRING, "the substring"},
			example = "'hello'.subString(1, 3);"
		)
		private Value subString(Arguments arguments) throws CodeError {
			StringValue thisValue = arguments.getNext(StringValue.class);
			int fromIndex = arguments.getNextGeneric(NumberValue.class).intValue();
			int toIndex = arguments.getNextGeneric(NumberValue.class).intValue();
			if (fromIndex < 0 || toIndex > thisValue.value.length()) {
				throw arguments.getError("Index out of bounds");
			}
			return StringValue.of(thisValue.value.substring(fromIndex, toIndex));
		}

		@FunctionDoc(
			name = "matches",
			desc = "This checks if the string matches the given regex",
			params = {STRING, "regex", "the regex to check the string with"},
			returns = {BOOLEAN, "true if the string matches the given regex"},
			example = "'hello'.matches('[a-z]*');"
		)
		private Value matches(Arguments arguments) throws CodeError {
			StringValue thisValue = arguments.getNext(StringValue.class);
			StringValue regex = arguments.getNext(StringValue.class);
			return BooleanValue.of(thisValue.value.matches(regex.value));
		}

		@FunctionDoc(
			name = "find",
			desc = "This finds all instances of the regex in the string",
			params = {STRING, "regex", "the regex to search the string with"},
			returns = {LIST, "the list of all instances of the regex in the string"},
			example = "'hello'.find('[a-z]*');"
		)
		private Value find(Arguments arguments) throws CodeError {
			StringValue thisValue = arguments.getNext(StringValue.class);
			StringValue regex = arguments.getNext(StringValue.class);
			Matcher matcher = Pattern.compile(regex.value).matcher(thisValue.value);
			ArucasList arucasList = new ArucasList();
			while (matcher.find()) {
				arucasList.add(StringValue.of(matcher.group()));
			}
			return new ListValue(arucasList);
		}

		@FunctionDoc(
			name = "startsWith",
			desc = "This checks if the string starts with the given string",
			params = {STRING, "string", "the string to check the string with"},
			returns = {BOOLEAN, "true if the string starts with the given string"},
			example = "'hello'.startsWith('he');"
		)
		private Value startsWith(Arguments arguments) throws CodeError {
			StringValue thisValue = arguments.getNext(StringValue.class);
			StringValue otherString = arguments.getNext(StringValue.class);
			return BooleanValue.of(thisValue.value.startsWith(otherString.value));
		}

		@FunctionDoc(
			name = "endsWith",
			desc = "This checks if the string ends with the given string",
			params = {STRING, "string", "the string to check the string with"},
			returns = {BOOLEAN, "true if the string ends with the given string"},
			example = "'hello'.endsWith('he');"
		)
		private Value endsWith(Arguments arguments) throws CodeError {
			StringValue thisValue = arguments.getNext(StringValue.class);
			StringValue otherString = arguments.getNext(StringValue.class);
			return BooleanValue.of(thisValue.value.endsWith(otherString.value));
		}
	}
}
