package me.senseiwells.arucas.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.ArucasValueList;
import me.senseiwells.arucas.utils.ArucasValueMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.StringUtils;
import me.senseiwells.arucas.values.functions.MemberFunction;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
	public String getStringValue(Context context) throws CodeError {
		ArucasValueMap map = this.value;
		
		// Because ArucasMapValue is a subclass of ConcurrentHashMap
		// it will never throw an ConcurrentModificationException.
		if (map.isEmpty()) {
			return "{}";
		}
		
		StringBuilder sb = new StringBuilder();

		for (Map.Entry<Value<?>, Value<?>> entry : map.entrySet()) {
			sb.append(", ").append(StringUtils.toPlainString(context, entry.getValue()))
				.append(" : ").append(StringUtils.toPlainString(context, entry.getKey()));
		}

		if (sb.length() > 0) {
			sb.deleteCharAt(0);
		}
		
		return "{%s}".formatted(sb.toString().trim());
	}

	public static class ArucasMapClass extends ArucasClassExtension {
		public ArucasMapClass() {
			super("Map");
		}

		@Override
		public Class<?> getValueClass() {
			return MapValue.class;
		}

		@Override
		public Set<MemberFunction> getDefinedMethods() {
			return Set.of(
				new MemberFunction("get", "key", this::mapGet),
				new MemberFunction("getKeys", this::mapGetKeys),
				new MemberFunction("getValues", this::mapGetValues),
				new MemberFunction("put", List.of("key", "value"), this::mapPut),
				new MemberFunction("putIfAbsent", List.of("key", "value"), this::mapPutIfAbsent),
				new MemberFunction("putAll", "anotherMap", this::mapPutAll),
				new MemberFunction("remove", "key", this::mapRemove),
				new MemberFunction("clear", this::mapClear),
				new MemberFunction("isEmpty", this::isEmpty)
			);
		}

		private Value<?> mapGet(Context context, MemberFunction function) throws CodeError {
			MapValue thisValue = function.getParameterValueOfType(context, MapValue.class, 0);
			Value<?> key = function.getParameterValue(context, 1);
			if (key.value == null) {
				throw new RuntimeError("Cannot get null from a map", function.syntaxPosition, context);
			}
			Value<?> value = thisValue.value.get(key);
			return value == null ? NullValue.NULL : value.newCopy();
		}

		private Value<?> mapGetKeys(Context context, MemberFunction function) throws CodeError {
			MapValue thisValue = function.getParameterValueOfType(context, MapValue.class, 0);
			ArucasValueList valueList = new ArucasValueList();
			thisValue.value.keySet().forEach(value -> valueList.add(value.newCopy()));
			return new ListValue(valueList);
		}

		private Value<?> mapGetValues(Context context, MemberFunction function) throws CodeError {
			MapValue thisValue = function.getParameterValueOfType(context, MapValue.class, 0);
			ArucasValueList valueList = new ArucasValueList();
			valueList.addAll(thisValue.value.values().stream().map(Value::newCopy).toList());
			return new ListValue(valueList);
		}

		private Value<?> mapPut(Context context, MemberFunction function) throws CodeError {
			MapValue thisValue = function.getParameterValueOfType(context, MapValue.class, 0);
			Value<?> key = function.getParameterValue(context, 1);
			Value<?> value = function.getParameterValue(context, 2);
			if (key.value == null || value.value == null) {
				throw new RuntimeError("Cannot put null into a map", function.syntaxPosition, context);
			}
			Value<?> returnValue = thisValue.value.put(key, value);
			return returnValue == null ? NullValue.NULL : returnValue.newCopy();
		}

		private Value<?> mapPutIfAbsent(Context context, MemberFunction function) throws CodeError {
			MapValue thisValue = function.getParameterValueOfType(context, MapValue.class, 0);
			Value<?> key = function.getParameterValue(context, 1);
			Value<?> value = function.getParameterValue(context, 2);
			if (key.value == null || value.value == null) {
				throw new RuntimeError("Cannot put null into a map", function.syntaxPosition, context);
			}
			Value<?> returnValue = thisValue.value.putIfAbsent(key, value);
			return returnValue == null ? NullValue.NULL : returnValue.newCopy();
		}

		private Value<?> mapPutAll(Context context, MemberFunction function) throws CodeError {
			MapValue thisValue = function.getParameterValueOfType(context, MapValue.class, 0);
			MapValue anotherMapValue = function.getParameterValueOfType(context, MapValue.class, 1);
			thisValue.value.putAll(anotherMapValue.value);
			return NullValue.NULL;
		}

		private Value<?> mapRemove(Context context, MemberFunction function) throws CodeError {
			MapValue thisValue = function.getParameterValueOfType(context, MapValue.class, 0);
			Value<?> key = function.getParameterValue(context, 1);
			if (key.value == null) {
				throw new RuntimeError("Cannot remove null from a map", function.syntaxPosition, context);
			}
			Value<?> removedValue = thisValue.value.remove(key);
			return removedValue == null ? NullValue.NULL : removedValue.newCopy();
		}

		private Value<?> mapClear(Context context, MemberFunction function) throws CodeError {
			MapValue thisValue = function.getParameterValueOfType(context, MapValue.class, 0);
			thisValue.value.clear();
			return NullValue.NULL;
		}

		private BooleanValue isEmpty(Context context, MemberFunction function) throws CodeError {
			MapValue thisValue = function.getParameterValueOfType(context, MapValue.class, 0);
			return BooleanValue.of(thisValue.value.isEmpty());
		}
	}
}
