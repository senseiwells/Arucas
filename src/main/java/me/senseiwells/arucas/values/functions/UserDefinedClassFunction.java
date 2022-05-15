package me.senseiwells.arucas.values.functions;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.values.ListValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.classes.ArucasClassDefinition;
import me.senseiwells.arucas.values.classes.ArucasClassValue;

import java.util.List;
import java.util.Objects;

public class UserDefinedClassFunction extends UserDefinedFunction implements IMemberFunction {
	private final ArucasClassDefinition definition;

	public UserDefinedClassFunction(ArucasClassDefinition definition, String name, List<String> argumentNames, ISyntax syntaxPosition) {
		super(name, argumentNames, syntaxPosition);
		this.definition = definition;
	}

	@Override
	protected Value execute(Context context, List<Value> arguments) throws CodeError {
		return super.execute(context, arguments);
	}

	@Override
	public String getAsString(Context context) throws CodeError {
		return "<class " + this.definition.getName() + "::" + this.getName() + "@" + Integer.toHexString(Objects.hashCode(this)) + ">";
	}

	@Override
	public UserDefinedClassFunction getDelegate(Value thisValue) {
		if (thisValue instanceof ArucasClassValue classValue) {
			return new Delegate(classValue, this);
		}
		return null;
	}

	private static class Delegate extends UserDefinedClassFunction {
		private final ArucasClassValue thisValue;

		public Delegate(ArucasClassValue classValue, UserDefinedClassFunction function) {
			super(function.definition, function.getName(), function.argumentNames, function.getPosition());
			this.complete(function.bodyNode);
			this.thisValue = classValue;
		}

		@Override
		protected Value execute(Context context, List<Value> arguments) throws CodeError {
			arguments.add(0, this.thisValue);
			return super.execute(context, arguments);
		}
	}

	public static final class Arbitrary extends UserDefinedClassFunction {
		public Arbitrary(ArucasClassDefinition definition, String name, List<String> argumentNames, ISyntax syntaxPosition) {
			super(definition, name, argumentNames, syntaxPosition);
		}

		@Override
		protected void checkAndPopulateArguments(Context context, List<Value> arguments) {
			for (int i = 0; i < this.argumentNames.size(); i++) {
				String argumentName = this.argumentNames.get(i);
				Value argument = arguments.get(i);
				context.setLocal(argumentName, argument);
			}
		}

		@Override
		public int getCount() {
			return -1;
		}

		@Override
		protected Value execute(Context context, List<Value> arguments) throws CodeError {
			if (arguments.isEmpty()) {
				throw new RuntimeException("'this' was not passed into the function");
			}
			Value thisValue = arguments.get(0);

			ArucasList varArgs = new ArucasList();
			varArgs.addAll(arguments.subList(1, arguments.size()));

			return super.execute(context, ArucasList.arrayListOf(thisValue, new ListValue(varArgs)));
		}
	}
}
