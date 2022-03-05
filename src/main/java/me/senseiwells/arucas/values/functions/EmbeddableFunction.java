package me.senseiwells.arucas.values.functions;

import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.classes.AbstractClassDefinition;

import java.util.function.Supplier;

public interface EmbeddableFunction {
	void setCallingMember(Supplier<Value<?>> supplier);
	void setDefinition(AbstractClassDefinition definition);
}
