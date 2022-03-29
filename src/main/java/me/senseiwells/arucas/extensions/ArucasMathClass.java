package me.senseiwells.arucas.extensions;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.BaseValue;
import me.senseiwells.arucas.values.NumberValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.functions.BuiltInFunction;

import java.util.List;
import java.util.Map;

/**
 * Math class extension for Arucas. Provides many basic math functions. <br>
 * Fully Documented.
 * @author senseiwells
 */
public class ArucasMathClass extends ArucasClassExtension {
	public ArucasMathClass() {
		super("Math");
	}

	@Override
	public Map<String, Value<?>> getDefinedStaticVariables() {
		return Map.of(
			"pi", NumberValue.of(Math.PI),
			"e", NumberValue.of(Math.E),
			"root2", NumberValue.of(Math.sqrt(2))
		);
	}

	@Override
	public ArucasFunctionMap<BuiltInFunction> getDefinedStaticMethods() {
		return ArucasFunctionMap.of(
			new BuiltInFunction("round", "num", this::round),
			new BuiltInFunction("ceil", "num", this::ceil),
			new BuiltInFunction("floor", "num", this::floor),
			new BuiltInFunction("sqrt", "num", this::sqrt),
			new BuiltInFunction("abs", "num", this::abs),
			new BuiltInFunction("mod", List.of("num1", "num2"), this::mod),
			new BuiltInFunction("max", List.of("num1", "num2"), this::max),
			new BuiltInFunction("min", List.of("num1", "num2"), this::min),
			new BuiltInFunction("clamp", List.of("num", "min", "max"), this::clamp),
			new BuiltInFunction("toRadians", "num", this::toRadians),
			new BuiltInFunction("toDegrees", "num", this::toDegrees),
			new BuiltInFunction("log", "num", this::log),
			new BuiltInFunction("log", List.of("base", "num"), this::logBase),
			new BuiltInFunction("log10", "num", this::log10),
			new BuiltInFunction("sin", "num", this::sin),
			new BuiltInFunction("cos", "num", this::cos),
			new BuiltInFunction("tan", "num", this::tan),
			new BuiltInFunction("arcsin", "num", this::arcsin),
			new BuiltInFunction("arccos", "num", this::arccos),
			new BuiltInFunction("arctan", "num", this::arctan),
			new BuiltInFunction("cosec", "num", this::cosec),
			new BuiltInFunction("sec", "num", this::sec),
			new BuiltInFunction("cot", "num", this::cot)
		);
	}

	/**
	 * Name: <code>Math.round(num)</code> <br>
	 * Description: Rounds a number to the nearest integer <br>
	 * Parameter - Number: the number to round <br>
	 * Returns - Number: the rounded number <br>
	 * Example: <code>Math.round(3.5);</code> 
	 */
	private NumberValue round(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(Math.round(numberValue));
	}

	/**
	 * Name: <code>Math.ceil(num)</code> <br>
	 * Description: Rounds a number up to the nearest integer <br>
	 * Parameter - Number: the number to round <br>
	 * Returns - Number: the rounded number <br>
	 * Example: <code>Math.ceil(3.5);</code> 
	 */
	private NumberValue ceil(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(Math.ceil(numberValue));
	}

	/**
	 * Name: <code>Math.floor(num)</code> <br>
	 * Description: Rounds a number down to the nearest integer <br>
	 * Parameter - Number: the number to round <br>
	 * Returns - Number: the rounded number <br>
	 * Example: <code>Math.floor(3.5);</code> 
	 */
	private NumberValue floor(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(Math.floor(numberValue));
	}

	/**
	 * Name: <code>Math.sqrt(num)</code> <br>
	 * Description: Returns the square root of a number <br>
	 * Parameter - Number: the number to square root <br>
	 * Returns - Number: the square root of the number <br>
	 * Example: <code>Math.sqrt(9);</code> 
	 */
	private Value<?> sqrt(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(Math.sqrt(numberValue));
	}

