package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.IArucasCollection;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.Value;

public class ForeachNode extends Node {
	private final Node list;
	private final Node body;
	private final String forParameterName;

	public ForeachNode(Node list, Node body, String forParameterName) {
		super(list.token, list.syntaxPosition, body.syntaxPosition);
		this.list = list;
		this.body = body;
		this.forParameterName = forParameterName;
	}

	@Override
	public Value visit(Context context) throws CodeError, ThrowValue {
		context.pushLoopScope(this.syntaxPosition);
		Value forValue = this.list.visit(context);
		if (!(forValue.isCollection())) {
			throw new CodeError(CodeError.ErrorType.ILLEGAL_OPERATION_ERROR, "For loop must contain a collection", this.syntaxPosition);
		}
		IArucasCollection collection = forValue.asCollection(context, this.syntaxPosition);

		for (Value item : collection.asCollection()) {
			// Throws an error if the thread has been interrupted
			this.keepRunning();

			context.setLocal(this.forParameterName, item);
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
		return NullValue.NULL;
	}
}
