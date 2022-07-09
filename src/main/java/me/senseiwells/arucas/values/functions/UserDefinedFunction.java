package me.senseiwells.arucas.values.functions;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.nodes.Node;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.values.ListValue;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.classes.AbstractClassDefinition;

import java.util.Iterator;
import java.util.List;

public class UserDefinedFunction extends FunctionValue {
	protected final List<String> argumentNames;
	protected List<AbstractClassDefinition> returnTypes;
	protected Node bodyNode;
	protected Context localContext;

	public UserDefinedFunction(String name, List<String> argumentNames, ISyntax position) {
		super(name, position, argumentNames.size(), null);
		this.argumentNames = argumentNames;
	}

	public void complete(Node bodyNode) {
		this.bodyNode = bodyNode;
	}

	public void setLocalContext(Context context) {
		if (context != null) {
			this.localContext = context.createBranch();
		}
	}

	public void setReturnTypes(List<AbstractClassDefinition> returnType) {
		this.returnTypes = returnType;
	}

	protected void checkAndPopulateArguments(Context context, List<Value> arguments) throws RuntimeError {
		if (arguments.size() > this.getCount()) {
			throw this.getError(
				context, "%d too many arguments passed into %s",
				arguments.size() - this.getCount(), this.getName()
			);
		}
		if (arguments.size() < this.getCount()) {
			throw this.getError(
				context, "%d too few arguments passed into %s",
				this.getCount() - arguments.size(), this.getName()
			);
		}

		for (int i = 0; i < this.getCount(); i++) {
			String argumentName = this.argumentNames.get(i);
			Value argument = arguments.get(i);
			context.setLocal(argumentName, argument);
		}
	}

	private String returnValuesAsString() {
		StringBuilder builder = new StringBuilder();
		Iterator<AbstractClassDefinition> iterator = this.returnTypes.iterator();
		while (iterator.hasNext()) {
			AbstractClassDefinition definition = iterator.next();
			builder.append(definition.getName());

			if (iterator.hasNext()) {
				builder.append(" | ");
			}
		}
		return builder.toString();
	}

	@Override
	protected Context getContext(Context context) {
		if (this.localContext != null) {
			// This breaks stack traces
			return this.localContext.createBranch();
		}
		return context;
	}

	@Override
	protected void onReturnValue(Context context, Value returnValue) throws CodeError {
		AbstractClassDefinition returnType = context.getClassDefinition(returnValue.getTypeName());
		if (this.returnTypes != null && !this.returnTypes.isEmpty() && !this.returnTypes.contains(returnType)) {
			throw new RuntimeError("Function returned type '%s', but expected type '%s'".formatted(
				returnValue.getTypeName(), this.returnValuesAsString()
			), this.getPosition(), context);
		}
	}

	@Override
	protected Value execute(Context context, List<Value> arguments) throws CodeError {
		this.checkAndPopulateArguments(context, arguments);
		this.bodyNode.visit(context);
		return NullValue.NULL;
	}

	public static final class Arbitrary extends UserDefinedFunction {
		public Arbitrary(String name, String argumentName, ISyntax position) {
			super(name, List.of(argumentName), position);
		}

		@Override
		public int getCount() {
			return -1;
		}

		@Override
		protected void checkAndPopulateArguments(Context context, List<Value> arguments) {
			ArucasList list = new ArucasList();
			// This can be empty
			if (!arguments.isEmpty()) {
				list.addAll(arguments);
			}
			context.setLocal(this.argumentNames.get(0), new ListValue(list));
		}
	}
}
