package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.functions.FunctionValue;
import me.senseiwells.arucas.values.functions.MemberFunction;

import java.util.ArrayList;
import java.util.List;

public class MemberCallNode extends CallNode {
	private final Node valueNode;

	public MemberCallNode(Node leftNode, Node rightNode, List<Node> argumentNodes) {
		super(rightNode, argumentNodes);
		this.valueNode = leftNode;
	}

	@Override
	public Value<?> visit(Context context) throws CodeError, ThrowValue {
		Value<?> callValue = this.callNode.visit(context);
		if (!(callValue instanceof FunctionValue)) {
			throw new RuntimeError("Cannot call a non function value '%s'".formatted(callValue), this.syntaxPosition, context);
		}
		if (!(callValue instanceof MemberFunction)) {
			throw new RuntimeError("Cannot call %s() as a member function".formatted(callValue), this.syntaxPosition, context);
		}

		List<Value<?>> argumentValues = new ArrayList<>();
		argumentValues.add(this.valueNode.visit(context));
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