	/**
	 * Name: <code>Math.abs(num)</code> <br>
	 * Description: Returns the absolute value of a number <br>
	 * Parameter - Number: the number to get the absolute value of <br>
	 * Returns - Number: the absolute value of the number <br>
	 * Example: <code>Math.abs(-3);</code> 
	 */
	private Value<?> abs(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(Math.abs(numberValue));
	}

	/**
	 * Name: <code>Math.mod(num1, num2)</code> <br>
	 * Description: Returns the remainder of a division <br>
	 * Parameters - Number, Number: the number to divide, the divisor <br>
	 * Returns - Number: the remainder of the division <br>
	 * Example: <code>Math.mod(5, 2);</code> 
	 */
	private Value<?> mod(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		double otherNumber = function.getParameterValueOfType(context, NumberValue.class, 1).value;
		return NumberValue.of(numberValue % otherNumber);
	}

	/**
	 * Name: <code>Math.max(num1, num2)</code> <br>
	 * Description: Returns the largest number <br>
	 * Parameters - Number, Number: the numbers to compare <br>
	 * Returns - Number: the largest number <br>
	 * Example: <code>Math.max(5, 2);</code> 
	 */
	private Value<?> max(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		double otherNumber = function.getParameterValueOfType(context, NumberValue.class, 1).value;
		return NumberValue.of(Math.max(numberValue, otherNumber));
	}

	/**
	 * Name: <code>Math.min(num1, num2)</code> <br>
	 * Description: Returns the smallest number <br>
	 * Parameters - Number, Number: the numbers to compare <br>
	 * Returns - Number: the smallest number <br>
	 * Example: <code>Math.min(5, 2);</code> 
	 */
	private Value<?> min(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		double otherNumber = function.getParameterValueOfType(context, NumberValue.class, 1).value;
		return NumberValue.of(Math.min(numberValue, otherNumber));
	}

	/**
	 * Name: <code>Math.clamp(value, min, max)</code> <br>
	 * Description: Clamps a value between a minimum and maximum <br>
	 * Parameters - Number, Number, Number: the value to clamp, the minimum, the maximum <br>
	 * Returns - Number: the clamped value <br>
	 * Example: <code>Math.clamp(10, 2, 8);</code> 
	 */
	private Value<?> clamp(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		double minNumber = function.getParameterValueOfType(context, NumberValue.class, 1).value;
		double maxNumber = function.getParameterValueOfType(context, NumberValue.class, 2).value;
		return NumberValue.of(numberValue < minNumber ? minNumber : Math.min(numberValue, maxNumber));
	}

	/**
	 * Name: <code>Math.toRadians(num)</code> <br>
	 * Description: Converts a number from degrees to radians <br>
	 * Parameter - Number: the number to convert <br>
	 * Returns - Number: the number in radians <br>
	 * Example: <code>Math.toRadians(90);</code> 
	 */
	private Value<?> toRadians(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(Math.toRadians(numberValue));
	}

	/**
	 * Name: <code>Math.toDegrees(num)</code> <br>
	 * Description: Converts a number from radians to degrees <br>
	 * Parameter - Number: the number to convert <br>
	 * Returns - Number: the number in degrees <br>
	 * Example: <code>Math.toDegrees(Math.PI);</code> 
	 */
	private Value<?> toDegrees(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(Math.toDegrees(numberValue));
	}

	/**
	 * Name: <code>Math.log(num)</code> <br>
	 * Description: Returns the natural logarithm of a number <br>
	 * Parameter - Number: the number to get the logarithm of <br>
	 * Returns - Number: the natural logarithm of the number <br>
	 * Example: <code>Math.log(Math.E);<code>
	 */
	private Value<?> log(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(Math.log(numberValue));
	}

