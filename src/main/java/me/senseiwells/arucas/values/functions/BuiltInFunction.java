package me.senseiwells.arucas.values.functions;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.Value;

import java.util.List;

public class BuiltInFunction extends FunctionValue {
	public final FunctionDefinition function;
	private boolean hasBeenWarned = false;

	protected BuiltInFunction(String name, int parameters, FunctionDefinition function, String deprecationMessage) {
		super(name, ISyntax.emptyOf("Function/" + name), parameters, deprecationMessage);
		this.function = function;
	}

	public static BuiltInFunction of(String name, int parameters, FunctionDefinition function, String deprecationMessage) {
		return new BuiltInFunction(name, parameters, function, deprecationMessage);
	}

	public static BuiltInFunction of(String name, int parameters, FunctionDefinition function) {
		return of(name, parameters, function, null);
	}

	public static BuiltInFunction of(String name, FunctionDefinition function, String deprecationMessage) {
		return of(name, 0, function, deprecationMessage);
	}

	public static BuiltInFunction of(String name, FunctionDefinition function) {
		return of(name, 0, function);
	}

	public static BuiltInFunction arbitrary(String name, FunctionDefinition function, String deprecationMessage) {
		return of(name, -1, function, deprecationMessage);
	}

	public static BuiltInFunction arbitrary(String name, FunctionDefinition function) {
		return arbitrary(name, function, null);
	}

	protected void checkDeprecation(Context context) {
		if (this.getDeprecationMessage() != null && !this.hasBeenWarned && !context.isSuppressDeprecated()) {
			context.printDeprecated(
				"The function %s() is deprecated and will be removed in the future! %s",
				this.getName(), this.getDeprecationMessage()
			);
			this.hasBeenWarned = true;
		}
	}

	@Override
	protected Value execute(Context context, List<Value> arguments) throws CodeError {
		this.checkDeprecation(context);
		return this.function.execute(context, this, arguments);
	}
}
