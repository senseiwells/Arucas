package me.senseiwells.arucas.values;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;

public class BooleanValue extends Value<Boolean> {
	public static BooleanValue
		TRUE = new BooleanValue(true),
		FALSE = new BooleanValue(false);

	private BooleanValue(Boolean value) {
		super(value);
	}

	public static BooleanValue of(boolean bool) {
		return bool ? TRUE : FALSE;
	}

	@Override
	public BooleanValue isAnd(Value<?> other, ISyntax syntaxPosition) throws CodeError {
		if (other instanceof BooleanValue booleanValue) {
			return BooleanValue.of(this.value && booleanValue.value);
		}
		return super.isAnd(other, syntaxPosition);
	}

	@Override
	public BooleanValue isOr(Value<?> other, ISyntax syntaxPosition) throws CodeError {
		if (other instanceof BooleanValue booleanValue) {
			return BooleanValue.of(this.value || booleanValue.value);
		}
		return super.isOr(other, syntaxPosition);
	}

	@Override
	public BooleanValue not(ISyntax syntaxPosition) throws RuntimeError {
		return BooleanValue.of(!this.value);
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
