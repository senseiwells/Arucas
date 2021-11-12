package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.values.BooleanValue;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.Value;

public class IfNode extends Node {
	private final Node conditionNode;
	private final Node bodyNode;
	private final Node elseNode;

	public IfNode(Node conditionNode, Node bodyNode, Node elseNode) {
		super(conditionNode.token, conditionNode.syntaxPosition, (elseNode instanceof NullNode ? conditionNode : elseNode).syntaxPosition);
		this.conditionNode = conditionNode;
		this.bodyNode = bodyNode;
		this.elseNode = elseNode;
	}

	@Override
	public Value<?> visit(Context context) throws CodeError, ThrowValue {
		context.pushScope(this.syntaxPosition);
		
		Value<?> conditionalValue = this.conditionNode.visit(context);
		if (!(conditionalValue instanceof BooleanValue booleanValue)) {
			context.popScope();
			throw new CodeError(CodeError.ErrorType.ILLEGAL_OPERATION_ERROR, "Condition must result in either 'true' or 'false'", this.syntaxPosition);
		}
		
		if (booleanValue.value) {
			this.bodyNode.visit(context);
		}
		else if (!(this.elseNode instanceof NullNode)) {
			this.elseNode.visit(context);
		}
		
		context.popScope();
		return new NullValue();
	}
}
