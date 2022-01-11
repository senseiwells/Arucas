package me.senseiwells.arucas.values.functions;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.*;

import java.util.*;

public class BuiltInFunction extends AbstractBuiltInFunction<BuiltInFunction> {
	public BuiltInFunction(String name, List<String> argumentNames, FunctionDefinition<BuiltInFunction> function, String deprecatedMessage) {
		super(name, argumentNames, function, deprecatedMessage);
	}

	public BuiltInFunction(String name, String argument, FunctionDefinition<BuiltInFunction> function, String deprecatedMessage) {
		this(name, List.of(argument), function, deprecatedMessage);
	}

	public BuiltInFunction(String name, List<String> argumentNames, FunctionDefinition<BuiltInFunction> function) {
		this(name, argumentNames, function, null);
	}

	public BuiltInFunction(String name, String argument, FunctionDefinition<BuiltInFunction> function) {
		this(name, List.of(argument), function, null);
	}

	public BuiltInFunction(String name, FunctionDefinition<BuiltInFunction> function) {
		this(name, List.of(), function, null);
	}

	@Override
	protected Value<?> execute(Context context, List<Value<?>> arguments) throws CodeError {
		this.checkDeprecated(context);
		this.checkAndPopulateArguments(context, arguments, this.argumentNames);
		return this.function.execute(context, this);
	}
}
