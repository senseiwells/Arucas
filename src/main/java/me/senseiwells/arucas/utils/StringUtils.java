package me.senseiwells.arucas.utils;

import me.senseiwells.arucas.values.ListValue;
import me.senseiwells.arucas.values.MapValue;
import me.senseiwells.arucas.values.StringValue;
import me.senseiwells.arucas.values.Value;

public class StringUtils {
	/**
	 * Converts all instances of <code>[\'] [\"] [\\] [\r] [\n] [\b] [\t] [\x..] [&bsol;u....]</code> to the correct character.
	 */
	public static String unescapeString(String string) {
		if (string == null) {
			return null;
		}

		StringBuilder sb = new StringBuilder();
		boolean escape = false;
		
		for (int i = 0, len = string.length(); i < len; i++) {
			char c = string.charAt(i);
			
			if (escape) {
				escape = false;

				switch (c) {
					case '\'', '\"', '\\' -> sb.append(c);
					case '0' -> sb.append('\0');
					case 'r' -> sb.append('\r');
					case 'n' -> sb.append('\n');
					case 'b' -> sb.append('\b');
					case 't' -> sb.append('\t');
					case 'x' -> {
						if (i + 3 > string.length()) {
							throw new RuntimeException("(index:%d) Not enough characters for '\\x..' escape.".formatted(i));
						}

						String hex = string.substring(i + 1, i + 3);

						try {
							sb.append((char)Integer.parseInt(hex, 16));
						}
						catch (NumberFormatException e) {
							throw new RuntimeException("(index:%d) Invalid escape '\\x%s'".formatted(i, hex));
						}

						i += 2;
					}
					
					case 'u' -> {
						if (i + 5 > string.length()) {
							throw new RuntimeException("(index:%d) Not enough characters for '\\u....' escape.".formatted(i));
						}

						String hex = string.substring(i + 1, i + 5);

						try {
							sb.append((char)Integer.parseInt(hex, 16));
						}
						catch (NumberFormatException e) {
							throw new RuntimeException("(index:%d) Invalid escape '\\u%s'".formatted(i, hex));
						}

						i += 4;
					}

					default -> throw new RuntimeException("(index:%d) Invalid character escape '\\%s'".formatted(i, c));
				}
			}
			else if (c == '\\') {
				escape = true;
			}
			else {
				sb.append(c);
			}
		}
		
		return sb.toString();
	}
	
	/**
	 * Escapes a string to convert all control characters into their escaped form.
	 */
	@SuppressWarnings("unused")
	public static String escapeString(String string) {
		if (string == null) {
			return null;
		}
		
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0, len = string.length(); i < len; i++) {
			char c = string.charAt(i);

			switch (c) { // Normal escapes
				case '\r' -> { sb.append("\\r"); continue; }
				case '\n' -> { sb.append("\\n"); continue; }
				case '\b' -> { sb.append("\\b"); continue; }
				case '\t' -> { sb.append("\\t"); continue; }
				case '\'' -> { sb.append("\\'"); continue; }
				case '\"' -> { sb.append("\\\""); continue; }
				case '\\' -> { sb.append("\\\\"); continue; }
			}
			
			if (c > 0xff) { // Unicode
				sb.append("\\u").append(toHexString(c, 4));
				continue;
			}
			
			if (Character.isISOControl(c)) { // Control character
				sb.append("\\x").append(toHexString(c, 2));
				continue;
			}
			
			sb.append(c);
		}
		
		return sb.toString();
	}
	
	/**
	 * Escapes a string so that it can safely be placed inside a regex expression.
	 */
	public static String regexEscape(String string) {
		if (string == null) {
			return null;
		}
		
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0, len = string.length(); i < len; i++) {
			char c = string.charAt(i);

			switch (c) { // Normal escapes
				case '\0' -> { sb.append("\\0"); continue; }
				case '\n' -> { sb.append("\\n"); continue; }
				case '\r' -> { sb.append("\\r"); continue; }
				case '\t' -> { sb.append("\\t"); continue; }
				case '\\' -> { sb.append("\\\\"); continue; }
				case '^', '$', '?', '|', '*', '/', '+', '.', '(', ')', '[', ']', '{', '}' -> {
					sb.append("\\").append(c);
					continue;
				}
			}
			
			if (c > 0xff) { // Unicode
				sb.append("\\u").append(toHexString(c, 4));
				continue;
			}
			
			if (Character.isISOControl(c)) { // Control character
				sb.append("\\x").append(toHexString(c, 2));
				continue;
			}
			
			sb.append(c);
		}
		
		return sb.toString();
	}
	
	/**
	 * Converts a number into a hex string with a given minimum length.
	 *
	 * @param	value	the value to be converted to a hex string
	 * @param	length	the minimum length of that hex string
	 * @return	a hex string
	 */
	public static String toHexString(long value, int length) {
		if (length < 1) {
			throw new IllegalArgumentException("The minimum length of the returned string cannot be less than one.");
		}
		return String.format("%0" + length + "x", value);
	}
	
	/**
	 * Converts a string into a number</br>
	 * If the input string has a negative sign it will be handled correctly.
	 *
	 * <pre>
	 *   Binary:
	 *     0[bB][01]+
	 *
	 *   Hexadecimal:
	 *     0[xX][0-9a-fA-F]+
	 *
	 *   Octodecimal:
	 *     0[0-7]+
	 *
	 *   Decimal:
	 *     [0-9]+(\.[0-9]+)?
	 * </pre>
	 */
	@SuppressWarnings("unused")
	public static double parseNumber(String string) {
		if (string == null || string.isBlank()) {
			throw new IllegalArgumentException("The input string must not be null or empty");
		}
		
		// First check if the value is negative.
		boolean isNegative = string.charAt(0) == '-';
		
		if (isNegative) {
			// If the string is negative we remove the first character.
			string = string.substring(1);
		}
		
		double result;
		try {
			if (string.startsWith("0x")) { // Hexadecimal
				result = Long.parseLong(string.substring(2), 16);
			}
			else if (string.startsWith("0b")) { // Binary
				result = Long.parseLong(string.substring(2), 2);
			}
			else if (string.startsWith("0")) { // Octodecimal
				result = Long.parseLong(string.substring(1), 7);
			}
			else { // Decimal
				result = Double.parseDouble(string);
			}
			
			return isNegative ? -result : result;
		}
		catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid input string. '%s' is not a number".formatted(string));
		}
	}
	
	/**
	 * Convert value objects into their simple form.
	 */
	public static String toPlainString(Value<?> value) {
		if (value instanceof StringValue) {
			return "\"%s\"".formatted(value);
		}
		else if (value instanceof ListValue) {
			return "<list>";
		}
		else if (value instanceof MapValue) {
			return "<map>";
		}
		else {
			return value.toString();
		}
	}
}
