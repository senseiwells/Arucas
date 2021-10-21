package me.senseiwells.arucas.throwables;

import me.senseiwells.arucas.utils.Context;

public class ThrowStop extends CodeError {

	public ThrowStop() {
		super(ErrorType.STOP, null, null, null);
	}
	
	@Override
	public String toString(Context context) {
		return "Program has stopped";
	}
}
