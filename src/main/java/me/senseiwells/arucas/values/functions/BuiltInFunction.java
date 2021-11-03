package me.senseiwells.arucas.values.functions;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.*;

import java.util.*;

public class BuiltInFunction extends AbstractBuiltInFunction<BuiltInFunction> {
	public BuiltInFunction(String name, List<String> argumentNames, FunctionDefinition<BuiltInFunction> function) {
		super(name, argumentNames, function);
	}

	public BuiltInFunction(String name, String argument, FunctionDefinition<BuiltInFunction> function) {
		this(name, List.of(argument), function);
	}

	public BuiltInFunction(String name, FunctionDefinition<BuiltInFunction> function) {
		this(name, List.of(), function);
	}

	@Override
	public Value<?> execute(Context context, List<Value<?>> arguments) throws CodeError {
		this.checkAndPopulateArguments(context, arguments, this.argumentNames);
		return this.function.execute(context, this);
	}

	@Override
	public Value<?> copy() {
		return new BuiltInFunction(this.value, this.argumentNames, this.function).setPos(this.startPos, this.endPos);
	}
}
