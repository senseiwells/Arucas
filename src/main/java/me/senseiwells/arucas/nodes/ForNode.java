package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.ListValue;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.Value;

import java.util.ConcurrentModificationException;
import java.util.List;

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
		if (!(forValue instanceof ListValue listValue)) {
			throw new CodeError(CodeError.ErrorType.ILLEGAL_OPERATION_ERROR, "For loop must contain a list", this.startPos, this.endPos);
		}
		
		final List<Value<?>> list = listValue.value;
		
		// This for loop must not iterate over the elements in the list
		// with 'for(element : list)' because this would allow for ConcurrentModificationException.
		for (int i = 0; i < list.size(); i++) {
			if (Thread.currentThread().isInterrupted()) {
				break;
			}
			
			// Not thread safe
			Value<?> value = list.get(i);
			
			context.setLocal(this.forParameterName, value);
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
		}
		context.popScope();
		return new NullValue();
	}
}
