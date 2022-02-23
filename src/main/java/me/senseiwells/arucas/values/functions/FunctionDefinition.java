package me.senseiwells.arucas.values.functions;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.Value;

@FunctionalInterface
public interface FunctionDefinition<T extends AbstractBuiltInFunction<?>> {
	Value<?> execute(Context context, T function) throws CodeError;
}
