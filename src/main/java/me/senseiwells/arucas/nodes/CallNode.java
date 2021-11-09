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
	protected final List<Node> argumentNodes;
	protected final Node callNode;

	public CallNode(Node callNode, List<Node> argumentNodes) {
		super(callNode.token, callNode.syntaxPosition, (argumentNodes.size() > 0 ? argumentNodes.get(argumentNodes.size() - 1) : callNode).syntaxPosition);
		this.argumentNodes = argumentNodes;
		this.callNode = callNode;
	}

	@Override
	public Value<?> visit(Context context) throws CodeError, ThrowValue {
		Value<?> callValue = this.callNode.visit(context);
		if (!(callValue instanceof FunctionValue)) {
			throw new RuntimeError("Cannot call the non function value '%s'".formatted(callValue), this.syntaxPosition, context);
		}
		
		if (Thread.currentThread().isInterrupted()) {
			throw new CodeError(CodeError.ErrorType.INTERRUPTED_ERROR, "", this.syntaxPosition);
		}
		
		List<Value<?>> argumentValues = new ArrayList<>();
		for (Node node : this.argumentNodes) {
			argumentValues.add(node.visit(context));
		}
		
		// We push a new scope to make StackTraces easier to read.
		context.pushScope(this.syntaxPosition);
		Value<?> result = ((FunctionValue) callValue).call(context, argumentValues);
		context.popScope();
		return result;
	}
}
