package me.senseiwells.arucas.values.functions.mod;

@SuppressWarnings("unused")
public class ConstructorFunction extends BuiltInFunction {
	protected ConstructorFunction(int parameters, FunctionDefinition function, String deprecationMessage) {
		super("", parameters, function, deprecationMessage);
	}

	public static ConstructorFunction of(int parameters, FunctionDefinition function, String deprecationMessage) {
		return new ConstructorFunction(parameters, function, deprecationMessage);
	}

	public static ConstructorFunction of(int parameters, FunctionDefinition function) {
		return of(parameters, function, null);
	}
}
