package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.StringValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.classes.ArucasClassValue;
import me.senseiwells.arucas.values.functions.EmbeddableFunction;

public class MemberAccessNode extends Node {
	private final Node leftNode;
	private final Node memberNameNode;

	public MemberAccessNode(Node leftNode, Node rightNode) {
		super(leftNode.token, leftNode.syntaxPosition, rightNode.syntaxPosition);
		this.leftNode = leftNode;
		this.memberNameNode = rightNode;
	}

	@Override
	public Value<?> visit(Context context) throws CodeError, ThrowValue {
		// The leftNode holds the Value that contains the member
		Value<?> memberValue = this.leftNode.visit(context);
		
		// The memberNameNode is the MemberAccessNode that contains the name of the member
		StringValue memberName = (StringValue) this.memberNameNode.visit(context);
		
		if (!(memberValue instanceof ArucasClassValue classValue)) {
			throw new RuntimeError("Member variable '%s' was not defined for the value type '%s'".formatted(
				memberName,
				memberValue.getClass().getSimpleName()
			), this.syntaxPosition, context);
		}
		
		Value<?> value = classValue.getMember(memberName.value);

		if (value == null) {
			// Get a delegate if method exists
			value = context.getMemberFunction(classValue.getClass(), memberName.value, -2);
			// If the function is embeddable we set the calling member
			if (value instanceof EmbeddableFunction embeddableFunction) {
				embeddableFunction.setCallingMember(() -> classValue);
			}
		}

		if (value == null) {
			throw new RuntimeError("Member variable '%s' was not defined for the value type '%s'".formatted(
				memberName,
				memberValue.getClass().getSimpleName()
			), this.syntaxPosition, context);
		}
		
		return value;
	}
}
