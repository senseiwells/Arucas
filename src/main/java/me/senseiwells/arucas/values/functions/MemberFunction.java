package me.senseiwells.arucas.values.functions;

import me.senseiwells.arucas.extensions.BuiltInFunction;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.Value;

import java.util.List;

public class MemberFunction extends BuiltInFunction {
	public MemberFunction(String name, List<String> argumentNames, FunctionDefinition function) {
		super(name, argumentNames, function);
		argumentNames.add(0, "this");
	}

	public MemberFunction(String name, String argument, FunctionDefinition function) {
		this(name, List.of(argument), function);
	}

	@Override
	public <T extends Value<?>> T getParameterValueOfType(Context context, Class<T> clazz, int index, String additionalInfo) throws CodeError {
		if (index > 0) {
			return super.getParameterValueOfType(context, clazz, index, additionalInfo);
		}
		Value<?> value = this.getParameterValue(context, index);
		if (!clazz.isInstance(value)) {
			throw this.throwInvalidParameterError("Only %s can call the method %s()%s".formatted(
					clazz.getSimpleName(), this.value, additionalInfo == null ? "" : ("\n" + additionalInfo)
			), context);
		}
		return clazz.cast(value);
	}

	@Override
	public <T extends Value<?>> T getParameterValueOfType(Context context, Class<T> clazz, int index) throws CodeError {
		return this.getParameterValueOfType(context, clazz, index, null);
	}
}
