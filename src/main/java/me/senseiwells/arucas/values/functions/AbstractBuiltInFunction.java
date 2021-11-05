package me.senseiwells.arucas.values.functions;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.BooleanValue;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.Value;

import java.util.List;

public abstract class AbstractBuiltInFunction<S extends AbstractBuiltInFunction<?>> extends FunctionValue {
	public final FunctionDefinition<S> function;
	public final List<String> argumentNames;
	public final boolean isDeprecated;

	public AbstractBuiltInFunction(String name, List<String> argumentNames, FunctionDefinition<S> function, boolean isDeprecated) {
		super(name);
		this.function = function;
		this.argumentNames = argumentNames;
		this.isDeprecated = isDeprecated;
	}

	public BooleanValue isType(Context context, Class<?> classInstance) {
		return new BooleanValue(classInstance.isInstance(this.getParameterValue(context, 0)));
	}

	public Value<?> getParameterValue(Context context, int index) {
		Value<?> param = context.getVariable(this.argumentNames.get(index));
		return param == null ? new NullValue() : param;
	}

	public void checkDeprecated(Context context) {
		if (this.isDeprecated && !context.isSuppressDeprecated()) {
			context.printDeprecated("The function %s() is deprecated and will be removed!".formatted(this.value));
		}
	}

	public <T extends Value<?>> T getParameterValueOfType(Context context, Class<T> clazz, int index) throws CodeError {
		return this.getParameterValueOfType(context, clazz, index, null);
	}

	public <T extends Value<?>> T getParameterValueOfType(Context context, Class<T> clazz, int index, String additionalInfo) throws CodeError {
		Value<?> value = this.getParameterValue(context, index);
		if (!clazz.isInstance(value)) {
			throw this.throwInvalidParameterError("Must pass %s into parameter %d for %s()%s".formatted(
					clazz.getSimpleName(), index + 1, this.value, additionalInfo == null ? "" : ("\n" + additionalInfo)
			), context);
		}
		return clazz.cast(value);
	}
}
