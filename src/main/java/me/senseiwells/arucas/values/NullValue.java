package me.senseiwells.arucas.values;

public class NullValue extends Value<Object> {

	public NullValue() {
		super(null);
	}

	@Override
	public BooleanValue isEqual(Value<?> other) {
		return new BooleanValue(other.value == null);
	}

	@Override
	public BooleanValue isNotEqual(Value<?> other) {
		return new BooleanValue(other.value != null);
	}

	@Override
	public NullValue copy() {
		return (NullValue) new NullValue().setPos(this.startPos, this.endPos);
	}

	@Override
	public String toString() {
		return "null";
	}
}
