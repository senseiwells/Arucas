package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.BooleanValue;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.Value;

public class ForNode extends Node {
	private final Node initialExpression;
	private final Node condition;
	private final Node endExpression;
	private final Node body;

	public ForNode(Node initialExpression, Node condition, Node endExpression, Node body) {
		super(condition.token, initialExpression.syntaxPosition, body.syntaxPosition);
		this.initialExpression = initialExpression;
		this.condition = condition;
		this.endExpression = endExpression;
		this.body = body;
	}

	@Override
	public Value visit(Context context) throws CodeError, ThrowValue {
		context.pushLoopScope(this.syntaxPosition);

		this.initialExpression.visit(context);
		while (this.keepRunning()) {
			Value conditionValue = this.condition.visit(context);
			if (!(conditionValue instanceof BooleanValue booleanValue)) {
				throw new CodeError(CodeError.ErrorType.ILLEGAL_OPERATION_ERROR, "Condition must result in either 'true' or 'false'", this.syntaxPosition);
			}

			if (!booleanValue.value) {
				break;
			}

			try {
				this.body.visit(context);
			}
			catch (ThrowValue.Break tv) {
				context.moveScope(context.getBreakScope());
				break;
			}
			catch (ThrowValue.Continue tv) {
				context.moveScope(context.getContinueScope());
			}
			this.endExpression.visit(context);
		}

		context.popScope();
		return NullValue.NULL;
	}
}
