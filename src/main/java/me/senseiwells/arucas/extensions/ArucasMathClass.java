package me.senseiwells.arucas.extensions;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.NumberValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.functions.MemberFunction;

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
	public List<MemberFunction> getDefinedStaticMethods() {
		return List.of(
			new MemberFunction("round", "num", this::round),
			new MemberFunction("ceil", "num", this::ceil),
			new MemberFunction("floor", "num", this::floor),
			new MemberFunction("sqrt", "num", this::sqrt),
			new MemberFunction("abs", "num", this::abs),
			new MemberFunction("mod", List.of("num1", "num2"), this::mod),
			new MemberFunction("max", List.of("num1", "num2"), this::max),
			new MemberFunction("min", List.of("num1", "num2"), this::min),
			new MemberFunction("clamp", List.of("num", "min", "max"), this::clamp),
			new MemberFunction("toRandians", "num", this::toRadians),
			new MemberFunction("toDegrees", "num", this::toDegrees),
			new MemberFunction("log", "num", this::log),
			new MemberFunction("log10", "num", this::log10),
			new MemberFunction("sin", "num", this::sin),
			new MemberFunction("cos", "num", this::cos),
			new MemberFunction("tan", "num", this::tan),
			new MemberFunction("arcsin", "num", this::arcsin),
			new MemberFunction("arccos", "num", this::arccos),
			new MemberFunction("arctan", "num", this::arctan),
			new MemberFunction("cosec", "num", this::cosec),
			new MemberFunction("sec", "num", this::sec),
			new MemberFunction("cot", "num", this::cot)
		);
	}

	private NumberValue round(Context context, MemberFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(Math.round(numberValue));
	}

	private NumberValue ceil(Context context, MemberFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(Math.ceil(numberValue));
	}

	private NumberValue floor(Context context, MemberFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(Math.floor(numberValue));
	}

	private Value<?> sqrt(Context context, MemberFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(Math.sqrt(numberValue));
	}

	private Value<?> abs(Context context, MemberFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(Math.abs(numberValue));
	}

	private Value<?> mod(Context context, MemberFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		double otherNumber = function.getParameterValueOfType(context, NumberValue.class, 1).value;
		return NumberValue.of(numberValue % otherNumber);
	}

	private Value<?> max(Context context, MemberFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		double otherNumber = function.getParameterValueOfType(context, NumberValue.class, 1).value;
		return NumberValue.of(Math.max(numberValue, otherNumber));
	}

	private Value<?> min(Context context, MemberFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		double otherNumber = function.getParameterValueOfType(context, NumberValue.class, 1).value;
		return NumberValue.of(Math.min(numberValue, otherNumber));
	}

	private Value<?> clamp(Context context, MemberFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		double minNumber = function.getParameterValueOfType(context, NumberValue.class, 1).value;
		double maxNumber = function.getParameterValueOfType(context, NumberValue.class, 2).value;
		return NumberValue.of(numberValue < minNumber ? minNumber : Math.min(numberValue, maxNumber));
	}

	private Value<?> toRadians(Context context, MemberFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(Math.toRadians(numberValue));
	}

	private Value<?> toDegrees(Context context, MemberFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(Math.toDegrees(numberValue));
	}

	private Value<?> log(Context context, MemberFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(Math.log(numberValue));
	}

	private Value<?> log10(Context context, MemberFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(Math.log10(numberValue));
	}

	private Value<?> sin(Context context, MemberFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(Math.sin(numberValue));
	}

	private Value<?> cos(Context context, MemberFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(Math.cos(numberValue));
	}

	private Value<?> tan(Context context, MemberFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(Math.tan(numberValue));
	}

	private Value<?> arcsin(Context context, MemberFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(Math.asin(numberValue));
	}

	private Value<?> arccos(Context context, MemberFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(Math.acos(numberValue));
	}

	private Value<?> arctan(Context context, MemberFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(Math.atan(numberValue));
	}

	private Value<?> cosec(Context context, MemberFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(1 / Math.sin(numberValue));
	}

	private Value<?> sec(Context context, MemberFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(1 / Math.cos(numberValue));
	}

	private Value<?> cot(Context context, MemberFunction function) throws CodeError {
		double numberValue = function.getParameterValueOfType(context, NumberValue.class, 0).value;
		return NumberValue.of(1 / Math.tan(numberValue));
	}
}
