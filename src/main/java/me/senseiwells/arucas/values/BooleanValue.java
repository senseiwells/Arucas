package me.senseiwells.arucas.values;

public class BooleanValue extends Value<Boolean> {

	public BooleanValue(Boolean value) {
		super(value);
	}

	public BooleanValue isAnd(BooleanValue other) {
		return new BooleanValue(this.value && other.value);
	}

	public BooleanValue isOr(BooleanValue other) {
		return new BooleanValue(this.value || other.value);
	}

	public BooleanValue not() {
		return new BooleanValue(!this.value);
	}

	@Override
	public Value<Boolean> copy() {
		return new BooleanValue(this.value).setPos(this.startPos, this.endPos);
	}
}
