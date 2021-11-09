package me.senseiwells.arucas.values.functions;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.*;

import java.util.*;

public class BuiltInFunction extends AbstractBuiltInFunction<BuiltInFunction> {
	public BuiltInFunction(String name, List<String> argumentNames, FunctionDefinition<BuiltInFunction> function, boolean isDeprecated) {
		super(name, argumentNames, function, isDeprecated);
	}

	public BuiltInFunction(String name, String argument, FunctionDefinition<BuiltInFunction> function, boolean isDeprecated) {
		this(name, List.of(argument), function, isDeprecated);
	}

	public BuiltInFunction(String name, List<String> argumentNames, FunctionDefinition<BuiltInFunction> function) {
		this(name, argumentNames, function, false);
	}

	public BuiltInFunction(String name, String argument, FunctionDefinition<BuiltInFunction> function) {
		this(name, List.of(argument), function, false);
	}

	public BuiltInFunction(String name, FunctionDefinition<BuiltInFunction> function) {
		this(name, List.of(), function, false);
	}

	@Override
	public Value<?> execute(Context context, List<Value<?>> arguments) throws CodeError {
		this.checkDeprecated(context);
		this.checkAndPopulateArguments(context, arguments, this.argumentNames);
		return this.function.execute(context, this);
	}

	@Override
	public BuiltInFunction copy() {
		return (BuiltInFunction) new BuiltInFunction(this.value, this.argumentNames, this.function, this.isDeprecated)
			.setPos(this.startPos, this.endPos);
	}
}
