package me.senseiwells.arucas.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasMap;
import me.senseiwells.arucas.utils.impl.ArucasOrderedMap;
import me.senseiwells.arucas.values.functions.MemberFunction;

import java.util.List;

public class MapValue extends Value<ArucasMap> {
	public MapValue(ArucasMap value) {
		super(value);
	}

	@Override
	public MapValue copy(Context context) {
		return this;
	}

	@Override
	public MapValue newCopy(Context context) throws CodeError {
		return new MapValue(new ArucasOrderedMap(context, this.value));
	}
	
	@Override
	public int getHashCode(Context context) throws CodeError {
		return this.value.getHashCode(context);
	}
	
	@Override
	public String getAsString(Context context) throws CodeError {
		return this.value.getAsString(context);
	}
	
	@Override
	public boolean isEquals(Context context, Value<?> other) throws CodeError {
		return this.value.isEquals(context, other);
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
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
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
			Value<?> value = thisValue.value.get(context, key);
			return value == null ? NullValue.NULL : value;
		}

		private Value<?> mapGetKeys(Context context, MemberFunction function) throws CodeError {
			MapValue thisValue = function.getParameterValueOfType(context, MapValue.class, 0);
			return new ListValue(thisValue.value.keys());
		}

		private Value<?> mapGetValues(Context context, MemberFunction function) throws CodeError {
			MapValue thisValue = function.getParameterValueOfType(context, MapValue.class, 0);
			return new ListValue(thisValue.value.values());
		}

		private Value<?> mapPut(Context context, MemberFunction function) throws CodeError {
			MapValue thisValue = function.getParameterValueOfType(context, MapValue.class, 0);
			Value<?> key = function.getParameterValue(context, 1);
			Value<?> value = function.getParameterValue(context, 2);
			Value<?> returnValue = thisValue.value.put(context, key, value);
			return returnValue == null ? NullValue.NULL : returnValue;
		}

		private Value<?> mapPutIfAbsent(Context context, MemberFunction function) throws CodeError {
			MapValue thisValue = function.getParameterValueOfType(context, MapValue.class, 0);
			Value<?> key = function.getParameterValue(context, 1);
			Value<?> value = function.getParameterValue(context, 2);
			Value<?> returnValue = thisValue.value.putIfAbsent(context, key, value);
			return returnValue == null ? NullValue.NULL : returnValue;
		}

		private Value<?> mapPutAll(Context context, MemberFunction function) throws CodeError {
			MapValue thisValue = function.getParameterValueOfType(context, MapValue.class, 0);
			MapValue anotherMapValue = function.getParameterValueOfType(context, MapValue.class, 1);
			thisValue.value.putAll(context, anotherMapValue.value);
			return NullValue.NULL;
		}

		private Value<?> mapRemove(Context context, MemberFunction function) throws CodeError {
			MapValue thisValue = function.getParameterValueOfType(context, MapValue.class, 0);
			Value<?> key = function.getParameterValue(context, 1);
			Value<?> removedValue = thisValue.value.remove(context, key);
			return removedValue == null ? NullValue.NULL : removedValue;
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
