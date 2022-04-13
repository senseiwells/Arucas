package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.extensions.util.JavaValue;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.ReflectionUtils;
import me.senseiwells.arucas.values.StringValue;
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
	public Value<?> visit(Context context) throws CodeError, ThrowValue {
		// The leftNode holds the Value that contains the member
		Value<?> memberValue = this.leftNode.visit(context);
		
		// The memberNameNode is the MemberAccessNode that contains the name of the member
		StringValue memberName = (StringValue) this.memberNameNode.visit(context);

		if (memberValue instanceof ArucasClassValue classValue) {
			Value<?> newValue = this.getNewValue(context);

			if (!classValue.hasMember(memberName.value) || !classValue.setMember(memberName.value, newValue)) {
				throw new RuntimeError(
					"The member '%s' cannot be set for '%s'".formatted(memberName.value,classValue.getClass().getSimpleName()),
					this.syntaxPosition,
					context
				);
			}

			return newValue;
		}
		if (memberValue instanceof JavaValue javaValue) {
			Value<?> newValue = this.getNewValue(context);
			String obfuscatedFieldName = JavaValue.getObfuscatedFieldName(context, javaValue.asJavaValue().getClass(), memberName.value);
			if (ReflectionUtils.setFieldFromJavaValue(javaValue, newValue, obfuscatedFieldName, this.syntaxPosition, context)) {
				return newValue;
			}
		}

		throw new RuntimeError("You can only assign values to class member values", this.syntaxPosition, context);
	}
}
