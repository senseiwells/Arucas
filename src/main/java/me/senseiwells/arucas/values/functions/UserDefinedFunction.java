package me.senseiwells.arucas.values.functions;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.nodes.Node;
import me.senseiwells.arucas.utils.Context;
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
		this.localContext = context;
	}

	protected Value<?> execute(Context context, List<Value<?>> arguments) throws CodeError, ThrowValue {
		if (this.localContext != null) {
			context = this.localContext;
		}
		this.checkAndPopulateArguments(context, arguments, this.argumentNames);
		this.bodyNode.visit(context);
		return NullValue.NULL;
	}
}
