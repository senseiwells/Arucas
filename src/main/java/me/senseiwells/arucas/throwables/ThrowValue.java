package me.senseiwells.arucas.throwables;

import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.Value;

public abstract class ThrowValue extends Exception {
	
	public static class Continue extends ThrowValue {
	
	}
	
	public static class Break extends ThrowValue {
	
	}
	
	public static class Return extends ThrowValue {
		public Value<?> returnValue;
		public Return(Value<?> returnValue) {
			this.returnValue = returnValue;
		}
	}
}
