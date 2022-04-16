package me.senseiwells.arucas.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.Context;

public class BooleanValue extends Value<Boolean> {
	public static final BooleanValue TRUE = new BooleanValue(true);
	public static final BooleanValue FALSE = new BooleanValue(false);

	private BooleanValue(boolean value) {
		super(value);
	}

	public static BooleanValue of(boolean bool) {
		return bool ? TRUE : FALSE;
	}

	@Override
	public BooleanValue isAnd(Context context, Value<?> other, ISyntax syntaxPosition) throws CodeError {
		if (other instanceof BooleanValue booleanValue) {
			return BooleanValue.of(this.value && booleanValue.value);
		}
		return super.isAnd(context, other, syntaxPosition);
	}

	@Override
	public BooleanValue isOr(Context context, Value<?> other, ISyntax syntaxPosition) throws CodeError {
		if (other instanceof BooleanValue booleanValue) {
			return BooleanValue.of(this.value || booleanValue.value);
		}
		return super.isOr(context, other, syntaxPosition);
	}

	@Override
	public Value<?> xor(Context context, Value<?> other, ISyntax syntaxPosition) throws CodeError {
		if (other instanceof BooleanValue booleanValue) {
			return BooleanValue.of(this.value ^ booleanValue.value);
		}
		return super.xor(context, other, syntaxPosition);
	}

	@Override
	public Value<?> bitAnd(Context context, Value<?> other, ISyntax syntaxPosition) throws CodeError {
		return this.isAnd(context, other, syntaxPosition);
	}

	@Override
	public Value<?> bitOr(Context context, Value<?> other, ISyntax syntaxPosition) throws CodeError {
		return this.isOr(context, other, syntaxPosition);
	}

	@Override
	public BooleanValue not(Context context, ISyntax syntaxPosition) throws RuntimeError {
		return this.not();
	}

	public BooleanValue not() {
		return BooleanValue.of(!this.value);
	}

	@Override
	public BooleanValue copy(Context context) {
		return this;
	}

	@Override
	public int getHashCode(Context context) {
		return Boolean.hashCode(this.value);
	}

	@Override
	public String getAsString(Context context) {
		return this.value ? "true" : "false";
	}

	@Override
	public boolean isEquals(Context context, Value<?> other) {
		return (other instanceof BooleanValue that) && this.value == that.value;
	}

	@Override
	public String getTypeName() {
		return "Boolean";
	}

	/**
	 * Boolean class for Arucas. <br>
	 * This class cannot be constructed since Booleans have literals. <br>
	 * No Documentation.
	 * @author senseiwells
	 */
	public static class ArucasBooleanClass extends ArucasClassExtension {
		public ArucasBooleanClass() {
			super("Boolean");
		}

		@Override
		public Class<BooleanValue> getValueClass() {
			return BooleanValue.class;
		}
	}
}
