package me.senseiwells.arucas.values;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.Position;

public abstract class Value<T> {
	public final T value;
	public Position startPos;
	public Position endPos;
	
	public Value(T value) {
		this.value = value;
	}

	public Value<T> setPos(Position startPos, Position endPos) {
		this.startPos = startPos;
		this.endPos = endPos;
		return this;
	}
	
	public Value<?> addTo(Value<?> other) throws CodeError {
		throw new RuntimeError("The 'add' operator cannot be applied to %s and %s".formatted(this, other), this.startPos, this.endPos);
	}

	public BooleanValue isEqual(Value<?> other) {
		return new BooleanValue(this.value.equals(other.value));
	}

	public BooleanValue isNotEqual(Value<?> other) {
		return new BooleanValue(!this.value.equals(other.value));
	}

	public abstract Value<?> copy();

	@Override
	public String toString() {
		return this.value.toString();
	}

}
