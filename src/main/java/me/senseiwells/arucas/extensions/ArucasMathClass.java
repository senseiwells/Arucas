package me.senseiwells.arucas.extensions;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.api.docs.MemberDoc;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Arguments;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.ValueTypes;
import me.senseiwells.arucas.values.NumberValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.functions.BuiltInFunction;

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
	public Map<String, Value> getDefinedStaticVariables() {
		return Map.of(
			"pi", NumberValue.of(Math.PI),
			"e", NumberValue.of(Math.E),
			"root2", NumberValue.of(Math.sqrt(2))
		);
	}

	@Override
	public ArucasFunctionMap<BuiltInFunction> getDefinedStaticMethods() {
		return ArucasFunctionMap.of(
			BuiltInFunction.of("round", 1, this::round),
			BuiltInFunction.of("ceil", 1, this::ceil),
			BuiltInFunction.of("floor", 1, this::floor),
			BuiltInFunction.of("sqrt", 1, this::sqrt),
			BuiltInFunction.of("abs", 1, this::abs),
			BuiltInFunction.of("mod", 2, this::mod),
			BuiltInFunction.of("rem", 2, this::rem),
			BuiltInFunction.of("max", 2, this::max),
			BuiltInFunction.of("min", 2, this::min),
			BuiltInFunction.of("clamp", 3, this::clamp),
			BuiltInFunction.of("toRadians", 1, this::toRadians),
			BuiltInFunction.of("toDegrees", 1, this::toDegrees),
			BuiltInFunction.of("log", 1, this::log),
			BuiltInFunction.of("log", 2, this::logBase),
			BuiltInFunction.of("log10", 1, this::log10),
			BuiltInFunction.of("sin", 1, this::sin),
			BuiltInFunction.of("cos", 1, this::cos),
			BuiltInFunction.of("tan", 1, this::tan),
			BuiltInFunction.of("arcsin", 1, this::arcsin),
			BuiltInFunction.of("arccos", 1, this::arccos),
			BuiltInFunction.of("arctan", 1, this::arctan),
			BuiltInFunction.of("cosec", 1, this::cosec),
			BuiltInFunction.of("sec", 1, this::sec),
			BuiltInFunction.of("cot", 1, this::cot)
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
	private NumberValue round(Arguments arguments) throws CodeError {
		double number = arguments.getNextGeneric(NumberValue.class);
		return NumberValue.of(Math.round(number));
	}

	@FunctionDoc(
		isStatic = true,
		name = "ceil",
		desc = "Rounds a number up to the nearest integer",
		params = {NUMBER, "num", "the number to round"},
		returns = {NUMBER, "the rounded number"},
		example = "Math.ceil(3.5);"
	)
	private NumberValue ceil(Arguments arguments) throws CodeError {
		double number = arguments.getNextGeneric(NumberValue.class);
		return NumberValue.of(Math.ceil(number));
	}

	@FunctionDoc(
		isStatic = true,
		name = "floor",
		desc = "Rounds a number down to the nearest integer",
		params = {NUMBER, "num", "the number to round"},
		returns = {NUMBER, "the rounded number"},
		example = "Math.floor(3.5);"
	)
	private NumberValue floor(Arguments arguments) throws CodeError {
		double number = arguments.getNextGeneric(NumberValue.class);
		return NumberValue.of(Math.floor(number));
	}

	@FunctionDoc(
		isStatic = true,
		name = "sqrt",
		desc = "Returns the square root of a number",
		params = {NUMBER, "num", "the number to square root"},
		returns = {NUMBER, "the square root of the number"},
		example = "Math.sqrt(9);"
	)
	private Value sqrt(Arguments arguments) throws CodeError {
		double number = arguments.getNextGeneric(NumberValue.class);
		return NumberValue.of(Math.sqrt(number));
	}

	@FunctionDoc(
		isStatic = true,
		name = "abs",
		desc = "Returns the absolute value of a number",
		params = {NUMBER, "num", "the number to get the absolute value of"},
		returns = {NUMBER, "the absolute value of the number"},
		example = "Math.abs(-3);"
	)
	private Value abs(Arguments arguments) throws CodeError {
		double number = arguments.getNextGeneric(NumberValue.class);
		return NumberValue.of(Math.abs(number));
	}

	@FunctionDoc(
		isStatic = true,
		name = "mod",
		desc = "Returns the modulus of a division",
		params = {
			NUMBER, "num1", "the number to divide",
			NUMBER, "num2", "the divisor"
		},
		returns = {NUMBER, "the modulus of the division"},
		example = "Math.mod(5, 2);"
	)
	private Value mod(Arguments arguments) throws CodeError {
		double number = arguments.getNextGeneric(NumberValue.class);
		double otherNumber = arguments.getNextGeneric(NumberValue.class);
		return NumberValue.of((number % otherNumber + otherNumber) % otherNumber);
	}

	@FunctionDoc(
		isStatic = true,
		name = "rem",
		desc = "Returns the remainder of a division",
		params = {
			NUMBER, "num1", "the number to divide",
			NUMBER, "num2", "the divisor"
		},
		returns = {NUMBER, "the remainder of the division"},
		example = "Math.rem(5, 2);"
	)
	private Value rem(Arguments arguments) throws CodeError {
		double number = arguments.getNextGeneric(NumberValue.class);
		double otherNumber = arguments.getNextGeneric(NumberValue.class);
		return NumberValue.of(number % otherNumber);
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
	private Value max(Arguments arguments) throws CodeError {
		double number = arguments.getNextGeneric(NumberValue.class);
		double otherNumber = arguments.getNextGeneric(NumberValue.class);
		return NumberValue.of(Math.max(number, otherNumber));
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
	private Value min(Arguments arguments) throws CodeError {
		double number = arguments.getNextGeneric(NumberValue.class);
		double otherNumber = arguments.getNextGeneric(NumberValue.class);
		return NumberValue.of(Math.min(number, otherNumber));
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
	private Value clamp(Arguments arguments) throws CodeError {
		double number = arguments.getNextGeneric(NumberValue.class);
		double minNumber = arguments.getNextGeneric(NumberValue.class);
		double maxNumber = arguments.getNextGeneric(NumberValue.class);
		return NumberValue.of(number < minNumber ? minNumber : Math.min(number, maxNumber));
	}

	@FunctionDoc(
		isStatic = true,
		name = "toRadians",
		desc = "Converts a number from degrees to radians",
		params = {NUMBER, "num", "the number to convert"},
		returns = {NUMBER, "the number in radians"},
		example = "Math.toRadians(90);"
	)
	private Value toRadians(Arguments arguments) throws CodeError {
		double number = arguments.getNextGeneric(NumberValue.class);
		return NumberValue.of(Math.toRadians(number));
	}

	@FunctionDoc(
		isStatic = true,
		name = "toDegrees",
		desc = "Converts a number from radians to degrees",
		params = {NUMBER, "num", "the number to convert"},
		returns = {NUMBER, "the number in degrees"},
		example = "Math.toDegrees(Math.pi);"
	)
	private Value toDegrees(Arguments arguments) throws CodeError {
		double number = arguments.getNextGeneric(NumberValue.class);
		return NumberValue.of(Math.toDegrees(number));
	}

	@FunctionDoc(
		isStatic = true,
		name = "log",
		desc = "Returns the natural logarithm of a number",
		params = {NUMBER, "num", "the number to get the logarithm of"},
		returns = {NUMBER, "the natural logarithm of the number"},
		example = "Math.log(Math.e);"
	)
	private Value log(Arguments arguments) throws CodeError {
		double number = arguments.getNextGeneric(NumberValue.class);
		return NumberValue.of(Math.log(number));
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
	private Value logBase(Arguments arguments) throws CodeError {
		double baseNumber = arguments.getNextGeneric(NumberValue.class);
		double number = arguments.getNextGeneric(NumberValue.class);
		return NumberValue.of(Math.log(number) / Math.log(baseNumber));
	}

	@FunctionDoc(
		isStatic = true,
		name = "log10",
		desc = "Returns the base 10 logarithm of a number",
		params = {NUMBER, "num", "the number to get the logarithm of"},
		returns = {NUMBER, "the base 10 logarithm of the number"},
		example = "Math.log10(100);"
	)
	private Value log10(Arguments arguments) throws CodeError {
		double number = arguments.getNextGeneric(NumberValue.class);
		return NumberValue.of(Math.log10(number));
	}

	@FunctionDoc(
		isStatic = true,
		name = "sin",
		desc = "Returns the sine of a number",
		params = {NUMBER, "num", "the number to get the sine of"},
		returns = {NUMBER, "the sine of the number"},
		example = "Math.sin(Math.pi);"
	)
	private Value sin(Arguments arguments) throws CodeError {
		double number = arguments.getNextGeneric(NumberValue.class);
		return NumberValue.of(Math.sin(number));
	}

	@FunctionDoc(
		isStatic = true,
		name = "cos",
		desc = "Returns the cosine of a number",
		params = {NUMBER, "num", "the number to get the cosine of"},
		returns = {NUMBER, "the cosine of the number"},
		example = "Math.cos(Math.pi);"
	)
	private Value cos(Arguments arguments) throws CodeError {
		double number = arguments.getNextGeneric(NumberValue.class);
		return NumberValue.of(Math.cos(number));
	}

	@FunctionDoc(
		isStatic = true,
		name = "tan",
		desc = "Returns the tangent of a number",
		params = {NUMBER, "num", "the number to get the tangent of"},
		returns = {NUMBER, "the tangent of the number"},
		example = "Math.tan(Math.pi);"
	)
	private Value tan(Arguments arguments) throws CodeError {
		double number = arguments.getNextGeneric(NumberValue.class);
		return NumberValue.of(Math.tan(number));
	}

	@FunctionDoc(
		isStatic = true,
		name = "arcsin",
		desc = "Returns the arc sine of a number",
		params = {NUMBER, "num", "the number to get the arc sine of"},
		returns = {NUMBER, "the arc sine of the number"},
		example = "Math.arcsin(Math.sin(Math.pi));"
	)
	private Value arcsin(Arguments arguments) throws CodeError {
		double number = arguments.getNextGeneric(NumberValue.class);
		return NumberValue.of(Math.asin(number));
	}

	@FunctionDoc(
		isStatic = true,
		name = "arccos",
		desc = "Returns the arc cosine of a number",
		params = {NUMBER, "num", "the number to get the arc cosine of"},
		returns = {NUMBER, "the arc cosine of the number"},
		example = "Math.arccos(Math.cos(Math.pi));"
	)
	private Value arccos(Arguments arguments) throws CodeError {
		double number = arguments.getNextGeneric(NumberValue.class);
		return NumberValue.of(Math.acos(number));
	}

	@FunctionDoc(
		isStatic = true,
		name = "arctan",
		desc = "Returns the arc tangent of a number",
		params = {NUMBER, "num", "the number to get the arc tangent of"},
		returns = {NUMBER, "the arc tangent of the number"},
		example = "Math.arctan(Math.tan(Math.pi));"
	)
	private Value arctan(Arguments arguments) throws CodeError {
		double number = arguments.getNextGeneric(NumberValue.class);
		return NumberValue.of(Math.atan(number));
	}

	@FunctionDoc(
		isStatic = true,
		name = "cosec",
		desc = "Returns the cosecant of a number",
		params = {NUMBER, "num", "the number to get the cosecant of"},
		returns = {NUMBER, "the cosecant of the number"},
		example = "Math.cosec(Math.pi);"
	)
	private Value cosec(Arguments arguments) throws CodeError {
		double number = arguments.getNextGeneric(NumberValue.class);
		return NumberValue.of(1 / Math.sin(number));
	}

	@FunctionDoc(
		isStatic = true,
		name = "sec",
		desc = "Returns the secant of a number",
		params = {NUMBER, "num", "the number to get the secant of"},
		returns = {NUMBER, "the secant of the number"},
		example = "Math.sec(Math.pi);"
	)
	private Value sec(Arguments arguments) throws CodeError {
		double number = arguments.getNextGeneric(NumberValue.class);
		return NumberValue.of(1 / Math.cos(number));
	}

	@FunctionDoc(
		isStatic = true,
		name = "cot",
		desc = "Returns the cotangent of a number",
		params = {NUMBER, "num", "the number to get the cotangent of"},
		returns = {NUMBER, "the cotangent of the number"},
		example = "Math.cot(Math.pi);"
	)
	private Value cot(Arguments arguments) throws CodeError {
		double number = arguments.getNextGeneric(NumberValue.class);
		return NumberValue.of(1 / Math.tan(number));
	}

	@Override
	public Class<? extends Value> getValueClass() {
		return null;
	}
}
