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
	private Node bodyNode;

	public UserDefinedFunction(String name, ISyntax syntaxPosition, List<String> argumentNames) {
		super(name, syntaxPosition, argumentNames, false);
	}
	
	public void complete(Node bodyNode) {
		this.bodyNode = bodyNode;
	}

	public Value<?> execute(Context context, List<Value<?>> arguments) throws CodeError, ThrowValue {
		this.checkAndPopulateArguments(context, arguments, this.argumentNames);
		this.bodyNode.visit(context);
		return new NullValue();
	}
}
