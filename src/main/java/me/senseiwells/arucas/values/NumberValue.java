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
	public Value<?> compareNumber(Context context, Value<?> other, Token.Type type, ISyntax syntaxPosition) throws CodeError {
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
	public Value<?> shiftLeft(Context context, Value<?> other, ISyntax syntaxPosition) throws CodeError {
		if (other instanceof NumberValue otherValue) {
			return new NumberValue(this.value.longValue() << otherValue.value.longValue());
		}
		return super.shiftLeft(context, other, syntaxPosition);
	}

	@Override
	public Value<?> shiftRight(Context context, Value<?> other, ISyntax syntaxPosition) throws CodeError {
		if (other instanceof NumberValue otherValue) {
			return new NumberValue(this.value.longValue() >> otherValue.value.longValue());
		}
		return super.shiftRight(context, other, syntaxPosition);
	}

	@Override
	public Value<?> bitAnd(Context context, Value<?> other, ISyntax syntaxPosition) throws CodeError {
		if (other instanceof NumberValue otherValue) {
			return new NumberValue(this.value.longValue() & otherValue.value.longValue());
		}
		return super.bitAnd(context, other, syntaxPosition);
	}

	@Override
	public Value<?> bitOr(Context context, Value<?> other, ISyntax syntaxPosition) throws CodeError {
		if (other instanceof NumberValue otherValue) {
			return new NumberValue(this.value.longValue() | otherValue.value.longValue());
		}
		return super.bitOr(context, other, syntaxPosition);
	}

	@Override
	public NumberValue not(Context context, ISyntax syntaxPosition) throws CodeError {
		return new NumberValue(~this.value.longValue());
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
	public Value<?> xor(Context context, Value<?> other, ISyntax syntaxPosition) throws CodeError {
		if (other instanceof NumberValue otherValue) {
			return NumberValue.of(this.value.longValue() ^ otherValue.value.longValue());
		}
		return super.xor(context, other, syntaxPosition);
	}

	@Override
	public NumberValue copy(Context context) {
		// Number values are immutable so we can just return this
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

	/**
	 * Number class for Arucas. <br>
	 * This class cannot be constructed as it has a literal representation. <br>
	 * For math related functions see the Math class. <br>
	 * Fully Documented.
	 * @author senseiwells
	 */
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

		/**
		 * Name: <code>&lt;Number>.round()</code> <br>
		 * Description: this allows you to round a number to the nearest integer <br>
		 * Returns - Number: the rounded number <br>
		 * Example: <code>3.5.round();</code>
		 */
		private NumberValue round(Context context, MemberFunction function) throws CodeError {
			NumberValue thisValue = function.getParameterValueOfType(context, NumberValue.class, 0);
			return new NumberValue(Math.round(thisValue.value));
		}

		/**
		 * Name: <code>&lt;Number>.ceil()</code> <br>
		 * Description: this allows you to round a number up to the nearest integer <br>
		 * Returns - Number: the rounded number <br>
		 * Example: <code>3.5.ceil();</code>
		 */
		private NumberValue ceil(Context context, MemberFunction function) throws CodeError {
			NumberValue thisValue = function.getParameterValueOfType(context, NumberValue.class, 0);
			return new NumberValue(Math.ceil(thisValue.value));
		}

		/**
		 * Name: <code>&lt;Number>.floor()</code> <br>
		 * Description: this allows you to round a number down to the nearest integer <br>
		 * Returns - Number: the rounded number <br>
		 * Example: <code>3.5.floor();</code>
		 */
		private NumberValue floor(Context context, MemberFunction function) throws CodeError {
			NumberValue thisValue = function.getParameterValueOfType(context, NumberValue.class, 0);
			return new NumberValue(Math.floor(thisValue.value));
		}

		/**
		 * Deprecated: You should use <code>Math.mod(num1, num2)</code> <br>
		 * Name: <code>&lt;Number>.modulus(otherNumber)</code> <br>
		 * Description: this allows you to get the modulus of two numbers <br>
		 * Parameter - Number: the divisor <br>
		 * Returns - Number: the modulus of the two numbers <br>
		 * Example: <code>5.modulus(2);</code>
		 */
		@Deprecated
		private NumberValue modulus(Context context, MemberFunction function) throws CodeError {
			NumberValue thisValue = function.getParameterValueOfType(context, NumberValue.class, 0);
			NumberValue otherNumber = function.getParameterValueOfType(context, NumberValue.class, 1);
			return new NumberValue(thisValue.value % otherNumber.value);
		}

		/**
		 * Deprecated: You should use <code>Math.abs(num)</code> <br>
		 * Name: <code>&lt;Number>.absolute()</code> <br>
		 * Description: this allows you to get the absolute value of a number <br>
		 * Returns - Number: the absolute value of the number <br>
		 * Example: <code>-5.absolute();</code>
		 */
		@Deprecated
		private NumberValue absolute(Context context, MemberFunction function) throws CodeError {
			NumberValue thisValue = function.getParameterValueOfType(context, NumberValue.class, 0);
			return new NumberValue(Math.abs(thisValue.value));
		}

		/**
		 * Deprecated: You should use <code>Math.toRadians(num)</code> <br>
		 * Name: <code>&lt;Number>.toRadians()</code> <br>
		 * Description: this allows you to convert a number in degrees to radians <br>
		 * Returns - Number: the number in radians <br>
		 * Example: <code>5.toRadians();</code>
		 */
		@Deprecated
		private NumberValue toRadians(Context context, MemberFunction function) throws CodeError {
			NumberValue thisValue = function.getParameterValueOfType(context, NumberValue.class, 0);
			return new NumberValue(Math.toRadians(thisValue.value));
		}

		/**
		 * Deprecated: You should use <code>Math.toDegrees(num)</code> <br>
		 * Name: <code>&lt;Number>.toDegrees()</code> <br>
		 * Description: this allows you to convert a number in radians to degrees <br>
		 * Returns - Number: the number in degrees <br>
		 * Example: <code>Math.PI.toDegrees();</code>
		 */
		@Deprecated
		private NumberValue toDegrees(Context context, MemberFunction function) throws CodeError {
			NumberValue thisValue = function.getParameterValueOfType(context, NumberValue.class, 0);
			return new NumberValue(Math.toDegrees(thisValue.value));
		}

		/**
		 * Name: <code>&lt;Number>.isInfinite()</code> <br>
		 * Description: this allows you to check if a number is infinite <br>
		 * Returns - Boolean: true if the number is infinite <br>
		 * Example: <code>(0/0).isInfinite();</code>
		 */
		private BooleanValue isInfinite(Context context, MemberFunction function) throws CodeError {
			NumberValue thisValue = function.getParameterValueOfType(context, NumberValue.class, 0);
			return BooleanValue.of(thisValue.value.isInfinite());
		}

		/**
		 * Name: <code>&lt;Number>.isNaN()</code> <br>
		 * Description: this allows you to check if a number is not a number <br>
		 * Returns - Boolean: true if the number is not a number <br>
		 * Example: <code>(0/0).isNaN();</code>
		 */
		private BooleanValue isNan(Context context, MemberFunction function) throws CodeError {
			NumberValue thisValue = function.getParameterValueOfType(context, NumberValue.class, 0);
			return BooleanValue.of(thisValue.value.isNaN());
		}
	}
}
