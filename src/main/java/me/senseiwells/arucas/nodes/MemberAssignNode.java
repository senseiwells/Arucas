package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.extensions.util.JavaValue;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.ReflectionUtils;
import me.senseiwells.arucas.values.StringValue;
import me.senseiwells.arucas.values.GenericValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.classes.ArucasClassValue;

public class MemberAssignNode extends VariableAssignNode {
	private final Node leftNode;
	private final Node memberNameNode;
	
	public MemberAssignNode(Node leftNode, Node memberName, Node valueNode) {
		super(leftNode.token, leftNode.syntaxPosition, valueNode.syntaxPosition, valueNode);
		this.leftNode = leftNode;
		this.memberNameNode = memberName;
	}

	@Override
	public Value visit(Context context) throws CodeError, ThrowValue {
		// The leftNode holds the Value that contains the member
		Value memberValue = this.leftNode.visit(context);
		
		// The memberNameNode is the MemberAccessNode that contains the name of the member
		StringValue memberName = (StringValue) this.memberNameNode.visit(context);

		return memberValue.onMemberAssign(context, memberName.value, this::getNewValue, this.syntaxPosition);
	}
}
