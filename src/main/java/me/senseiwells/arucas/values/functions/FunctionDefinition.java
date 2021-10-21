package me.senseiwells.arucas.values.functions;

import me.senseiwells.arucas.extensions.BuiltInFunction;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.Value;

public interface FunctionDefinition {

	Value<?> execute(Context context, BuiltInFunction builtInFunctionValue) throws CodeError;
	
}
