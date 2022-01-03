package me.senseiwells.arucas.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.functions.MemberFunction;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Set;

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
	public NumberValue copy() {
		return this;
	}
	
	@Override
	public String getStringValue(Context context) throws CodeError {
		return NumberValue.DECIMAL_FORMAT.format(this.value);
	}

	@Override
	protected Set<MemberFunction> getDefinedFunctions() {
		Set<MemberFunction> memberFunctions = super.getDefinedFunctions();
		memberFunctions.addAll(Set.of(
			new MemberFunction("round", this::numberRound),
			new MemberFunction("ceil", this::numberCeil),
			new MemberFunction("floor", this::numberFloor),
			new MemberFunction("modulus", "otherNumber", this::numberModulus, "Use 'Math.mod(num1, num2)'"),
			new MemberFunction("toRadians", this::toRadians, "Use 'Math.toRadians(num)'"),
			new MemberFunction("toDegrees", this::toDegrees, "Use 'Math.toDegrees(num)'"),
			new MemberFunction("absolute", this::numberAbsolute, "Use 'Math.abs(num)'"),
			new MemberFunction("isInfinite", this::numberIsInfinite),
			new MemberFunction("isNaN", this::numberIsNan)
		));
		return memberFunctions;
	}

	private NumberValue numberRound(Context context, MemberFunction function) {
		return new NumberValue(Math.round(this.value));
	}

	private NumberValue numberCeil(Context context, MemberFunction function) {
		return new NumberValue(Math.ceil(this.value));
	}

	private NumberValue numberFloor(Context context, MemberFunction function) {
		return new NumberValue(Math.floor(this.value));
	}

	private NumberValue numberModulus(Context context, MemberFunction function) throws CodeError {
		NumberValue otherNumber = function.getParameterValueOfType(context, NumberValue.class, 0);
		return new NumberValue(this.value % otherNumber.value);
	}

	private NumberValue numberAbsolute(Context context, MemberFunction function) {
		return new NumberValue(Math.abs(this.value));
	}
	private NumberValue toRadians(Context context, MemberFunction function) {
		return new NumberValue(Math.toRadians(this.value));
	}

	private NumberValue toDegrees(Context context, MemberFunction function) {
		return new NumberValue(Math.toDegrees(this.value));
	}

	private BooleanValue numberIsInfinite(Context context, MemberFunction function) {
		return BooleanValue.of(this.value.isInfinite());
	}

	private BooleanValue numberIsNan(Context context, MemberFunction function) {
		return BooleanValue.of(this.value.isNaN());
	}

	public static class ArucasNumberClass extends ArucasClassExtension {
		public ArucasNumberClass() {
			super("Number");
		}
	}
}
