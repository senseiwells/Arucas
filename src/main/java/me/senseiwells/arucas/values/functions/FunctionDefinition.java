package me.senseiwells.arucas.values.functions;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Arguments;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.Value;

import java.util.List;

@FunctionalInterface
public interface FunctionDefinition {
	Value execute(Arguments arguments) throws CodeError;

	default Value execute(Context context, FunctionValue function, List<Value> values) throws CodeError {
		return this.execute(new Arguments(context, function, values));
	}
}
