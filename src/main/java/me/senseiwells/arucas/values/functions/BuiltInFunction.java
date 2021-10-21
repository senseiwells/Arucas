package me.senseiwells.arucas.values.functions;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.*;

import java.util.*;

public class BuiltInFunction extends FunctionValue {
	public final FunctionDefinition function;
	public final List<String> argumentNames;

	public BuiltInFunction(String name, List<String> argumentNames, FunctionDefinition function) {
		super(name);
		this.function = function;
		this.argumentNames = argumentNames;
	}

	public BuiltInFunction(String name, String argument, FunctionDefinition function) {
		this(name, List.of(argument), function);
	}

	public BuiltInFunction(String name, FunctionDefinition function) {
		this(name, List.of(), function);
	}
	
	@Override
	public Value<?> execute(Context context, List<Value<?>> arguments) throws CodeError {
		this.checkAndPopulateArguments(arguments, this.argumentNames, this.context);
		return this.function.execute(this);
	}

	public BooleanValue isType(Class<?> classInstance) {
		return new BooleanValue(classInstance.isInstance(this.getValueFromTable(this.argumentNames.get(0))));
	}
	
	public <T extends Value<?>> T getValueForType(Class<T> clazz, int index, String additionalInfo) throws CodeError {
		Value<?> value = this.getValueFromTable(this.argumentNames.get(index));
		if (!(clazz.isInstance(value)))
			throw this.throwInvalidParameterError("Must pass " + clazz.getSimpleName() + " into parameter " + (index + 1) + " for " + this.value + "()" + (additionalInfo == null ? "" : "\n" + additionalInfo));
		return clazz.cast(value);
	}

	@Override
	public Value<?> copy() {
		return new BuiltInFunction(this.value, this.argumentNames, this.function).setPos(this.startPos, this.endPos).setContext(this.context);
	}
}
