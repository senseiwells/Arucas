package me.senseiwells.arucas.throwables;

import me.senseiwells.arucas.values.Value;

public abstract class ThrowValue extends RuntimeException {
	ThrowValue(String message) {
		super(message);
	}

	// Filling in stack trace is very expensive
	@Override
	public synchronized Throwable fillInStackTrace() {
		return this;
	}

	public static class Continue extends ThrowValue {
		public Continue() {
			super("Cannot continue here");
		}
	}
	
	public static class Break extends ThrowValue {
		public Break() {
			super("Cannot break here");
		}
	}
	
	public static class Return extends ThrowValue {
		private Value returnValue;
		public Return(Value returnValue) {
			super("Cannot return here");
			this.returnValue = returnValue;
		}
		
		public Value getReturnValue() {
			return this.returnValue;
		}
		
		/**
		 * This method is only used internally and should not be called directly
		 */
		public void setReturnValue(Value value) {
			this.returnValue = value;
		}
	}
}
