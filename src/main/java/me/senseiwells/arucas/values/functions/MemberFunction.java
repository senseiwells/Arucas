package me.senseiwells.arucas.values.functions;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.Value;

import java.util.ArrayList;
import java.util.List;

public class MemberFunction extends AbstractBuiltInFunction<MemberFunction> {
	public MemberFunction(String name, List<String> argumentNames, FunctionDefinition<MemberFunction> function, String isDeprecated) {
		super(name, addThis(argumentNames), function, isDeprecated);
	}

	public MemberFunction(String name, List<String> argumentNames, FunctionDefinition<MemberFunction> function) {
		this(name, addThis(argumentNames), function, null);
	}

	public MemberFunction(String name, String argument, FunctionDefinition<MemberFunction> function) {
		this(name, List.of(argument), function, null);
	}

	public MemberFunction(String name, FunctionDefinition<MemberFunction> function) {
		// TODO: Remove empty redundant allocations
		this(name, List.of(), function, null);
	}

	public MemberFunction(String name, String argument, FunctionDefinition<MemberFunction> function, String isDeprecated) {
		this(name, List.of(argument), function, isDeprecated);
	}

	public MemberFunction(String name, FunctionDefinition<MemberFunction> function, String isDeprecated) {
		// TODO: Remove empty redundant allocations
		this(name, List.of(), function, isDeprecated);
	}

	public <T extends Value<?>> T getThis(Context context, Class<T> clazz) throws CodeError {
		return this.getParameterValueOfType(context, clazz, 0);
	}

	@Override
	protected Value<?> execute(Context context, List<Value<?>> arguments) throws CodeError {
		this.checkDeprecated(context);
		this.checkAndPopulateArguments(context, arguments, this.argumentNames);
		return this.function.execute(context, this);
	}
	
	private static List<String> addThis(List<String> stringList) {
		if (stringList.isEmpty() || !stringList.get(0).equals("this")) {
			stringList = new ArrayList<>(stringList);
			stringList.add(0, "this");
		}
		return stringList;
	}
}
