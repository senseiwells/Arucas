package me.senseiwells.arucas.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.Arguments;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.functions.MemberFunction;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import static me.senseiwells.arucas.utils.ValueTypes.BOOLEAN;
import static me.senseiwells.arucas.utils.ValueTypes.NUMBER;

public class NumberValue extends GenericValue<Double> {
	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.############", DecimalFormatSymbols.getInstance(Locale.US));

	private NumberValue(double value) {
		super(value);
	}

	public static NumberValue of(double value) {
		return new NumberValue(value);
	}

	@Override
	public Value addTo(Context context, Value other, ISyntax syntaxPosition) throws CodeError {
		if (other instanceof NumberValue otherValue) {
			return new NumberValue(this.value + otherValue.value);
		}
		return super.addTo(context, other, syntaxPosition);
	}

	@Override
	public Value subtractBy(Context context, Value other, ISyntax syntaxPosition) throws CodeError {
		if (other instanceof NumberValue otherValue) {
			return new NumberValue(this.value - otherValue.value);
		}
		return super.subtractBy(context, other, syntaxPosition);
	}

	@Override
	public Value multiplyBy(Context context, Value other, ISyntax syntaxPosition) throws CodeError {
		if (other instanceof NumberValue otherValue) {
			return new NumberValue(this.value * otherValue.value);
		}
		return super.multiplyBy(context, other, syntaxPosition);
	}

	@Override
	public Value divideBy(Context context, Value other, ISyntax syntaxPosition) throws CodeError {
		if (other instanceof NumberValue otherValue) {
			return new NumberValue(this.value / otherValue.value);
		}
		return super.divideBy(context, other, syntaxPosition);
	}

	@Override
	public Value powerBy(Context context, Value other, ISyntax syntaxPosition) throws CodeError {
		if (other instanceof NumberValue otherValue) {
			return new NumberValue(Math.pow(this.value, otherValue.value));
		}
		return super.powerBy(context, other, syntaxPosition);
	}

