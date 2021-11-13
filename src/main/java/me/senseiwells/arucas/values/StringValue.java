package me.senseiwells.arucas.values;

import me.senseiwells.arucas.api.ISyntax;

public class StringValue extends Value<String> {
	public StringValue(String value) {
		super(value);
	}

	@Override
	public StringValue addTo(Value<?> other, ISyntax syntaxPosition) {
		return new StringValue(this.value + other.toString());
	}

	@Override
	public StringValue copy() {
		return this;
	}
	
	@Override
	public int hashCode() {
		return this.value.hashCode();
	}
}
