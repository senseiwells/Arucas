package me.senseiwells.arucas.utils;

import me.senseiwells.arucas.values.Value;

public class ValuePair {
	private final Value<?> key;
	private final Value<?> value;

	public ValuePair(Value<?> key, Value<?> value) {
		this.key = key;
		this.value = value;
	}

	public Value<?> getKey() {
		return this.key;
	}

	public Value<?> getValue() {
		return this.value;
	}
}
