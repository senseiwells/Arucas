package me.senseiwells.arucas.values.functions;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.values.ListValue;
import me.senseiwells.arucas.values.GenericValue;
import me.senseiwells.arucas.values.Value;

import java.util.ArrayList;
import java.util.List;

public class MemberFunction extends AbstractBuiltInFunction<MemberFunction> implements IMemberFunction {
	public MemberFunction(String name, List<String> argumentNames, FunctionDefinition<MemberFunction> function, String deprecationMessage) {
		super(name, addThis(argumentNames), function, deprecationMessage);
	}

	public MemberFunction(String name, List<String> argumentNames, FunctionDefinition<MemberFunction> function) {
		this(name, addThis(argumentNames), function, null);
	}

	public MemberFunction(String name, String argument, FunctionDefinition<MemberFunction> function) {
		this(name, List.of(argument), function, null);
	}

	public MemberFunction(String name, FunctionDefinition<MemberFunction> function) {
		this(name, List.of(), function, null);
	}

	public MemberFunction(String name, String argument, FunctionDefinition<MemberFunction> function, String deprecationMessage) {
		this(name, List.of(argument), function, deprecationMessage);
	}

	public MemberFunction(String name, FunctionDefinition<MemberFunction> function, String deprecationMessage) {
		this(name, List.of(), function, deprecationMessage);
	}

	public <T extends Value> T getThis(Context context, Class<T> clazz) throws CodeError {
		return this.getFirstParameter(context, clazz);
	}

	@Override
	protected Value execute(Context context, List<Value> arguments) throws CodeError {
		this.checkDeprecated(context);

		this.checkAndPopulateArguments(context, arguments, this.argumentNames);
		return this.function.execute(context, this);
	}

	@Override
	public <E extends Value> E getParameterValueOfType(Context context, Class<E> clazz, int index, String additionalInfo) throws CodeError {
		Value value = this.getParameterValue(context, index);
		if (!clazz.isInstance(value)) {
			throw this.throwInvalidParameterError("Must pass %s into parameter %d for %s()%s".formatted(
				clazz.getSimpleName(), index, this.value, additionalInfo == null ? "" : ("\n" + additionalInfo)
			), context);
		}
		return clazz.cast(value);
	}
	
	private static List<String> addThis(List<String> stringList) {
		if (stringList.isEmpty() || !stringList.get(0).equals("this")) {
			stringList = new ArrayList<>(stringList);
			stringList.add(0, "this");
		}
		return stringList;
	}

	@Override
	public FunctionValue setThisAndGet(Value thisValue) {
		return new Delegatable(thisValue, this);
	}

	/**
	 * Arbitrary functions allow any number of parameters to be passed
	 * in and all values will be passed into the function as a list
	 */
	public static final class Arbitrary extends MemberFunction {
		public Arbitrary(String name, FunctionDefinition<MemberFunction> function) {
			this(name, function, null);
		}

		public Arbitrary(String name, FunctionDefinition<MemberFunction> function, String deprecatedMessage) {
			super(name, "arbitrary", function, deprecatedMessage);
		}

		@Override
		public int getParameterCount() {
			return -1;
		}

		@Override
		public void checkAndPopulateArguments(Context context, List<Value> arguments, List<String> argumentNames) throws RuntimeError {
			if (arguments == null || arguments.isEmpty()) {
				// This should never happen as members always pass themselves in
				throw new RuntimeError("Value was not found in parameter", this.syntaxPosition, context);
			}

			context.setLocal("this", arguments.get(0));
			ArucasList list = new ArucasList();

			for (int i = 1; i < arguments.size(); i++) {
				list.add(arguments.get(i));
			}

			context.setLocal("arbitrary", new ListValue(list));
		}
	}

	private static class Delegatable extends MemberFunction {
		private final Value thisValue;

		public Delegatable(Value thisValue, MemberFunction function) {
			super(function.getName(), function.argumentNames, function.function, function.deprecatedMessage);
			this.thisValue = thisValue;
		}

		@Override
		protected Value execute(Context context, List<Value> arguments) throws CodeError {
			arguments.add(0, this.thisValue);
			return super.execute(context, arguments);
		}
	}
}
