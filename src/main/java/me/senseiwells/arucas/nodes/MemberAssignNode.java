package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.StringValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.classes.ArucasClassValue;

public class MemberAssignNode extends Node {
	private final Node leftNode;
	private final Node memberName;
	private final Node valueNode;
	
	public MemberAssignNode(Node leftNode, Node memberName, Node valueNode) {
		super(leftNode.token, leftNode.syntaxPosition, valueNode.syntaxPosition);
		this.leftNode = leftNode;
		this.memberName = memberName;
		this.valueNode = valueNode;
	}

	@Override
	public Value<?> visit(Context context) throws CodeError, ThrowValue {
		// The leftNode holds the Value<?> we which to call this member function on
		Value<?> memberValue = this.leftNode.visit(context);
		
		// The methodName node is the MemberAccessNode that just contains a string
		StringValue memberName = (StringValue) this.memberName.visit(context);
		
		if (!(memberValue instanceof ArucasClassValue classValue)) {
			throw new RuntimeError("You can only assign values to class member values", this.syntaxPosition, context);
		}
		
		Value<?> newValue = this.valueNode.visit(context);
		
		if (!classValue.hasMember(memberName.value) || !classValue.setMember(memberName.value, newValue)) {
			throw new RuntimeError(
				"The class '%s' does not contain the member variable '%s'".formatted(classValue.getClass().getSimpleName(), memberName.value),
				this.syntaxPosition,
				context
			);
		}
		
		return newValue;
	}
}