	@Override
	public Value compareNumber(Context context, Value other, Token.Type type, ISyntax syntaxPosition) throws CodeError {
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
	public Value shiftLeft(Context context, Value other, ISyntax syntaxPosition) throws CodeError {
		if (other instanceof NumberValue otherValue) {
			return new NumberValue(this.value.longValue() << otherValue.value.longValue());
		}
		return super.shiftLeft(context, other, syntaxPosition);
	}

	@Override
	public Value shiftRight(Context context, Value other, ISyntax syntaxPosition) throws CodeError {
		if (other instanceof NumberValue otherValue) {
			return new NumberValue(this.value.longValue() >> otherValue.value.longValue());
		}
		return super.shiftRight(context, other, syntaxPosition);
	}

	@Override
	public Value bitAnd(Context context, Value other, ISyntax syntaxPosition) throws CodeError {
		if (other instanceof NumberValue otherValue) {
			return new NumberValue(this.value.longValue() & otherValue.value.longValue());
		}
		return super.bitAnd(context, other, syntaxPosition);
	}

	@Override
	public Value bitOr(Context context, Value other, ISyntax syntaxPosition) throws CodeError {
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
	public Value unaryPlus(Context context, ISyntax syntaxPosition) {
		return this;
	}

	@Override
	public Value unaryMinus(Context context, ISyntax syntaxPosition) {
		return new NumberValue(-this.value);
	}

	@Override
	public Value xor(Context context, Value other, ISyntax syntaxPosition) throws CodeError {
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
	public boolean isEquals(Context context, Value other) {
		return other instanceof NumberValue numberValue && this.value.equals(numberValue.value);
	}

	@Override
	public String getTypeName() {
		return NUMBER;
	}

	@ClassDoc(
		name = NUMBER,
		desc = "This class cannot be constructed as it has a literal representation. For math related functions see the Math class."
	)
	public static class ArucasNumberClass extends ArucasClassExtension {
		public ArucasNumberClass() {
			super(NUMBER);
		}

		@Override
		public Class<NumberValue> getValueClass() {
			return NumberValue.class;
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				MemberFunction.of("round", this::round),
				MemberFunction.of("ceil", this::ceil),
				MemberFunction.of("floor", this::floor),
				MemberFunction.of("modulus", 1, this::modulus, "Use 'Math.mod(num1, num2)'"),
				MemberFunction.of("toRadians", this::toRadians, "Use 'Math.toRadians(num)'"),
				MemberFunction.of("toDegrees", this::toDegrees, "Use 'Math.toDegrees(num)'"),
				MemberFunction.of("absolute", this::absolute, "Use 'Math.abs(num)'"),
				MemberFunction.of("isInfinite", this::isInfinite),
				MemberFunction.of("isNaN", this::isNan)
			);
		}

		@FunctionDoc(
			name = "round",
			desc = "This allows you to round a number to the nearest integer",
			returns = {NUMBER, "the rounded number"},
			example = "3.5.round();"
		)
		private NumberValue round(Arguments arguments) throws CodeError {
			NumberValue thisValue = arguments.getNext(NumberValue.class);
			return new NumberValue(Math.round(thisValue.value));
		}

		@FunctionDoc(
			name = "ceil",
			desc = "This allows you to round a number up to the nearest integer",
			returns = {NUMBER, "the rounded number"},
			example = "3.5.ceil();"
		)
		private NumberValue ceil(Arguments arguments) throws CodeError {
			NumberValue thisValue = arguments.getNext(NumberValue.class);
			return new NumberValue(Math.ceil(thisValue.value));
		}

		@FunctionDoc(
			name = "floor",
			desc = "This allows you to round a number down to the nearest integer",
			returns = {NUMBER, "the rounded number"},
			example = "3.5.floor();"
		)
		private NumberValue floor(Arguments arguments) throws CodeError {
			NumberValue thisValue = arguments.getNext(NumberValue.class);
			return new NumberValue(Math.floor(thisValue.value));
		}

		@FunctionDoc(
			deprecated = "You should use `Math.mod(num1, num2)`",
			name = "modulus",
			desc = "This allows you to get the modulus of two numbers",
			params = {NUMBER, "otherNumber", "the divisor"},
			returns = {NUMBER, "the modulus of the two numbers"},
			example = "5.modulus(2);"
		)
		private NumberValue modulus(Arguments arguments) throws CodeError {
			NumberValue thisValue = arguments.getNext(NumberValue.class);
			NumberValue otherNumber = arguments.getNext(NumberValue.class);
			return new NumberValue(thisValue.value % otherNumber.value);
		}

		@FunctionDoc(
			deprecated = "You should use `Math.abs(num)`",
			name = "absolute",
			desc = "This allows you to get the absolute value of a number",
			returns = {NUMBER, "the absolute value of the number"},
			example = "(-5).absolute();"
		)
		private NumberValue absolute(Arguments arguments) throws CodeError {
			NumberValue thisValue = arguments.getNext(NumberValue.class);
			return new NumberValue(Math.abs(thisValue.value));
		}

		@FunctionDoc(
			deprecated = "You should use `Math.toRadians(num)`",
			name = "toRadians",
			desc = "This allows you to convert a number in degrees to radians",
			returns = {NUMBER, "the number in radians"},
			example = "5.toRadians();"
		)
		private NumberValue toRadians(Arguments arguments) throws CodeError {
			NumberValue thisValue = arguments.getNext(NumberValue.class);
			return new NumberValue(Math.toRadians(thisValue.value));
		}

		@FunctionDoc(
			deprecated = "You should use `Math.toDegrees(num)`",
			name = "toDegrees",
			desc = "This allows you to convert a number in radians to degrees",
			returns = {NUMBER, "the number in degrees"},
			example = "Math.pi.toDegrees();"
		)
		private NumberValue toDegrees(Arguments arguments) throws CodeError {
			NumberValue thisValue = arguments.getNext(NumberValue.class);
			return new NumberValue(Math.toDegrees(thisValue.value));
		}

		@FunctionDoc(
			name = "isInfinite",
			desc = "This allows you to check if a number is infinite",
			returns = {BOOLEAN, "true if the number is infinite"},
			example = "(1/0).isInfinite();"
		)
		private BooleanValue isInfinite(Arguments arguments) throws CodeError {
			NumberValue thisValue = arguments.getNext(NumberValue.class);
			return BooleanValue.of(thisValue.value.isInfinite());
		}

		@FunctionDoc(
			name = "isNaN",
			desc = "This allows you to check if a number is not a number",
			returns = {BOOLEAN, "true if the number is not a number"},
			example = "(0/0).isNaN();"
		)
		private BooleanValue isNan(Arguments arguments) throws CodeError {
			NumberValue thisValue = arguments.getNext(NumberValue.class);
			return BooleanValue.of(thisValue.value.isNaN());
		}
	}
}
