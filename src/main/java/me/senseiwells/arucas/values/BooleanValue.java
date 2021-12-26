package me.senseiwells.arucas.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.Context;

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
	public BooleanValue isAnd(Context context,  Value<?> other, ISyntax syntaxPosition) throws CodeError {
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
	public BooleanValue not(Context context, ISyntax syntaxPosition) throws RuntimeError {
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

	public static class ArucasBooleanClass extends ArucasClassExtension {
		public ArucasBooleanClass() {
			super("Boolean");
		}
	}
}
