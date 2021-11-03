package me.senseiwells.arucas.values;

import java.util.HashMap;
import java.util.Map;

public class MapValue extends Value<Map<Value<?>, Value<?>>> {
	public MapValue(Map<Value<?>, Value<?>> value) {
		super(value);
	}

	@Override
	public MapValue copy() {
		return (MapValue) new MapValue(this.value).setPos(this.startPos, this.endPos);
	}

	@Override
	public Value<?> newCopy() {
		return new MapValue(new HashMap<>(this.value)).setPos(this.startPos, this.endPos);
	}

	@Override
	public String toString() {
		final Map<Value<?>, Value<?>> map = Map.copyOf(this.value);
		if (map.isEmpty()) return "{}";

		StringBuilder sb = new StringBuilder();
		map.forEach((value1, value2) -> {
			sb.append(", ");
			this.makeString(value1, sb);
			sb.append(" : ");
			this.makeString(value2, sb);
		});
		sb.deleteCharAt(0);

		return "{%s}".formatted(sb.toString().trim());
	}

	private void makeString(Value<?> element, StringBuilder sb) {
		if (element instanceof ListValue) {
			sb.append("<list>");
		}
		else if (element instanceof StringValue) {
			sb.append("\"%s\"".formatted(element.toString()));
		}
		else if (element instanceof MapValue) {
			sb.append("<map>");
		}
		else {
			sb.append(element);
		}
	}
}
