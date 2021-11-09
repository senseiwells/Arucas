package me.senseiwells.arucas.values;

public class NullValue extends Value<Object> {

	public NullValue() {
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
	public String toString() {
		return "null";
	}
}
