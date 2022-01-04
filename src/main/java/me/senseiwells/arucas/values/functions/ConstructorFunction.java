package me.senseiwells.arucas.values.functions;

import java.util.List;

@SuppressWarnings("unused")
public class ConstructorFunction extends BuiltInFunction {
	public ConstructorFunction(List<String> argumentNames, FunctionDefinition<BuiltInFunction> function, String deprecatedMessage) {
		super("", argumentNames, function, deprecatedMessage);
	}

	public ConstructorFunction(List<String> argumentNames, FunctionDefinition<BuiltInFunction> function) {
		super("", argumentNames, function);
	}

	public ConstructorFunction(String argument, FunctionDefinition<BuiltInFunction> function) {
		super("", argument, function);
	}

	public ConstructorFunction(FunctionDefinition<BuiltInFunction> function) {
		super("", function);
	}

	public ConstructorFunction(String argument, FunctionDefinition<BuiltInFunction> function, String deprecatedMessage) {
		super("", argument, function, deprecatedMessage);
	}
}
