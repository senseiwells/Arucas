package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.StringValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.classes.ArucasClassValue;

public class MemberAccessNode extends Node {
	private final Node leftNode;
	private final Node rightNode;

	public MemberAccessNode(Node leftNode, Node rightNode) {
		super(leftNode.token, leftNode.syntaxPosition, rightNode.syntaxPosition);
		this.leftNode = leftNode;
		this.rightNode = rightNode;
	}

	@Override
	public Value<?> visit(Context context) throws CodeError, ThrowValue {
		// The leftNode holds the Value<?> we which to get this member function on
		Value<?> memberValue = this.leftNode.visit(context);
		
		// The rightNode is the MemberAccessNode that just contains a string
		StringValue memberName = (StringValue) this.rightNode.visit(context);
		
		if (!(memberValue instanceof ArucasClassValue classValue)) {
			throw new RuntimeError("Member variable '%s' was not defined for the value type '%s'".formatted(
				memberName,
				memberValue.getClass().getSimpleName()
			), this.syntaxPosition, context);
		}
		
		Value<?> value = classValue.getMember(memberName.value);
		if (value == null) {
			throw new RuntimeError("Member variable '%s' was not defined for the value type '%s'".formatted(
				memberName,
				memberValue.getClass().getSimpleName()
			), this.syntaxPosition, context);
		}
		
		return value;
	}
}
