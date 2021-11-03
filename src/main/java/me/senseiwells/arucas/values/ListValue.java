package me.senseiwells.arucas.values;

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
	public String toString() {
		final Value<?>[] array = this.value.toArray(Value<?>[]::new);
		if (array.length == 0) return "[]";
		
		StringBuilder sb = new StringBuilder();
		for (Value<?> element : array) {
			if (element instanceof ListValue) {
				sb.append(", <list>");
			}
			else if (element instanceof StringValue) {
				sb.append(", \"%s\"".formatted(element.toString()));
			}
			else {
				sb.append(", ").append(element);
			}
		}
		sb.deleteCharAt(0);
		
		return "[%s]".formatted(sb.toString().trim());
	}
}
