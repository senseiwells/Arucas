package me.senseiwells.arucas.values;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.tokens.Token;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class NumberValue extends Value<Double> {
	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.############", DecimalFormatSymbols.getInstance(Locale.US));
	
	public NumberValue(double value) {
		super(value);
	}

	@Override
	public Value<?> addTo(Value<?> other, ISyntax syntaxPosition) throws CodeError {
		if (other instanceof NumberValue otherValue) {
			return new NumberValue(this.value + otherValue.value);
		}
		return super.addTo(other, syntaxPosition);
	}

	@Override
	public Value<?> subtractBy(Value<?> other, ISyntax syntaxPosition) throws CodeError {
		if (other instanceof NumberValue otherValue) {
			return new NumberValue(this.value - otherValue.value);
		}
		return super.subtractBy(other, syntaxPosition);
	}

	@Override
	public Value<?> multiplyBy(Value<?> other, ISyntax syntaxPosition) throws CodeError {
		if (other instanceof NumberValue otherValue) {
			return new NumberValue(this.value * otherValue.value);
		}
		return super.multiplyBy(other, syntaxPosition);
	}

	@Override
	public Value<?> divideBy(Value<?> other, ISyntax syntaxPosition) throws CodeError {
		if (other instanceof NumberValue otherValue) {
			return new NumberValue(this.value / otherValue.value);
		}
		return super.divideBy(other, syntaxPosition);
	}

	@Override
	public Value<?> powerBy(Value<?> other, ISyntax syntaxPosition) throws CodeError {
		if (other instanceof NumberValue otherValue) {
			return new NumberValue(Math.pow(this.value, otherValue.value));
		}
		return super.powerBy(other, syntaxPosition);
	}

	public BooleanValue compareNumber(Value<?> other, Token.Type type, ISyntax syntaxPosition) throws CodeError {
		if (other instanceof NumberValue otherValue) {
			boolean bool = switch (type) {
				case LESS_THAN -> this.value < otherValue.value;
				case MORE_THAN -> this.value > otherValue.value;
				case MORE_THAN_EQUAL -> this.value >= otherValue.value;
				case LESS_THAN_EQUAL -> this.value <= otherValue.value;
				default -> false;
			};
			return new BooleanValue(bool);
		}
		return super.compareNumber(other, type, syntaxPosition);
	}

	@Override
	public NumberValue copy() {
		return this;
	}
	
	@Override
	public int hashCode() {
		return this.value.hashCode();
	}
	
	@Override
	public String toString() {
		return DECIMAL_FORMAT.format(this.value);
	}
}
