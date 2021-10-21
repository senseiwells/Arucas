package me.senseiwells.arucas.extensions;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.FunctionDefinition;
import me.senseiwells.arucas.values.functions.FunctionValue;

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
		this.checkAndPopulateArguments(context, arguments, this.argumentNames);
		return this.function.execute(context, this);
	}

	public BooleanValue isType(Context context, Class<?> classInstance) {
		return new BooleanValue(classInstance.isInstance(this.getParameterValue(context, 0)));
	}
	
	public Value<?> getParameterValue(Context context, int index) {
		return context.getVariable(this.argumentNames.get(index));
	}
	
	public <T extends Value<?>> T getParameterValueOfType(Context context, Class<T> clazz, int index) throws CodeError {
		return this.getParameterValueOfType(context, clazz, index, null);
	}
	
	public <T extends Value<?>> T getParameterValueOfType(Context context, Class<T> clazz, int index, String additionalInfo) throws CodeError {
		Value<?> value = this.getParameterValue(context, index);
		if (!clazz.isInstance(value)) {
			throw this.throwInvalidParameterError("Must pass %s into parameter %d for %s()%s".formatted(
				clazz.getSimpleName(), index + 1, this.value, additionalInfo == null ? "" : ("\n" + additionalInfo)
			));
		}
		return clazz.cast(value);
	}

	@Override
	public Value<?> copy() {
		return new BuiltInFunction(this.value, this.argumentNames, this.function).setPos(this.startPos, this.endPos);
	}
}
