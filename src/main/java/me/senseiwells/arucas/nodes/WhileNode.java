package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.values.BooleanValue;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.Value;

public class WhileNode extends Node {
	public final Node condition;
	public final Node body;

	public WhileNode(Node condition, Node body) {
		super(condition.token, condition.startPos, body.endPos);
		this.condition = condition;
		this.body = body;
	}

	@Override
	public Value<?> visit(Context context) throws CodeError, ThrowValue {
		// Push while scope
		context.pushWhileScope(this.startPos);
		
		while (true) {
			Value<?> conditionValue = this.condition.visit(context);
			if (!(conditionValue instanceof BooleanValue booleanValue))
				throw new CodeError(CodeError.ErrorType.ILLEGAL_OPERATION_ERROR, "Condition must result in either 'true' or 'false'", this.startPos, this.endPos);
			if (!booleanValue.value)
				break;
			try {
				this.body.visit(context);
			}
			catch (ThrowValue tv) {
				if (tv.shouldContinue) {
					context.moveScope(context.symbolTable.getContinueScope());
					continue;
				}
				if (tv.shouldBreak) {
					context.moveScope(context.symbolTable.getBreakScope());
					break;
				}
				throw tv;
			}
		}
		
		// Pop while scope
		context.popScope();
		return new NullValue().setContext(context);
	}
}
