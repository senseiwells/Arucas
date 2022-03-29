package me.senseiwells.arucas.values.functions;

import me.senseiwells.arucas.values.Value;

public interface IMemberFunction {
	FunctionValue setThisAndGet(Value<?> thisValue);
}
