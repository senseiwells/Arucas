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

	@Override
	public String getTypeName() {
		return "Map";
	}

	/**
	 * Map class for Arucas. <br>
	 * This class cannot be constructed since it has a literal, <code>{}</code> <br>
	 * Fully Documented.
	 * @author senseiwells
	 */
	public static class ArucasMapClass extends ArucasClassExtension {
		public ArucasMapClass() {
			super("Map");
		}

		@Override
		public Class<MapValue> getValueClass() {
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
				new MemberFunction("isEmpty", this::isEmpty),
				new MemberFunction("containsKey", "key", this::mapContainsKey),
				new MemberFunction("toString", this::toString)
			);
		}

		/**
		 * Name: <code>&lt;Map>.get(key)</code> <br>
		 * Description: this allows you to get the value of a key in the map <br>
		 * Parameter - Value: the key you want to get the value of <br>
		 * Returns - Value: the value of the key, will return null if non-existent <br>
		 * Example: <code>{"key": "value"}.get("key");</code>
		 */
		private Value<?> mapGet(Context context, MemberFunction function) throws CodeError {
			MapValue thisValue = function.getParameterValueOfType(context, MapValue.class, 0);
			Value<?> key = function.getParameterValue(context, 1);
			Value<?> value = thisValue.value.get(context, key);
			return value == null ? NullValue.NULL : value;
		}

		/**
		 * Name: <code>&lt;Map>.getKeys()</code> <br>
		 * Description: this allows you to get the keys in the map <br>
		 * Returns - List: a complete list of all the keys <br>
		 * Example: <code>{"key": "value", "key2", "value2"}.getKeys();</code>
		 */
		private Value<?> mapGetKeys(Context context, MemberFunction function) throws CodeError {
			MapValue thisValue = function.getParameterValueOfType(context, MapValue.class, 0);
			return new ListValue(thisValue.value.keys());
		}

		/**
		 * Name: <code>&lt;Map>.getValues()</code> <br>
		 * Description: this allows you to get the values in the map <br>
		 * Returns - List: a complete list of all the values <br>
		 * Example: <code>{"key": "value", "key2", "value2"}.getValues();</code>
		 */
		private Value<?> mapGetValues(Context context, MemberFunction function) throws CodeError {
			MapValue thisValue = function.getParameterValueOfType(context, MapValue.class, 0);
			return new ListValue(thisValue.value.values());
		}

		/**
		 * Name: <code>&lt;Map>.put(key, value)</code> <br>
		 * Description: this allows you to put a key and value in the map <br>
		 * Parameter - Value, Value: the key you want to put, the value you want to put <br>
		 * Returns - Value: the previous value associated with the key, null if none <br>
		 * Example: <code>{"key": "value"}.put("key2", "value2");</code>
		 */
		private Value<?> mapPut(Context context, MemberFunction function) throws CodeError {
			MapValue thisValue = function.getParameterValueOfType(context, MapValue.class, 0);
			Value<?> key = function.getParameterValue(context, 1);
			Value<?> value = function.getParameterValue(context, 2);
			Value<?> returnValue = thisValue.value.put(context, key, value);
			return returnValue == null ? NullValue.NULL : returnValue;
		}

		/**
		 * Name: <code>&lt;Map>.putIfAbsent(key, value)</code> <br>
		 * Description: this allows you to put a key and value in the map if it doesn't exist <br>
		 * Parameter - Value, Value: the key you want to put, the value you want to put <br>
		 * Returns - Value: the previous value associated with the key, null if none <br>
		 * Example: <code>{"key": "value"}.putIfAbsent("key2", "value2");</code>
		 */
		private Value<?> mapPutIfAbsent(Context context, MemberFunction function) throws CodeError {
			MapValue thisValue = function.getParameterValueOfType(context, MapValue.class, 0);
			Value<?> key = function.getParameterValue(context, 1);
			Value<?> value = function.getParameterValue(context, 2);
			Value<?> returnValue = thisValue.value.putIfAbsent(context, key, value);
			return returnValue == null ? NullValue.NULL : returnValue;
		}

		/**
		 * Name: <code>&lt;Map>.putAll(anotherMap)</code> <br>
		 * Description: this allows you to put all the keys and values of another map into this map <br>
		 * Parameter - Value: another map <br>
		 * Example: <code>{"key": "value"}.putAll({"key2": "value2"});</code>
		 */
		private Value<?> mapPutAll(Context context, MemberFunction function) throws CodeError {
			MapValue thisValue = function.getParameterValueOfType(context, MapValue.class, 0);
			MapValue anotherMapValue = function.getParameterValueOfType(context, MapValue.class, 1);
			thisValue.value.putAll(context, anotherMapValue.value);
			return NullValue.NULL;
		}

		/**
		 * Name: <code>&lt;Map>.remove(key)</code> <br>
		 * Description: this allows you to remove a key and its value from the map <br>
		 * Parameter - Value: the key you want to remove <br>
		 * Returns - Value: the value associated with the key, null if none <br>
		 * Example: <code>{"key": "value"}.remove("key");</code>
		 */
		private Value<?> mapRemove(Context context, MemberFunction function) throws CodeError {
			MapValue thisValue = function.getParameterValueOfType(context, MapValue.class, 0);
			Value<?> key = function.getParameterValue(context, 1);
			Value<?> removedValue = thisValue.value.remove(context, key);
			return removedValue == null ? NullValue.NULL : removedValue;
		}

		/**
		 * Name: <code>&lt;Map>.clear()</code> <br>
		 * Description: this allows you to clear the map of all the keys and values <br>
		 * Example: <code>{"key": "value"}.clear();</code>
		 */
		private Value<?> mapClear(Context context, MemberFunction function) throws CodeError {
			MapValue thisValue = function.getParameterValueOfType(context, MapValue.class, 0);
			thisValue.value.clear();
			return NullValue.NULL;
		}

		/**
		 * Name: <code>&lt;Map>.isEmpty()</code> <br>
		 * Description: this allows you to check if the map is empty <br>
		 * Returns - Boolean: true if the map is empty, false otherwise <br>
		 * Example: <code>{"key": "value"}.isEmpty();</code>
		 */
		private BooleanValue isEmpty(Context context, MemberFunction function) throws CodeError {
			MapValue thisValue = function.getParameterValueOfType(context, MapValue.class, 0);
			return BooleanValue.of(thisValue.value.isEmpty());
		}

		/**
		 * Name: <code>&lt;Map>.containsKey(key)</code> <br>
		 * Description: this allows you to check if the map contains a specific key <br>
		 * Parameter - Value: the key you want to check <br>
		 * Returns - Boolean: true if the map contains the key, false otherwise <br>
		 * Example: <code>{"key": "value"}.containsKey("key");</code>
		 */
		private BooleanValue mapContainsKey(Context context, MemberFunction function) throws CodeError {
			MapValue thisValue = function.getParameterValueOfType(context, MapValue.class, 0);
			Value<?> key = function.getParameterValue(context, 1);
			return BooleanValue.of(thisValue.value.containsKey(context, key));
		}

		/**
		 * Name: <code>&lt;Map>.toString()</code> <br>
		 * Description: this allows you to get the string representation of the map and evaluating any collections inside it <br>
		 * Returns - String: the string representation of the map <br>
		 * Example: <code>{"key": []}.toString();</code>
		 */
		private Value<?> toString(Context context, MemberFunction function) throws CodeError {
			MapValue thisValue = function.getParameterValueOfType(context, MapValue.class, 0);
			return StringValue.of(thisValue.value.getAsStringUnsafe(context, function.syntaxPosition));
		}
	}
}
