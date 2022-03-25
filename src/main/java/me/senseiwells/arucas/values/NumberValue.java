package me.senseiwells.arucas.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.functions.MemberFunction;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class NumberValue extends Value<Double> {
	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.############", DecimalFormatSymbols.getInstance(Locale.US));

	private NumberValue(double value) {
		super(value);
	}

	public static NumberValue of(double value) {
		return new NumberValue(value);
	}

	@Override
	public Value<?> addTo(Context context, Value<?> other, ISyntax syntaxPosition) throws CodeError {
		if (other instanceof NumberValue otherValue) {
			return new NumberValue(this.value + otherValue.value);
		}
		return super.addTo(context, other, syntaxPosition);
	}

	@Override
	public Value<?> subtractBy(Context context, Value<?> other, ISyntax syntaxPosition) throws CodeError {
		if (other instanceof NumberValue otherValue) {
			return new NumberValue(this.value - otherValue.value);
		}
		return super.subtractBy(context, other, syntaxPosition);
	}

	@Override
	public Value<?> multiplyBy(Context context, Value<?> other, ISyntax syntaxPosition) throws CodeError {
		if (other instanceof NumberValue otherValue) {
			return new NumberValue(this.value * otherValue.value);
		}
		return super.multiplyBy(context, other, syntaxPosition);
	}

	@Override
	public Value<?> divideBy(Context context, Value<?> other, ISyntax syntaxPosition) throws CodeError {
		if (other instanceof NumberValue otherValue) {
			return new NumberValue(this.value / otherValue.value);
		}
		return super.divideBy(context, other, syntaxPosition);
	}

	@Override
	public Value<?> powerBy(Context context, Value<?> other, ISyntax syntaxPosition) throws CodeError {
		if (other instanceof NumberValue otherValue) {
			return new NumberValue(Math.pow(this.value, otherValue.value));
		}
		return super.powerBy(context, other, syntaxPosition);
	}

	@Override
	public BooleanValue compareNumber(Context context, Value<?> other, Token.Type type, ISyntax syntaxPosition) throws CodeError {
		if (other instanceof NumberValue otherValue) {
			boolean bool = switch (type) {
				case LESS_THAN -> this.value < otherValue.value;
				case MORE_THAN -> this.value > otherValue.value;
				case MORE_THAN_EQUAL -> this.value >= otherValue.value;
				case LESS_THAN_EQUAL -> this.value <= otherValue.value;
				default -> false;
			};
			return BooleanValue.of(bool);
		}
		return super.compareNumber(context, other, type, syntaxPosition);
	}

	@Override
	public Value<?> unaryPlus(Context context, ISyntax syntaxPosition) {
		return this;
	}

	@Override
	public Value<?> unaryMinus(Context context, ISyntax syntaxPosition) {
		return new NumberValue(-this.value);
	}

	@Override
	public NumberValue copy(Context context) {
		return this;
	}

	@Override
	public int getHashCode(Context context) {
		return Double.hashCode(this.value);
	}

	@Override
	public String getAsString(Context context) {
		return NumberValue.DECIMAL_FORMAT.format(this.value);
	}

	@Override
	public boolean isEquals(Context context, Value<?> other) {
		return other instanceof NumberValue numberValue && this.value.equals(numberValue.value);
	}

	@Override
	public String getTypeName() {
		return "Number";
	}

	public static class ArucasNumberClass extends ArucasClassExtension {
		public ArucasNumberClass() {
			super("Number");
		}

		@Override
		public Class<NumberValue> getValueClass() {
			return NumberValue.class;
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				new MemberFunction("round", this::round),
				new MemberFunction("ceil", this::ceil),
				new MemberFunction("floor", this::floor),
				new MemberFunction("modulus", "otherNumber", this::modulus, "Use 'Math.mod(num1, num2)'"),
				new MemberFunction("toRadians", this::toRadians, "Use 'Math.toRadians(num)'"),
				new MemberFunction("toDegrees", this::toDegrees, "Use 'Math.toDegrees(num)'"),
				new MemberFunction("absolute", this::absolute, "Use 'Math.abs(num)'"),
				new MemberFunction("isInfinite", this::isInfinite),
				new MemberFunction("isNaN", this::isNan)
			);
		}

		private NumberValue round(Context context, MemberFunction function) throws CodeError {
			NumberValue thisValue = function.getParameterValueOfType(context, NumberValue.class, 0);
			return new NumberValue(Math.round(thisValue.value));
		}

		private NumberValue ceil(Context context, MemberFunction function) throws CodeError {
			NumberValue thisValue = function.getParameterValueOfType(context, NumberValue.class, 0);
			return new NumberValue(Math.ceil(thisValue.value));
		}

		private NumberValue floor(Context context, MemberFunction function) throws CodeError {
			NumberValue thisValue = function.getParameterValueOfType(context, NumberValue.class, 0);
			return new NumberValue(Math.floor(thisValue.value));
		}

		@Deprecated
		private NumberValue modulus(Context context, MemberFunction function) throws CodeError {
			NumberValue thisValue = function.getParameterValueOfType(context, NumberValue.class, 0);
			NumberValue otherNumber = function.getParameterValueOfType(context, NumberValue.class, 1);
			return new NumberValue(thisValue.value % otherNumber.value);
		}

		@Deprecated
		private NumberValue absolute(Context context, MemberFunction function) throws CodeError {
			NumberValue thisValue = function.getParameterValueOfType(context, NumberValue.class, 0);
			return new NumberValue(Math.abs(thisValue.value));
		}

		@Deprecated
		private NumberValue toRadians(Context context, MemberFunction function) throws CodeError {
			NumberValue thisValue = function.getParameterValueOfType(context, NumberValue.class, 0);
			return new NumberValue(Math.toRadians(thisValue.value));
		}

		@Deprecated
		private NumberValue toDegrees(Context context, MemberFunction function) throws CodeError {
			NumberValue thisValue = function.getParameterValueOfType(context, NumberValue.class, 0);
			return new NumberValue(Math.toDegrees(thisValue.value));
		}

		private BooleanValue isInfinite(Context context, MemberFunction function) throws CodeError {
			NumberValue thisValue = function.getParameterValueOfType(context, NumberValue.class, 0);
			return BooleanValue.of(thisValue.value.isInfinite());
		}

		private BooleanValue isNan(Context context, MemberFunction function) throws CodeError {
			NumberValue thisValue = function.getParameterValueOfType(context, NumberValue.class, 0);
			return BooleanValue.of(thisValue.value.isNaN());
		}
	}
}
