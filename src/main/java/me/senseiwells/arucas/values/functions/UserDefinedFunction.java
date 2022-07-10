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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class UserDefinedFunction extends FunctionValue {
	protected final List<Argument> arguments;
	protected List<AbstractClassDefinition> returnTypes;
	protected Node bodyNode;
	protected Context localContext;

	public UserDefinedFunction(String name, List<Argument> arguments, ISyntax position) {
		super(name, position, arguments.size(), null);
		this.arguments = arguments;
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
			Argument argumentDef = this.arguments.get(i);
			Value argument = arguments.get(i);

			this.checkTypes(context, argument, argumentDef.getTypes(), "Parameter " + (i + 1));

			context.setLocal(argumentDef.getName(), argument);
		}
	}

	protected void checkTypes(Context context, Value value, List<AbstractClassDefinition> types, String messageStart) throws RuntimeError {
		if (types != null && !types.isEmpty()) {
			AbstractClassDefinition argumentType = context.getClassDefinition(value.getTypeName());
			if (!types.contains(argumentType)) {
				throw new RuntimeError("%s got '%s', but expected type '%s'".formatted(
					messageStart, value.getTypeName(), this.typesAsString(types)
				), this.getPosition(), context);
			}
		}
	}

	private String typesAsString(Collection<AbstractClassDefinition> collection) {
		StringBuilder builder = new StringBuilder();
		Iterator<AbstractClassDefinition> iterator = collection.iterator();
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
		this.checkTypes(context, returnValue, this.returnTypes, "Function return");
	}

	@Override
	protected Value execute(Context context, List<Value> arguments) throws CodeError {
		this.checkAndPopulateArguments(context, arguments);
		this.bodyNode.visit(context);
		return NullValue.NULL;
	}

	public static final class Arbitrary extends UserDefinedFunction {
		public Arbitrary(String name, String argumentName, ISyntax position) {
			super(name, List.of(new Argument(argumentName)), position);
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
			context.setLocal(this.arguments.get(0).getName(), new ListValue(list));
		}
	}
}
