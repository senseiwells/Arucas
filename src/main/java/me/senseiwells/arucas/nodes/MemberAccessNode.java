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
import me.senseiwells.arucas.values.functions.IMemberFunction;
import me.senseiwells.arucas.values.functions.JavaFunction;

public class MemberAccessNode extends Node {
	private final Node leftNode;
	private final Node memberNameNode;

	public MemberAccessNode(Node leftNode, Node rightNode) {
		super(leftNode.token, leftNode.syntaxPosition, rightNode.syntaxPosition);
		this.leftNode = leftNode;
		this.memberNameNode = rightNode;
	}

	@Override
	public Value visit(Context context) throws CodeError, ThrowValue {
		// The leftNode holds the Value that contains the member
		Value memberValue = this.leftNode.visit(context);

		// The memberNameNode is the MemberAccessNode that contains the name of the member
		StringValue memberName = (StringValue) this.memberNameNode.visit(context);

		Value value = null;
		if (memberValue instanceof ArucasClassValue classValue) {
			value = classValue.getMember(memberName.value);
		}
		else if (memberValue instanceof JavaValue javaValue) {
			Object callingObject = javaValue.asJavaValue();
			Class<?> callingClass = callingObject.getClass();
			String obfuscatedName = JavaValue.getObfuscatedFieldName(context, callingClass, memberName.value);
			value = ReflectionUtils.getFieldFromJavaValue(javaValue, obfuscatedName, this.syntaxPosition, context);
			if (value == null) {
				obfuscatedName = JavaValue.getObfuscatedMethodName(context, callingClass, memberName.value);
				value = JavaFunction.of(
					ReflectionUtils.getMethodSlow(callingClass, callingObject, obfuscatedName, -1),
					callingObject,
					this.syntaxPosition
				);
			}
		}

		if (value == null) {
			// Get a delegate if method exists
			value = context.getMemberFunction(memberValue.getClass(), memberName.value, -2);
			// If it's a member function we must set the member now
			if (value instanceof IMemberFunction memberFunction) {
				value = memberFunction.setThisAndGet(memberValue);
			}
		}

		if (value == null) {
			throw new RuntimeError("Member variable '%s' was not defined for the value type '%s'".formatted(
				memberName,
				memberValue.getTypeName()
			), this.syntaxPosition, context);
		}

		return value;
	}
}
