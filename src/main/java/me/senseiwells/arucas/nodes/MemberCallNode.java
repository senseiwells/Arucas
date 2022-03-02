package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.StringValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.classes.ArucasClassValue;
import me.senseiwells.arucas.values.functions.FunctionValue;

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
		// Throws an error if the thread has been interrupted
		this.keepRunning();

		// The valueNode holds the Value that contains the member
		Value<?> memberValue = this.valueNode.visit(context);

		// The callNode is the MemberAccessNode that contains the name of the member
		StringValue memberFunctionName = (StringValue) this.callNode.visit(context);

		List<Value<?>> argumentValues = new ArrayList<>();
		FunctionValue function;
		String customClassName = null;
		if (memberValue instanceof ArucasClassValue classValue) {
			customClassName = classValue.getName();
			// Get the class method from the value
			function = classValue.getMember(memberFunctionName.value, this.argumentNodes.size() + 1);

			if (function == null) {
				// If we had a class value, but we didn't find the member we should search its built in members
				function = context.getMemberFunction(classValue.getClass(), memberFunctionName.value, this.argumentNodes.size() + 1);
				argumentValues.add(classValue);
			}
		}
		else {
			function = context.getMemberFunction(memberValue.getClass(), memberFunctionName.value, this.argumentNodes.size() + 1);
			argumentValues.add(memberValue);
		}

		if (function == null) {
			int arguments = this.argumentNodes.size();
			String parameters = (arguments == 0) ? "" : " with %d parameter%s".formatted(arguments, arguments == 1 ? "" : "s");
			throw new RuntimeError("Member function '%s'%s was not defined for the type '%s'".formatted(
				memberFunctionName,
				parameters,
				customClassName != null ? customClassName : memberValue.getClass().getSimpleName()
			), this.syntaxPosition, context);
		}

		for (Node node : this.argumentNodes) {
			argumentValues.add(node.visit(context));
		}

		// We push a new scope to make StackTraces easier to read
		context.pushScope(this.syntaxPosition);
		Value<?> result = function.call(context, argumentValues);
		context.popScope();
		return result;
	}
}
