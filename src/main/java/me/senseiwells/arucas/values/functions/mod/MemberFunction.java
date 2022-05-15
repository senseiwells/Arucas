package me.senseiwells.arucas.values.functions.mod;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.Value;

import java.util.List;

public class MemberFunction extends BuiltInFunction implements IMemberFunction {
	protected MemberFunction(String name, int parameters, FunctionDefinition function, String deprecationMessage) {
		super(name, parameters + 1, function, deprecationMessage);
	}

	public static MemberFunction of(String name, int parameters, FunctionDefinition function, String deprecationMessage) {
		return new MemberFunction(name, parameters, function, deprecationMessage);
	}

	public static MemberFunction of(String name, int parameters, FunctionDefinition function) {
		return of(name, parameters, function, null);
	}

	public static MemberFunction arbitrary(String name, FunctionDefinition function, String deprecationMessage) {
		return new MemberFunction(name, -2, function, deprecationMessage);
	}

	public static MemberFunction arbitrary(String name, FunctionDefinition function) {
		return arbitrary(name, function, null);
	}

	@Override
	public MemberFunction setThisAndGet(Value thisValue) {
		return new Delegate(thisValue, this);
	}

	private static class Delegate extends MemberFunction {
		private final Value thisValue;

		protected Delegate(Value thisValue, MemberFunction function) {
			super(function.getName(), function.getCount(), function.function, function.getDeprecationMessage());
			this.thisValue = thisValue;
		}

		@Override
		protected Value execute(Context context, List<Value> arguments) throws CodeError {
			arguments.add(0, this.thisValue);
			return super.execute(context, arguments);
		}
	}
}
