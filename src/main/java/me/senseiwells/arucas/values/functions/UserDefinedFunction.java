package me.senseiwells.arucas.values.functions;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.nodes.Node;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.TypedValue;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.values.GenericValue;
import me.senseiwells.arucas.values.ListValue;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.classes.AbstractClassDefinition;
import me.senseiwells.arucas.values.classes.ArucasClassDefinition;

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

	protected void checkAndPopulateArguments(Context context, List<Value> arguments) throws CodeError {
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

			argument = this.checkTypes(context, argument, argumentDef.getTypes(), "Parameter " + (i + 1));

			context.setLocal(argumentDef.getName(), argument);
		}
	}

	protected Value checkTypes(Context context, Value value, List<AbstractClassDefinition> types, String messageStart) throws CodeError {
		if (types != null && !types.isEmpty()) {
			AbstractClassDefinition argumentType = value.getType(context).value;
			if (!types.contains(argumentType)) {
				if (argumentType instanceof ArucasClassDefinition classArgumentType) {
					for (AbstractClassDefinition validTypes : types) {
						UserDefinedClassFunction function = classArgumentType.getCastMethod(validTypes);
						if (function != null) {
							return function.call(context, ArucasList.arrayListOf(value));
						}
					}
				}

				throw new RuntimeError("%s got '%s', but expected type '%s'".formatted(
					messageStart, value.getTypeName(), TypedValue.typesAsString(types)
				), this.getPosition(), context);
			}
		}
		return value;
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
	protected Value onReturnValue(Context context, Value returnValue) throws CodeError {
		return this.checkTypes(context, returnValue, this.returnTypes, "Function return");
	}

	@Override
	protected Value execute(Context context, List<Value> arguments) throws CodeError {
		this.checkAndPopulateArguments(context, arguments);
		this.bodyNode.visit(context);
		return NullValue.NULL;
	}

	@Override
	public UserDefinedFunction copy(Context context) throws CodeError {
		UserDefinedFunction copiedFunction = new UserDefinedFunction(this.getName(), this.arguments, this.getPosition());
		copiedFunction.setReturnTypes(this.returnTypes);
		copiedFunction.setLocalContext(this.localContext);
		copiedFunction.complete(this.bodyNode);
		return copiedFunction;
	}

	public static final class Arbitrary extends UserDefinedFunction {
		public Arbitrary(String name, String argumentName, ISyntax position) {
			this(name, List.of(new Argument(argumentName)), position);
		}

		private Arbitrary(String name, List<Argument> arguments, ISyntax position) {
			super(name, arguments, position);
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

		@Override
		public UserDefinedFunction copy(Context context) throws CodeError {
			return new Arbitrary(this.getName(), this.arguments, this.getPosition());
		}
	}
}
