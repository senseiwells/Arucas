package me.senseiwells.arucas.extensions;

import me.senseiwells.arucas.api.IArucasValueExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.BooleanValue;
import me.senseiwells.arucas.values.NumberValue;
import me.senseiwells.arucas.values.functions.MemberFunction;

import java.util.Set;

public class ArucasNumberMembers implements IArucasValueExtension {

	@Override
	public Set<MemberFunction> getDefinedFunctions() {
		return this.numberFunctions;
	}
	
	@Override
	public Class<NumberValue> getValueType() {
		return NumberValue.class;
	}
	
	@Override
	public String getName() {
		return "NumberMemberFunctions";
	}

	private final Set<MemberFunction> numberFunctions = Set.of(
		new MemberFunction("round", this::numberRound),
		new MemberFunction("ceil", this::numberCeil),
		new MemberFunction("floor", this::numberFloor),
		new MemberFunction("modulus", "otherNumber", this::numberModulus),
		new MemberFunction("toRadians", this::toRadians),
		new MemberFunction("toDegrees", this::toDegrees),
		new MemberFunction("absolute", this::numberAbsolute),
		new MemberFunction("isInfinite", this::numberIsInfinite),
		new MemberFunction("isNaN", this::numberIsNan)
	);

	private NumberValue numberRound(Context context, MemberFunction function) throws CodeError {
		NumberValue numValue = function.getParameterValueOfType(context, NumberValue.class, 0);
		return new NumberValue(Math.round(numValue.value));
	}

	private NumberValue numberCeil(Context context, MemberFunction function) throws CodeError {
		NumberValue numValue = function.getParameterValueOfType(context, NumberValue.class, 0);
		return new NumberValue(Math.ceil(numValue.value));
	}

	private NumberValue numberFloor(Context context, MemberFunction function) throws CodeError {
		NumberValue numValue = function.getParameterValueOfType(context, NumberValue.class, 0);
		return new NumberValue(Math.floor(numValue.value));
	}

	private NumberValue numberModulus(Context context, MemberFunction function) throws CodeError {
		NumberValue numberValue1 = function.getParameterValueOfType(context, NumberValue.class, 0);
		NumberValue numberValue2 = function.getParameterValueOfType(context, NumberValue.class, 1);
		return new NumberValue(numberValue1.value % numberValue2.value);
	}

	private NumberValue numberAbsolute(Context context, MemberFunction function) throws CodeError {
		NumberValue value = function.getParameterValueOfType(context, NumberValue.class, 0);
		return new NumberValue(Math.abs(value.value));
	}
	private NumberValue toRadians(Context context, MemberFunction function) throws CodeError {
		NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 0);
		return new NumberValue(Math.toRadians(numberValue.value));
	}

	private NumberValue toDegrees(Context context, MemberFunction function) throws CodeError {
		NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 0);
		return new NumberValue(Math.toDegrees(numberValue.value));
	}

	private BooleanValue numberIsInfinite(Context context, MemberFunction function) throws CodeError {
		NumberValue value = function.getParameterValueOfType(context, NumberValue.class, 0);
		return BooleanValue.of(value.value.isInfinite());
	}

	private BooleanValue numberIsNan(Context context, MemberFunction function) throws CodeError {
		NumberValue value = function.getParameterValueOfType(context, NumberValue.class, 0);
		return BooleanValue.of(value.value.isNaN());
	}

}
