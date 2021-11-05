package me.senseiwells.arucas.extensions;

import me.senseiwells.arucas.api.IArucasExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.MemberFunction;

import java.util.*;

public class ArucasMapMembers implements IArucasExtension {
	private final Object MAP_LOCK = new Object();

	@Override
	public Set<MemberFunction> getDefinedFunctions() {
		return this.mapFunctions;
	}

	@Override
	public String getName() {
		return "MapMemberFunctions";
	}

	private final Set<MemberFunction> mapFunctions = Set.of(
		new MemberFunction("get", "key", this::mapGet),
		new MemberFunction("getKeys", this::mapGetKeys),
		new MemberFunction("getValues", this::mapGetValues),
		new MemberFunction("put", List.of("key", "value"), this::mapPut),
		new MemberFunction("putIfAbsent", List.of("key", "value"), this::mapPutIfAbsent),
		new MemberFunction("putAll", "anotherMap", this::mapPutAll),
		new MemberFunction("remove", "key", this::mapRemove),
		new MemberFunction("clear", this::mapClear)
	);

	private Value<?> mapGet(Context context, MemberFunction function) throws CodeError {
		synchronized (MAP_LOCK) {
			MapValue mapValue = function.getParameterValueOfType(context, MapValue.class, 0);
			Value<?> key = function.getParameterValue(context, 1);
			if (key.value == null) {
				throw new RuntimeError("Cannot get null from a map", function.startPos, function.endPos, context);
			}
			Value<?> value = mapValue.value.get(key);
			return value == null ? new NullValue() : value.newCopy();
		}
	}

	private Value<?> mapGetKeys(Context context, MemberFunction function) throws CodeError {
		synchronized (MAP_LOCK) {
			MapValue mapValue = function.getParameterValueOfType(context, MapValue.class, 0);
			return new ListValue(mapValue.value.keySet().stream().toList());
		}
	}

	private Value<?> mapGetValues(Context context, MemberFunction function) throws CodeError {
		synchronized (MAP_LOCK) {
			MapValue mapValue = function.getParameterValueOfType(context, MapValue.class, 0);
			return new ListValue(mapValue.value.values().stream().toList());
		}
	}

	private Value<?> mapPut(Context context, MemberFunction function) throws CodeError {
		synchronized (MAP_LOCK) {
			MapValue mapValue = function.getParameterValueOfType(context, MapValue.class, 0);
			Value<?> key = function.getParameterValue(context, 1);
			Value<?> value = function.getParameterValue(context, 2);
			if (key.value == null || value.value == null) {
				throw new RuntimeError("Cannot put null into a map", function.startPos, function.endPos, context);
			}
			Value<?> returnValue = mapValue.value.put(key, value);
			return returnValue == null ? new NullValue() : returnValue.newCopy();
		}
	}

	private Value<?> mapPutIfAbsent(Context context, MemberFunction function) throws CodeError {
		synchronized (MAP_LOCK) {
			MapValue mapValue = function.getParameterValueOfType(context, MapValue.class, 0);
			Value<?> key = function.getParameterValue(context, 1);
			Value<?> value = function.getParameterValue(context, 2);
			if (key.value == null || value.value == null) {
				throw new RuntimeError("Cannot put null into a map", function.startPos, function.endPos, context);
			}
			Value<?> returnValue = mapValue.value.putIfAbsent(key, value);
			return returnValue == null ? new NullValue() : returnValue.newCopy();
		}
	}

	private Value<?> mapPutAll(Context context, MemberFunction function) throws CodeError {
		synchronized (MAP_LOCK) {
			MapValue mapValue = function.getParameterValueOfType(context, MapValue.class, 0);
			MapValue anotherMapValue = function.getParameterValueOfType(context, MapValue.class, 1);
			mapValue.value.putAll(anotherMapValue.value);
			return new NullValue();
		}
	}

	private Value<?> mapRemove(Context context, MemberFunction function) throws CodeError {
		synchronized (MAP_LOCK) {
			MapValue mapValue = function.getParameterValueOfType(context, MapValue.class, 0);
			Value<?> key = function.getParameterValue(context, 1);
			if (key.value == null) {
				throw new RuntimeError("Cannot remove null from a map", function.startPos, function.endPos, context);
			}
			Value<?> removedValue = mapValue.value.remove(key);
			return removedValue == null ? new NullValue() : removedValue.newCopy();
		}
	}

	private Value<?> mapClear(Context context, MemberFunction function) throws CodeError {
		synchronized (MAP_LOCK) {
			MapValue mapValue = function.getParameterValueOfType(context, MapValue.class, 0);
			mapValue.value.clear();
			return new NullValue();
		}
	}
}
