package me.senseiwells.arucas.extensions;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.api.docs.MemberDoc;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.ValueTypes;
import me.senseiwells.arucas.values.BaseValue;
import me.senseiwells.arucas.values.NumberValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.functions.BuiltInFunction;

import java.util.List;
import java.util.Map;

import static me.senseiwells.arucas.utils.ValueTypes.NUMBER;

@ClassDoc(
	name = ValueTypes.MATH,
	desc = "Provides many basic math functions. This is a utility class, and cannot be constructed."
)
public class ArucasMathClass extends ArucasClassExtension {
	public ArucasMathClass() {
		super("Math");
	}

	@MemberDoc(isStatic = true, name = "pi", desc = "The value of pi", type = NUMBER, examples = "Math.pi;")
	@MemberDoc(isStatic = true, name = "e", desc = "The value of e", type = NUMBER, examples = "Math.e;")
	@MemberDoc(isStatic = true, name = "root2", desc = "The value of root 2", type = NUMBER, examples = "Math.root2;")
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

	@FunctionDoc(
		isStatic = true,
		name = "round",
		desc = "Rounds a number to the nearest integer",
		params = {NUMBER, "num", "the number to round"},
		returns = {NUMBER, "the rounded number"},
		example = "Math.round(3.5);"
	)
	private NumberValue round(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(Math.round(numberValue));
	}

	@FunctionDoc(
		isStatic = true,
		name = "ceil",
		desc = "Rounds a number up to the nearest integer",
		params = {NUMBER, "num", "the number to round"},
		returns = {NUMBER, "the rounded number"},
		example = "Math.ceil(3.5);"
	)
	private NumberValue ceil(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(Math.ceil(numberValue));
	}

	@FunctionDoc(
		isStatic = true,
		name = "floor",
		desc = "Rounds a number down to the nearest integer",
		params = {NUMBER, "num", "the number to round"},
		returns = {NUMBER, "the rounded number"},
		example = "Math.floor(3.5);"
	)
	private NumberValue floor(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(Math.floor(numberValue));
	}

	@FunctionDoc(
		isStatic = true,
		name = "sqrt",
		desc = "Returns the square root of a number",
		params = {NUMBER, "num", "the number to square root"},
		returns = {NUMBER, "the square root of the number"},
		example = "Math.sqrt(9);"
	)
	private Value<?> sqrt(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(Math.sqrt(numberValue));
	}

	@FunctionDoc(
		isStatic = true,
		name = "abs",
		desc = "Returns the absolute value of a number",
		params = {NUMBER, "num", "the number to get the absolute value of"},
		returns = {NUMBER, "the absolute value of the number"},
		example = "Math.abs(-3);"
	)
	private Value<?> abs(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(Math.abs(numberValue));
	}

	@FunctionDoc(
		isStatic = true,
		name = "mod",
		desc = "Returns the remainder of a division",
		params = {
			NUMBER, "num1", "the number to divide",
			NUMBER, "num2", "the divisor"
		},
		returns = {NUMBER, "the remainder of the division"},
		example = "Math.mod(5, 2);"
	)
	private Value<?> mod(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		double otherNumber = function.getParameterValueOfType(context, NumberValue.class, 1).value;
		return NumberValue.of(numberValue % otherNumber);
	}

	@FunctionDoc(
		isStatic = true,
		name = "max",
		desc = "Returns the largest number",
		params = {
			NUMBER, "num1", "the first number to compare",
			NUMBER, "num2", "the second number to compare"
		},
		returns = {NUMBER, "the largest number"},
		example = "Math.max(5, 2);"
	)
	private Value<?> max(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		double otherNumber = function.getParameterValueOfType(context, NumberValue.class, 1).value;
		return NumberValue.of(Math.max(numberValue, otherNumber));
	}

	@FunctionDoc(
		isStatic = true,
		name = "min",
		desc = "Returns the smallest number",
		params = {
			NUMBER, "num1", "the first number to compare",
			NUMBER, "num2", "the second number to compare"
		},
		returns = {NUMBER, "the smallest number"},
		example = "Math.min(5, 2);"
	)
	private Value<?> min(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		double otherNumber = function.getParameterValueOfType(context, NumberValue.class, 1).value;
		return NumberValue.of(Math.min(numberValue, otherNumber));
	}

	@FunctionDoc(
		isStatic = true,
		name = "clamp",
		desc = "Clamps a value between a minimum and maximum",
		params = {
			NUMBER, "value", "the value to clamp",
			NUMBER, "min", "the minimum",
			NUMBER, "max", "the maximum"
		},
		returns = {NUMBER, "the clamped value"},
		example = "Math.clamp(10, 2, 8);"
	)
	private Value<?> clamp(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		double minNumber = function.getParameterValueOfType(context, NumberValue.class, 1).value;
		double maxNumber = function.getParameterValueOfType(context, NumberValue.class, 2).value;
		return NumberValue.of(numberValue < minNumber ? minNumber : Math.min(numberValue, maxNumber));
	}

	@FunctionDoc(
		isStatic = true,
		name = "toRadians",
		desc = "Converts a number from degrees to radians",
		params = {NUMBER, "num", "the number to convert"},
		returns = {NUMBER, "the number in radians"},
		example = "Math.toRadians(90);"
	)
	private Value<?> toRadians(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(Math.toRadians(numberValue));
	}

