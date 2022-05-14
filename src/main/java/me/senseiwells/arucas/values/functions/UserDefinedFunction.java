package me.senseiwells.arucas.values.functions;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.nodes.Node;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.values.ListValue;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.Value;

import java.util.List;

public class UserDefinedFunction extends FunctionValue {
	protected Node bodyNode;
	protected Context localContext;

	public UserDefinedFunction(String name, List<String> argumentNames, ISyntax syntaxPosition) {
		super(name, syntaxPosition, argumentNames, null);
	}

	public void complete(Node bodyNode) {
		this.bodyNode = bodyNode;
	}

	public void setLocalContext(Context context) {
		this.localContext = context.createBranch();
	}

	@Override
	protected Value execute(Context context, List<Value> arguments) throws CodeError, ThrowValue {
		if (this.localContext != null) {
			context = this.localContext.createBranch();
		}
		context.pushFunctionScope(this.syntaxPosition);
		this.checkAndPopulateArguments(context, arguments, this.argumentNames);
		this.bodyNode.visit(context);
		context.popScope();
		return NullValue.NULL;
	}

	public static final class Arbitrary extends UserDefinedFunction {
		public Arbitrary(String name, String argumentName, ISyntax syntaxPosition) {
			super(name, List.of(argumentName), syntaxPosition);
		}

		@Override
		public int getParameterCount() {
			return -1;
		}

		@Override
		public void checkAndPopulateArguments(Context context, List<Value> arguments, List<String> argumentNames) {
			ArucasList list = new ArucasList();
			// This can be empty
			if (arguments != null && !arguments.isEmpty()) {
				list.addAll(arguments);
			}
			context.setLocal(this.argumentNames.get(0), new ListValue(list));
		}
	}
}
