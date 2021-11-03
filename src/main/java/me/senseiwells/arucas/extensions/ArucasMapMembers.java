package me.senseiwells.arucas.extensions;

import me.senseiwells.arucas.api.IArucasExtension;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.MemberFunction;

import java.util.*;

public class ArucasMapMembers implements IArucasExtension {

	@Override
	public Set<MemberFunction> getDefinedFunctions() {
		return this.mapFunctions;
	}

	@Override
	public String getName() {
		return "MapMemberFunctions";
	}

	private final Set<MemberFunction> mapFunctions = Set.of(
		new MemberFunction("get", "key", (context, function) -> {
			MapValue mapValue = function.getParameterValueOfType(context, MapValue.class, 0);
			Value<?> key = function.getParameterValue(context, 1);
			Value<?> value = mapValue.value.get(key);
			return value == null ? new NullValue() : value;
		}),

		new MemberFunction("put", List.of("key", "value"), (context, function) -> {
			MapValue mapValue = function.getParameterValueOfType(context, MapValue.class, 0);
			Value<?> key = function.getParameterValue(context, 1);
			Value<?> value = function.getParameterValue(context, 2);
			return mapValue.value.put(key, value);
		}),

		new MemberFunction("remove", "key", (context, function) -> {
			MapValue mapValue = function.getParameterValueOfType(context, MapValue.class, 0);
			Value<?> key = function.getParameterValue(context, 1);
			return mapValue.value.remove(key);
		}),

		new MemberFunction("putAll", "anotherMap", (context, function) -> {
			MapValue mapValue = function.getParameterValueOfType(context, MapValue.class, 0);
			MapValue anotherMapValue = function.getParameterValueOfType(context, MapValue.class, 1);
			mapValue.value.putAll(anotherMapValue.value);
			return new NullValue();
		}),

		new MemberFunction("putIfAbsent", List.of("key", "value"), (context, function) -> {
			MapValue mapValue = function.getParameterValueOfType(context, MapValue.class, 0);
			Value<?> key = function.getParameterValue(context, 1);
			Value<?> value = function.getParameterValue(context, 2);
			return mapValue.value.putIfAbsent(key, value);
		}),

		new MemberFunction("getKeys", (context, function) -> {
			MapValue mapValue = function.getParameterValueOfType(context, MapValue.class, 0);
			return new ListValue(mapValue.value.keySet().stream().toList());
		}),

		new MemberFunction("getValues", (context, function) -> {
			MapValue mapValue = function.getParameterValueOfType(context, MapValue.class, 0);
			return new ListValue(mapValue.value.values().stream().toList());
		}),

		new MemberFunction("clear", (context, function) -> {
			MapValue mapValue = function.getParameterValueOfType(context, MapValue.class, 0);
			mapValue.value.clear();
			return new NullValue();
		})
	);
}
