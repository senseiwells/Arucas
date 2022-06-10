package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.throwables.ArucasRuntimeError;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.StackTable;
import me.senseiwells.arucas.values.ErrorValue;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.Value;

public class TryNode extends Node {
	private final Node bodyNode;
	private final Node catchNode;
	private final String catchParameterName;

	public TryNode(Node bodyNode, Node catchNode, String catchParameterName) {
		super(bodyNode.token, bodyNode.syntaxPosition, catchNode.syntaxPosition);
		this.bodyNode = bodyNode;
		this.catchNode = catchNode;
		this.catchParameterName = catchParameterName;
	}

	@Override
	public Value visit(Context context) throws CodeError, ThrowValue {
		StackTable originalScope = context.getStackTable();
		context.pushScope(this.syntaxPosition);
		try {
			this.bodyNode.visit(context);
		}
		catch (RuntimeError e) {
			context.moveScope(originalScope);
			context.pushScope(this.syntaxPosition);

			Value value = e instanceof ArucasRuntimeError are ? are.getErrorValue() : new ErrorValue(e);
			context.setLocal(this.catchParameterName, value);
			this.catchNode.visit(context);
		}
		context.popScope();
		return NullValue.NULL;
	}
}
