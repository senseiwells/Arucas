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
	public int hashCode() {
		// This value is taken from Arrays.hash( ... )
		return 0;
	}

	@Override
	public String getStringValue(Context context) throws CodeError {
		return "null";
	}

	public static class ArucasNullClass extends ArucasClassExtension {
		public ArucasNullClass() {
			super("Null");
		}
	}
}
