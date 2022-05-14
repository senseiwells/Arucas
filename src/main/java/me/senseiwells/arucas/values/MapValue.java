package me.senseiwells.arucas.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasMap;
import me.senseiwells.arucas.utils.impl.ArucasOrderedMap;
import me.senseiwells.arucas.values.functions.BuiltInFunction;
import me.senseiwells.arucas.values.functions.MemberFunction;

import java.util.List;

import static me.senseiwells.arucas.utils.ValueTypes.*;

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
		return MAP;
	}

	@ClassDoc(
		name = MAP,
		desc = "This class cannot be constructed since it has a literal, `{}`"
	)
	public static class ArucasMapClass extends ArucasClassExtension {
		public ArucasMapClass() {
			super(MAP);
		}

		@Override
		public Class<MapValue> getValueClass() {
			return MapValue.class;
		}

		@Override
		public ArucasFunctionMap<BuiltInFunction> getDefinedStaticMethods() {
			return ArucasFunctionMap.of(
				new BuiltInFunction("unordered", this::unordered)
			);
		}

		@FunctionDoc(
			isStatic = true,
			name = "unordered",
			desc = "This function allows you to create an unordered map",
			returns = {MAP, "an unordered map"},
			example = "Map.unordered();"
		)
		private Value<?> unordered(Context context, BuiltInFunction function) {
			return new MapValue(new ArucasMap());
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

		@FunctionDoc(
			name = "get",
			desc = "This allows you to get the value of a key in the map",
			params = {ANY, "key", "the key you want to get the value of"},
			returns = {ANY, "the value of the key, will return null if non-existent"},
			example = "{'key': 'value'}.get('key');"
		)
		private Value<?> mapGet(Context context, MemberFunction function) throws CodeError {
			MapValue thisValue = function.getParameterValueOfType(context, MapValue.class, 0);
			Value<?> key = function.getParameterValue(context, 1);
			Value<?> value = thisValue.value.get(context, key);
			return value == null ? NullValue.NULL : value;
		}

		@FunctionDoc(
			name = "getKeys",
			desc = "This allows you to get the keys in the map",
			returns = {LIST, "a complete list of all the keys"},
			example = "{'key': 'value', 'key2', 'value2'}.getKeys();"
		)
		private Value<?> mapGetKeys(Context context, MemberFunction function) throws CodeError {
			MapValue thisValue = function.getParameterValueOfType(context, MapValue.class, 0);
			return new ListValue(thisValue.value.keys());
		}

		@FunctionDoc(
			name = "getValues",
			desc = "This allows you to get the values in the map",
			returns = {LIST, "a complete list of all the values"},
			example = "{'key': 'value', 'key2', 'value2'}.getValues();"
		)
		private Value<?> mapGetValues(Context context, MemberFunction function) throws CodeError {
			MapValue thisValue = function.getParameterValueOfType(context, MapValue.class, 0);
			return new ListValue(thisValue.value.values());
		}

		@FunctionDoc(
			name = "put",
			desc = "This allows you to put a key and value in the map",
			params = {
				ANY, "key", "the key you want to put",
				ANY, "value", "the value you want to put"
			},
			returns = {ANY, "the previous value associated with the key, null if none"},
			example = "{'key': 'value'}.put('key2', 'value2');"
		)
		private Value<?> mapPut(Context context, MemberFunction function) throws CodeError {
			MapValue thisValue = function.getParameterValueOfType(context, MapValue.class, 0);
			Value<?> key = function.getParameterValue(context, 1);
			Value<?> value = function.getParameterValue(context, 2);
			Value<?> returnValue = thisValue.value.put(context, key, value);
			return returnValue == null ? NullValue.NULL : returnValue;
		}

		@FunctionDoc(
			name = "putIfAbsent",
			desc = "This allows you to put a key and value in the map if it doesn't exist",
			params = {
				ANY, "key", "the key you want to put",
				ANY, "value", "the value you want to put"
			},
			returns = {ANY, "the previous value associated with the key, null if none"},
			example = "{'key': 'value'}.putIfAbsent('key2', 'value2');"
		)
		private Value<?> mapPutIfAbsent(Context context, MemberFunction function) throws CodeError {
			MapValue thisValue = function.getParameterValueOfType(context, MapValue.class, 0);
			Value<?> key = function.getParameterValue(context, 1);
			Value<?> value = function.getParameterValue(context, 2);
			Value<?> returnValue = thisValue.value.putIfAbsent(context, key, value);
			return returnValue == null ? NullValue.NULL : returnValue;
		}

		@FunctionDoc(
			name = "putAll",
			desc = "This allows you to put all the keys and values of another map into this map",
			params = {MAP, "another map", "the map you want to merge into this map"},
			example = "{'key': 'value'}.putAll({'key2': 'value2'});"
		)
		private Value<?> mapPutAll(Context context, MemberFunction function) throws CodeError {
			MapValue thisValue = function.getParameterValueOfType(context, MapValue.class, 0);
			MapValue anotherMapValue = function.getParameterValueOfType(context, MapValue.class, 1);
			thisValue.value.putAll(context, anotherMapValue.value);
			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "remove",
			desc = "This allows you to remove a key and its value from the map",
			params = {ANY, "key", "the key you want to remove"},
			returns = {ANY, "the value associated with the key, null if none"},
			example = "{'key': 'value'}.remove('key');"
		)
		private Value<?> mapRemove(Context context, MemberFunction function) throws CodeError {
			MapValue thisValue = function.getParameterValueOfType(context, MapValue.class, 0);
			Value<?> key = function.getParameterValue(context, 1);
			Value<?> removedValue = thisValue.value.remove(context, key);
			return removedValue == null ? NullValue.NULL : removedValue;
		}

		@FunctionDoc(
			name = "clear",
			desc = "This allows you to clear the map of all the keys and values",
			example = "{'key': 'value'}.clear();"
		)
		private Value<?> mapClear(Context context, MemberFunction function) throws CodeError {
			MapValue thisValue = function.getParameterValueOfType(context, MapValue.class, 0);
			thisValue.value.clear();
			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "isEmpty",
			desc = "This allows you to check if the map is empty",
			returns = {BOOLEAN, "true if the map is empty, false otherwise"},
			example = "{'key': 'value'}.isEmpty();"
		)
		private BooleanValue isEmpty(Context context, MemberFunction function) throws CodeError {
			MapValue thisValue = function.getParameterValueOfType(context, MapValue.class, 0);
			return BooleanValue.of(thisValue.value.isEmpty());
		}

		@FunctionDoc(
			name = "containsKey",
			desc = "This allows you to check if the map contains a specific key",
			params = {ANY, "key", "the key you want to check"},
			returns = {BOOLEAN, "true if the map contains the key, false otherwise"},
			example = "{'key': 'value'}.containsKey('key');"
		)
		private BooleanValue mapContainsKey(Context context, MemberFunction function) throws CodeError {
			MapValue thisValue = function.getParameterValueOfType(context, MapValue.class, 0);
			Value<?> key = function.getParameterValue(context, 1);
			return BooleanValue.of(thisValue.value.containsKey(context, key));
		}

		@FunctionDoc(
			name = "toString",
			desc = "This allows you to get the string representation of the map and evaluating any collections inside it",
			returns = {STRING, "the string representation of the map"},
			example = "{'key': []}.toString();"
		)
		private Value<?> toString(Context context, MemberFunction function) throws CodeError {
			MapValue thisValue = function.getParameterValueOfType(context, MapValue.class, 0);
			return StringValue.of(thisValue.value.getAsStringUnsafe(context, function.syntaxPosition));
		}
	}
}
