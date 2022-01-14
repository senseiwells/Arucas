package me.senseiwells.arucas.extensions;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.NumberValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.functions.BuiltInFunction;

import java.util.List;
import java.util.Map;

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

	private NumberValue round(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(Math.round(numberValue));
	}

	private NumberValue ceil(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(Math.ceil(numberValue));
	}

	private NumberValue floor(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(Math.floor(numberValue));
	}

	private Value<?> sqrt(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(Math.sqrt(numberValue));
	}

	private Value<?> abs(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(Math.abs(numberValue));
	}

	private Value<?> mod(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		double otherNumber = function.getParameterValueOfType(context, NumberValue.class, 1).value;
		return NumberValue.of(numberValue % otherNumber);
	}

	private Value<?> max(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		double otherNumber = function.getParameterValueOfType(context, NumberValue.class, 1).value;
		return NumberValue.of(Math.max(numberValue, otherNumber));
	}

	private Value<?> min(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		double otherNumber = function.getParameterValueOfType(context, NumberValue.class, 1).value;
		return NumberValue.of(Math.min(numberValue, otherNumber));
	}

	private Value<?> clamp(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		double minNumber = function.getParameterValueOfType(context, NumberValue.class, 1).value;
		double maxNumber = function.getParameterValueOfType(context, NumberValue.class, 2).value;
		return NumberValue.of(numberValue < minNumber ? minNumber : Math.min(numberValue, maxNumber));
	}

	private Value<?> toRadians(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(Math.toRadians(numberValue));
	}

	private Value<?> toDegrees(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(Math.toDegrees(numberValue));
	}

	private Value<?> log(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(Math.log(numberValue));
	}

	private Value<?> log10(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(Math.log10(numberValue));
	}

	private Value<?> sin(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(Math.sin(numberValue));
	}

	private Value<?> cos(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(Math.cos(numberValue));
	}

	private Value<?> tan(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(Math.tan(numberValue));
	}

	private Value<?> arcsin(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(Math.asin(numberValue));
	}

	private Value<?> arccos(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(Math.acos(numberValue));
	}

	private Value<?> arctan(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(Math.atan(numberValue));
	}

	private Value<?> cosec(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(1 / Math.sin(numberValue));
	}

	private Value<?> sec(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(1 / Math.cos(numberValue));
	}

	private Value<?> cot(Context context, BuiltInFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(1 / Math.tan(numberValue));
	}

	@Override
	public Class<?> getValueClass() {
		return null;
	}
}
