package me.senseiwells.arucas.values;

import me.senseiwells.arucas.utils.ArucasValueMap;
import me.senseiwells.arucas.utils.StringUtils;

public class MapValue extends Value<ArucasValueMap> {
	public MapValue(ArucasValueMap value) {
		super(value);
	}

	@Override
	public MapValue copy() {
		return new MapValue(this.value);
	}

	@Override
	public MapValue newCopy() {
		return new MapValue(new ArucasValueMap(this.value));
	}

	@Override
	public String toString() {
		ArucasValueMap map = this.value;
		
		// Because ArucasMapValue is a subclass of ConcurrentHashMap
		// it will never throw an ConcurrentModificationException.
		if (map.isEmpty()) return "{}";
		
		StringBuilder sb = new StringBuilder();
		map.forEach((value1, value2) ->
			sb.append(", ").append(StringUtils.toPlainString(value1))
			  .append(" : ").append(StringUtils.toPlainString(value2))
		);
		sb.deleteCharAt(0);

		return "{%s}".formatted(sb.toString().trim());
	}
}
