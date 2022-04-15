package me.senseiwells.arucas.throwables;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.utils.Context;

public class ThrowStop extends CodeError {
	public ThrowStop() {
		super(ErrorType.STOP, "Program has stopped", ISyntax.empty());
	}

	@Override
	public String toString(Context context) {
		return context.getOutput().addErrorFormattingBold("Program has stopped");
	}
}
