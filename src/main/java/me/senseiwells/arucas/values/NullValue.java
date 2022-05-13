package me.senseiwells.arucas.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.ValueTypes;

public class NullValue extends Value<Object> {
	public static final NullValue NULL = new NullValue();

	private NullValue() {
		super(null);
	}

	@Override
	public NullValue copy(Context context) {
		return this;
	}

	@Override
	public int getHashCode(Context context) {
		return 0;
	}

	@Override
	public String getAsString(Context context) {
		return "null";
	}

	@Override
	public boolean isEquals(Context context, Value<?> other) {
		return other == NULL;
	}

	@Override
	public String getTypeName() {
		return ValueTypes.NULL;
	}

	/**
	 * Null class for Arucas. <br>
	 * This class cannot be constructed since null has a literal <code>null</code> <br>
	 * No Documentation.
	 * @author senseiwells
	 */
	public static class ArucasNullClass extends ArucasClassExtension {
		public ArucasNullClass() {
			super(ValueTypes.NULL);
		}

		@Override
		public Class<NullValue> getValueClass() {
			return NullValue.class;
		}
	}
}
