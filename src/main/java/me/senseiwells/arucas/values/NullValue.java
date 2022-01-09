package me.senseiwells.arucas.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Context;

public class NullValue extends Value<Object> {
	public static final NullValue NULL = new NullValue();

	private NullValue() {
		super(null);
	}

	@Override
	public NullValue copy() {
		return this;
	}
	
	@Override
	public int getHashCode(Context context) {
		return 0;
	}
	
	@Override
	public String getStringValue(Context context) {
		return "null";
	}
	
	@Override
	public boolean isEquals(Context context, Value<?> other) {
		return other instanceof NullValue;
	}

	public static class ArucasNullClass extends ArucasClassExtension {
		public ArucasNullClass() {
			super("Null");
		}

		@Override
		public Class<?> getValueClass() {
			return NullValue.class;
		}
	}
}
