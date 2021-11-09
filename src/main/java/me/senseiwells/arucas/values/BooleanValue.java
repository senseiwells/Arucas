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
	public BooleanValue copy() {
		return this;
	}
	
	@Override
	public int hashCode() {
		return this.value.hashCode();
	}
}
