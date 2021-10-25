package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.ListValue;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.Value;

public class ForNode extends Node {
	private final Node list;
	private final Node body;
	private final String forParameterName;

	public ForNode(Node list, Node body, String forParameterName) {
		super(list.token, list.startPos, body.endPos);
		this.list = list;
		this.body = body;
		this.forParameterName = forParameterName;
	}

	@Override
	public Value<?> visit(Context context) throws CodeError, ThrowValue {
		context.pushLoopScope(this.startPos);
		Value<?> forValue = this.list.visit(context);
		if (!(forValue instanceof ListValue listValue))
			throw new CodeError(CodeError.ErrorType.ILLEGAL_OPERATION_ERROR, "For loop must contain a list", this.startPos, this.endPos);
		for (Value<?> value : listValue.value) {
			if (Thread.currentThread().isInterrupted())
				break;
			context.setLocal(this.forParameterName, value);
			try {
				this.body.visit(context);
			}
			catch (ThrowValue.Break tv) {
				context.moveScope(context.getSymbolTable().getBreakScope());
				break;
			}
			catch (ThrowValue.Continue tv) {
				context.moveScope(context.getSymbolTable().getContinueScope());
			}
		}
		context.popScope();
		return new NullValue();
	}
}
