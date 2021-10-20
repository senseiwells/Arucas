package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.values.BooleanValue;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.Value;

public class IfNode extends Node {

	public final Node conditionNode;
	public final Node bodyNode;
	public final Node elseNode;

	public IfNode(Node conditionNode, Node bodyNode, Node elseNode) {
		super(conditionNode.token, conditionNode.startPos, elseNode instanceof NullNode ? conditionNode.endPos : elseNode.endPos);
		this.conditionNode = conditionNode;
		this.bodyNode = bodyNode;
		this.elseNode = elseNode;
	}

	@Override
	public Value<?> visit(Context context) throws CodeError, ThrowValue {
		context.pushScope(this.startPos);
		
		Value<?> conditionalValue = this.conditionNode.visit(context);
		if (!(conditionalValue instanceof BooleanValue booleanValue)) {
			context.popScope();
			throw new CodeError(CodeError.ErrorType.ILLEGAL_OPERATION_ERROR, "Condition must result in either 'true' or 'false'", this.startPos, this.endPos);
		}
		
		if (booleanValue.value)
			this.bodyNode.visit(context);
		else if (!(this.elseNode instanceof NullNode))
			this.elseNode.visit(context);
		
		context.popScope();
		return new NullValue().setContext(context);
	}
}
