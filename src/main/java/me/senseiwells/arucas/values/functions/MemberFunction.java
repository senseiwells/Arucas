package me.senseiwells.arucas.values.functions;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.Value;

import java.util.List;

public class MemberFunction extends AbstractBuiltInFunction<MemberFunction> {
	public MemberFunction(String name, List<String> argumentNames, FunctionDefinition<MemberFunction> function, String isDeprecated) {
		super(name, argumentNames, function, isDeprecated);
	}

	public MemberFunction(String name, List<String> argumentNames, FunctionDefinition<MemberFunction> function) {
		super(name, argumentNames, function, null);
	}

	public MemberFunction(String name, String argument, FunctionDefinition<MemberFunction> function) {
		this(name, List.of(argument), function, null);
	}

	public MemberFunction(String name, FunctionDefinition<MemberFunction> function) {
		this(name, List.of(), function, null);
	}

	public MemberFunction(String name, String argument, FunctionDefinition<MemberFunction> function, String isDeprecated) {
		this(name, List.of(argument), function, isDeprecated);
	}

	public MemberFunction(String name, FunctionDefinition<MemberFunction> function, String isDeprecated) {
		this(name, List.of(), function, isDeprecated);
	}

	@Override
	protected Value<?> execute(Context context, List<Value<?>> arguments) throws CodeError {
		this.checkDeprecated(context);
		this.checkAndPopulateArguments(context, arguments, this.argumentNames);
		return this.function.execute(context, this);
	}
}