	/**
	 * Name: <code>Math.logBase(base, num)</code> <br>
	 * Description: Returns the logarithm of a number with a specified base <br>
	 * Parameters - Number, Number: the base, the number to get the logarithm of <br>
	 * Returns - Number: the logarithm of the number <br>
	 * Example: <code>Math.logBase(2, 4);<code>
	 */
	private Value<?> logBase(Context context, BuiltInFunction function) throws CodeError {
		double baseNumber = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 1).value;
		return NumberValue.of(Math.log(numberValue) / Math.log(baseNumber));
	}

	/**
	 * Name: <code>Math.log10(num)</code> <br>
	 * Description: Returns the base 10 logarithm of a number <br>
	 * Parameter - Number: the number to get the logarithm of <br>
	 * Returns - Number: the base 10 logarithm of the number <br>
	 * Example: <code>Math.log10(100);<code>
	 */
	private Value<?> log10(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(Math.log10(numberValue));
	}

	/**
	 * Name: <code>Math.sin(num)</code> <br>
	 * Description: Returns the sine of a number <br>
	 * Parameter - Number: the number to get the sine of <br>
	 * Returns - Number: the sine of the number <br>
	 * Example: <code>Math.sin(Math.PI);<code>
	 */
	private Value<?> sin(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(Math.sin(numberValue));
	}

	/**
	 * Name: <code>Math.cos(num)</code> <br>
	 * Description: Returns the cosine of a number <br>
	 * Parameter - Number: the number to get the cosine of <br>
	 * Returns - Number: the cosine of the number <br>
	 * Example: <code>Math.cos(Math.PI);<code>
	 */
	private Value<?> cos(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(Math.cos(numberValue));
	}

	/**
	 * Name: <code>Math.tan(num)</code> <br>
	 * Description: Returns the tangent of a number <br>
	 * Parameter - Number: the number to get the tangent of <br>
	 * Returns - Number: the tangent of the number <br>
	 * Example: <code>Math.tan(Math.PI);<code>
	 */
	private Value<?> tan(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(Math.tan(numberValue));
	}

	/**
	 * Name: <code>Math.arcsin(num)</code> <br>
	 * Description: Returns the arc sine of a number <br>
	 * Parameter - Number: the number to get the arc sine of <br>
	 * Returns - Number: the arc sine of the number <br>
	 * Example: <code>Math.arcsin(Math.sin(Math.PI));<code>
	 */
	private Value<?> arcsin(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(Math.asin(numberValue));
	}

	/**
	 * Name: <code>Math.arccos(num)</code> <br>
	 * Description: Returns the arc cosine of a number <br>
	 * Parameter - Number: the number to get the arc cosine of <br>
	 * Returns - Number: the arc cosine of the number <br>
	 * Example: <code>Math.arccos(Math.cos(Math.PI));<code>
	 */
	private Value<?> arccos(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(Math.acos(numberValue));
	}

	/**
	 * Name: <code>Math.arctan(num)</code> <br>
	 * Description: Returns the arc tangent of a number <br>
	 * Parameter - Number: the number to get the arc tangent of <br>
	 * Returns - Number: the arc tangent of the number <br>
	 * Example: <code>Math.arctan(Math.tan(Math.PI));<code>
	 */
	private Value<?> arctan(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(Math.atan(numberValue));
	}

	/**
	 * Name: <code>Math.cosec(num)</code> <br>
	 * Description: Returns the cosecant of a number <br>
	 * Parameter - Number: the number to get the cosecant of <br>
	 * Returns - Number: the cosecant of the number <br>
	 * Example: <code>Math.cosec(Math.PI);<code>
	 */
	private Value<?> cosec(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(1 / Math.sin(numberValue));
	}

	/**
	 * Name: <code>Math.sec(num)</code> <br>
	 * Description: Returns the secant of a number <br>
	 * Parameter - Number: the number to get the secant of <br>
	 * Returns - Number: the secant of the number <br>
	 * Example: <code>Math.sec(Math.PI);<code>
	 */
	private Value<?> sec(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(1 / Math.cos(numberValue));
	}

	/**
	 * Name: <code>Math.cot(num)</code> <br>
	 * Description: Returns the cotangent of a number <br>
	 * Parameter - Number: the number to get the cotangent of <br>
	 * Returns - Number: the cotangent of the number <br>
	 * Example: <code>Math.cot(Math.PI);<code>
	 */
	private Value<?> cot(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(1 / Math.tan(numberValue));
	}

	@Override
	public Class<? extends BaseValue> getValueClass() {
		return null;
	}
}
