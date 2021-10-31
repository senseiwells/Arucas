package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.values.functions.FunctionValue;
import me.senseiwells.arucas.values.Value;

import java.util.ArrayList;
import java.util.List;

public class CallNode extends Node {
	public final List<Node> argumentNodes;
	public final Node callNode;

	public CallNode(Node callNode, List<Node> argumentNodes) {
		super(callNode.token, callNode.startPos, argumentNodes.size() > 0 ? argumentNodes.get(argumentNodes.size() - 1).endPos : callNode.endPos);
		this.argumentNodes = argumentNodes;
		this.callNode = callNode;
	}

	@Override
	public Value<?> visit(Context context) throws CodeError, ThrowValue {
		Value<?> callValue = this.callNode.visit(context);
		if (!(callValue instanceof FunctionValue)) {
			throw new RuntimeError("Cannot call the non function value '%s'".formatted(callValue), this.startPos, this.endPos, context);
		}
		
		if (Thread.currentThread().isInterrupted()) {
			throw new CodeError(CodeError.ErrorType.INTERRUPTED_ERROR, "", this.startPos, this.endPos);
		}
		
		List<Value<?>> argumentValues = new ArrayList<>();
		for (Node node : this.argumentNodes) {
			argumentValues.add(node.visit(context));
		}
		
		callValue = callValue.copy().setPos(this.startPos, this.endPos);
		Value<?> functionValue = ((FunctionValue) callValue).call(context, argumentValues);
		return functionValue.setPos(this.startPos, this.endPos);
	}
}
