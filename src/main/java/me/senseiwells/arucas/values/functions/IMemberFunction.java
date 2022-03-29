package me.senseiwells.arucas.values.functions;

import me.senseiwells.arucas.values.Value;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public interface IMemberFunction {
	@Nullable FunctionValue setThisAndGet(Value<?> thisValue);
}
