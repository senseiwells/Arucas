package me.senseiwells.arucas.values.functions;

import me.senseiwells.arucas.values.Value;

public interface Delegatable {
	FunctionValue getDelegate(Value thisValue);
}
