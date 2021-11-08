package me.senseiwells.arucas.values;

import me.senseiwells.arucas.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ListValue extends Value<List<Value<?>>> {

	public ListValue(List<Value<?>> value) {
		super(value);
	}

	@Override
	public Value<List<Value<?>>> copy() {
		return new ListValue(this.value).setPos(this.startPos, this.endPos);
	}

	@Override
	public Value<?> newCopy() {
		// TODO: Is this thread safe?
		return new ListValue(new ArrayList<>(this.value)).setPos(this.startPos, this.endPos);
	}

	@Override
	public String toString() {
		// TODO: Is this thread safe?
		final Value<?>[] array = this.value.toArray(Value<?>[]::new);
		if(array.length == 0) return "[]";
		
		StringBuilder sb = new StringBuilder();
		for (Value<?> element : array) {
			sb.append(", ").append(StringUtils.toPlainString(element));
		}
		sb.deleteCharAt(0);
		
		return "[%s]".formatted(sb.toString().trim());
	}
}
