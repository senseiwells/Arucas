package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ErrorRuntime;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.Value;

public class TryNode extends Node {
	
	private final Node bodyNode;
	private final Node catchNode;
	
	public TryNode(Node bodyNode, Node catchNode) {
		super(bodyNode.token, bodyNode.startPos, catchNode.endPos);
		this.bodyNode = bodyNode;
		this.catchNode = catchNode;
	}

	@Override
	public Value<?> visit(Context context) throws CodeError, ThrowValue {
		context.pushScope(this.startPos);
		try {
			this.bodyNode.visit(context);
		}
		catch (ErrorRuntime e) {
			this.catchNode.visit(context);
		}
		context.popScope();
		return new NullValue();
	}
}
