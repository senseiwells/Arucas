package me.senseiwells.arucas.values.functions;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.nodes.Node;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.Value;

import java.util.List;

public class UserDefinedFunction extends FunctionValue {
	private final Node bodyNode;
	private final List<String> argumentNames;

	public UserDefinedFunction(String name, Node bodyNode, List<String> argumentNames) {
		super(name);
		this.bodyNode = bodyNode;
		this.argumentNames = argumentNames;
	}

	public Value<?> execute(Context context, List<Value<?>> arguments) throws CodeError, ThrowValue {
		this.checkAndPopulateArguments(context, arguments, this.argumentNames);
		this.bodyNode.visit(context);
		return new NullValue();
	}

	@Override
	public UserDefinedFunction copy() {
		return (UserDefinedFunction) new UserDefinedFunction(this.value, this.bodyNode, this.argumentNames)
			.setPos(this.startPos, this.endPos);
	}
}
