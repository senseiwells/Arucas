package me.senseiwells.arucas.values;

import me.senseiwells.arucas.utils.ArucasValueList;
import me.senseiwells.arucas.utils.StringUtils;

public class ListValue extends Value<ArucasValueList> {

	public ListValue(ArucasValueList value) {
		super(value);
	}

	@Override
	public ListValue copy() {
		return (ListValue) new ListValue(this.value).setPos(this.startPos, this.endPos);
	}

	@Override
	public ListValue newCopy() {
		return (ListValue) new ListValue(new ArucasValueList(this.value)).setPos(this.startPos, this.endPos);
	}

	@Override
	public String toString() {
		ArucasValueList list = this.value;
		if(list.isEmpty()) return "[]";
		
		StringBuilder sb = new StringBuilder();
		for (Value<?> element : list) {
			sb.append(", ").append(StringUtils.toPlainString(element));
		}
		sb.deleteCharAt(0);
		
		return "[%s]".formatted(sb.toString().trim());
	}
}
