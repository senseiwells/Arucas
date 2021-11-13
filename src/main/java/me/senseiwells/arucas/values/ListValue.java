package me.senseiwells.arucas.values;

import me.senseiwells.arucas.utils.ArucasValueList;
import me.senseiwells.arucas.utils.StringUtils;

public class ListValue extends Value<ArucasValueList> {

	public ListValue(ArucasValueList value) {
		super(value);
	}

	@Override
	public ListValue copy() {
		return new ListValue(this.value);
	}

	@Override
	public ListValue newCopy() {
		return new ListValue(new ArucasValueList(this.value));
	}

	@Override
	public String toString() {
		ArucasValueList list = this.value;
		if (list.isEmpty()) return "[]";
		
		StringBuilder sb = new StringBuilder();
		for (Value<?> element : list) {
			sb.append(", ").append(StringUtils.toPlainString(element));
		}
		
		/*
		 * Because of thread safety the list might have been reset before this point
		 * and is empty meaning that 'sb' will be empty. If this was the case an
		 * StringIndexOutOfBoundsException would have been thrown.
		 *
		 * To prevent this exception we check if the StringBuilder has any characters
		 * inside of it.
		 */
		if (sb.length() > 0) {
			sb.deleteCharAt(0);
		}
		
		return "[%s]".formatted(sb.toString().trim());
	}
}