	@FunctionDoc(
		isStatic = true,
		name = "toDegrees",
		desc = "Converts a number from radians to degrees",
		params = {NUMBER, "num", "the number to convert"},
		returns = {NUMBER, "the number in degrees"},
		example = "Math.toDegrees(Math.pi);"
	)
	private Value<?> toDegrees(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(Math.toDegrees(numberValue));
	}

	@FunctionDoc(
		isStatic = true,
		name = "log",
		desc = "Returns the natural logarithm of a number",
		params = {NUMBER, "num", "the number to get the logarithm of"},
		returns = {NUMBER, "the natural logarithm of the number"},
		example = "Math.log(Math.e);"
	)
	private Value<?> log(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(Math.log(numberValue));
	}

	@FunctionDoc(
		isStatic = true,
		name = "log",
		desc = "Returns the logarithm of a number with a specified base",
		params = {
			NUMBER, "base", "the base",
			NUMBER, "num", "the number to get the logarithm of",
		},
		returns = {NUMBER, "the logarithm of the number"},
		example = "Math.log(2, 4);"
	)
	private Value<?> logBase(Context context, BuiltInFunction function) throws CodeError {
		double baseNumber = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 1).value;
		return NumberValue.of(Math.log(numberValue) / Math.log(baseNumber));
	}

	@FunctionDoc(
		isStatic = true,
		name = "log10",
		desc = "Returns the base 10 logarithm of a number",
		params = {NUMBER, "num", "the number to get the logarithm of"},
		returns = {NUMBER, "the base 10 logarithm of the number"},
		example = "Math.log10(100);"
	)
	private Value<?> log10(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(Math.log10(numberValue));
	}

	@FunctionDoc(
		isStatic = true,
		name = "sin",
		desc = "Returns the sine of a number",
		params = {NUMBER, "num", "the number to get the sine of"},
		returns = {NUMBER, "the sine of the number"},
		example = "Math.sin(Math.pi);"
	)
	private Value<?> sin(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(Math.sin(numberValue));
	}

	@FunctionDoc(
		isStatic = true,
		name = "cos",
		desc = "Returns the cosine of a number",
		params = {NUMBER, "num", "the number to get the cosine of"},
		returns = {NUMBER, "the cosine of the number"},
		example = "Math.cos(Math.pi);"
	)
	private Value<?> cos(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(Math.cos(numberValue));
	}

	@FunctionDoc(
		isStatic = true,
		name = "tan",
		desc = "Returns the tangent of a number",
		params = {NUMBER, "num", "the number to get the tangent of"},
		returns = {NUMBER, "the tangent of the number"},
		example = "Math.tan(Math.pi);"
	)
	private Value<?> tan(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(Math.tan(numberValue));
	}

	@FunctionDoc(
		isStatic = true,
		name = "arcsin",
		desc = "Returns the arc sine of a number",
		params = {NUMBER, "num", "the number to get the arc sine of"},
		returns = {NUMBER, "the arc sine of the number"},
		example = "Math.arcsin(Math.sin(Math.pi));"
	)
	private Value<?> arcsin(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(Math.asin(numberValue));
	}

	@FunctionDoc(
		isStatic = true,
		name = "arccos",
		desc = "Returns the arc cosine of a number",
		params = {NUMBER, "num", "the number to get the arc cosine of"},
		returns = {NUMBER, "the arc cosine of the number"},
		example = "Math.arccos(Math.cos(Math.pi));"
	)
	private Value<?> arccos(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(Math.acos(numberValue));
	}

	@FunctionDoc(
		isStatic = true,
		name = "arctan",
		desc = "Returns the arc tangent of a number",
		params = {NUMBER, "num", "the number to get the arc tangent of"},
		returns = {NUMBER, "the arc tangent of the number"},
		example = "Math.arctan(Math.tan(Math.pi));"
	)
	private Value<?> arctan(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(Math.atan(numberValue));
	}

	@FunctionDoc(
		isStatic = true,
		name = "cosec",
		desc = "Returns the cosecant of a number",
		params = {NUMBER, "num", "the number to get the cosecant of"},
		returns = {NUMBER, "the cosecant of the number"},
		example = "Math.cosec(Math.pi);"
	)
	private Value<?> cosec(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(1 / Math.sin(numberValue));
	}

	@FunctionDoc(
		isStatic = true,
		name = "sec",
		desc = "Returns the secant of a number",
		params = {NUMBER, "num", "the number to get the secant of"},
		returns = {NUMBER, "the secant of the number"},
		example = "Math.sec(Math.pi);"
	)
	private Value<?> sec(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(1 / Math.cos(numberValue));
	}

	@FunctionDoc(
		isStatic = true,
		name = "cot",
		desc = "Returns the cotangent of a number",
		params = {NUMBER, "num", "the number to get the cotangent of"},
		returns = {NUMBER, "the cotangent of the number"},
		example = "Math.cot(Math.pi);"
	)
	private Value<?> cot(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(1 / Math.tan(numberValue));
	}

	@Override
	public Class<? extends BaseValue> getValueClass() {
		return null;
	}
}
