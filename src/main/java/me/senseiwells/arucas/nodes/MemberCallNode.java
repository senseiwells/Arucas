package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.StringValue;
import me.senseiwells.arucas.values.Value;
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
		// The value node holds the Value<?> we which to call this member function on.
		Value<?> memberValue = this.valueNode.visit(context);
		
		// The call node is the MemberAccessNode that just contains a string.
		StringValue memberFunctionName = (StringValue)this.callNode.visit(context);
		
		// Get the member function with the context calls.
		MemberFunction memberFunction = context.getMemberFunction(memberValue, memberFunctionName.value, argumentNodes.size() + 1);
		
		if (memberFunction == null) {
			throw new RuntimeError("Member function '%s' was not defined for the value type '%s'".formatted(
				memberFunctionName,
				memberValue.getClass().getSimpleName()
			), this.syntaxPosition, context);
		}

		List<Value<?>> argumentValues = new ArrayList<>();
		argumentValues.add(this.valueNode.visit(context));
		for (Node node : this.argumentNodes) {
			argumentValues.add(node.visit(context));
		}
		
		// We push a new scope to make StackTraces easier to read.
		context.pushScope(this.syntaxPosition);
		Value<?> result = memberFunction.call(context, argumentValues);
		context.popScope();
		return result;
	}
}
