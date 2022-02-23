package me.senseiwells.arucas.values.functions;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasList;
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

	public BuiltInFunction(String name, FunctionDefinition<BuiltInFunction> function, String deprecatedMessage) {
		this(name, List.of(), function, deprecatedMessage);
	}

	@Override
	protected Value<?> execute(Context context, List<Value<?>> arguments) throws CodeError {
		this.checkDeprecated(context);
		this.checkAndPopulateArguments(context, arguments, this.argumentNames);
		return this.function.execute(context, this);
	}

	/**
	 * Arbitrary functions allow any number of parameters to be passed
	 * in and all values will be passed into the function as a list
	 */
	public static final class Arbitrary extends BuiltInFunction {
		public Arbitrary(String name, FunctionDefinition<BuiltInFunction> function) {
			this(name, function, null);
		}

		public Arbitrary(String name, FunctionDefinition<BuiltInFunction> function, String deprecatedMessage) {
			super(name, "arbitrary", function, deprecatedMessage);
		}

		@Override
		public int getParameterCount() {
			return -1;
		}

		@Override
		public void checkAndPopulateArguments(Context context, List<Value<?>> arguments, List<String> argumentNames) {
			ArucasList list = new ArucasList();
			// This can be empty
			if (arguments != null && !arguments.isEmpty()) {
				list.addAll(arguments);
			}
			context.setLocal("arbitrary", new ListValue(list));
		}
	}
}
