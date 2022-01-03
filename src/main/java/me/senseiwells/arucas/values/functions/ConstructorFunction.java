package me.senseiwells.arucas.values.functions;

import java.util.List;

@SuppressWarnings("unused")
public class ConstructorFunction extends MemberFunction {
	public ConstructorFunction(List<String> argumentNames, FunctionDefinition<MemberFunction> function, String deprecatedMessage) {
		super("", argumentNames, function, deprecatedMessage);
	}

	public ConstructorFunction(List<String> argumentNames, FunctionDefinition<MemberFunction> function) {
		super("", argumentNames, function);
	}

	public ConstructorFunction(String argument, FunctionDefinition<MemberFunction> function) {
		super("", argument, function);
	}

	public ConstructorFunction(FunctionDefinition<MemberFunction> function) {
		super("", function);
	}

	public ConstructorFunction(String argument, FunctionDefinition<MemberFunction> function, String deprecatedMessage) {
		super("", argument, function, deprecatedMessage);
	}

	public ConstructorFunction(FunctionDefinition<MemberFunction> function, String deprecatedMessage) {
		super("", function, deprecatedMessage);
	}
}
