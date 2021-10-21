package me.senseiwells.arucas.values;

import me.senseiwells.arucas.throwables.CodeError;

public class StringValue extends Value<String> {

	public StringValue(String value) {
		super(value);
	}

	@Override
	public StringValue addTo(Value<?> other) throws CodeError {
		if (!(other instanceof StringValue otherValue))
			throw new CodeError(CodeError.ErrorType.ILLEGAL_OPERATION_ERROR, "The 'add' operator cannot be applied to %s and %s".formatted(this, other), this.startPos, this.endPos);
		return new StringValue(this.value + otherValue.value);
	}

	@Override
	public Value<String> copy() {
		return new StringValue(this.value).setPos(this.startPos, this.endPos);
	}
}
