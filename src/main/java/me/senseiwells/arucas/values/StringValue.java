package me.senseiwells.arucas.values;

public class StringValue extends Value<String> {
	public StringValue(String value) {
		super(value);
	}

	@Override
	public StringValue addTo(Value<?> other) {
		return new StringValue(this.value + other.toString());
	}

	@Override
	public Value<String> copy() {
		return new StringValue(this.value).setPos(this.startPos, this.endPos);
	}
}
