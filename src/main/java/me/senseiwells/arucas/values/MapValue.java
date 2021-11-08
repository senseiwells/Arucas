package me.senseiwells.arucas.values;

import me.senseiwells.arucas.utils.StringUtils;

import java.io.File;
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
		// TODO: Is this thread safe?
		return new MapValue(new HashMap<>(this.value)).setPos(this.startPos, this.endPos);
	}

	@Override
	public String toString() {
		// TODO: Is this thread safe?
		final Map<Value<?>, Value<?>> map = Map.copyOf(this.value);
		if (map.isEmpty()) return "{}";
		
		StringBuilder sb = new StringBuilder();
		map.forEach((value1, value2) -> {
			sb.append(", ").append(StringUtils.toPlainString(value1))
			  .append(" : ").append(StringUtils.toPlainString(value2));
		});
		sb.deleteCharAt(0);

		return "{%s}".formatted(sb.toString().trim());
	}
}
