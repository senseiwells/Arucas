package me.senseiwells.arucas.extensions;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.functions.BuiltInFunction;

import java.util.Map;

public class ArucasDebugClass extends ArucasClassExtension {
	public ArucasDebugClass() {
		super("Debug");
	}

	@Override
	public Map<String, Value<?>> getDefinedStaticVariables() {
		return Map.of();
	}

	@Override
	public ArucasFunctionMap<BuiltInFunction> getDefinedStaticMethods() {
		return ArucasFunctionMap.of(
			new BuiltInFunction("debug", this::debug)
		);
	}

	private Value<?> debug(Context context, BuiltInFunction function) {
		context.dumpScopes();
		return NullValue.NULL;
	}
	
	@Override
	public Class<?> getValueClass() {
		return null;
	}
}
