package me.senseiwells.arucas.values.functions;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.Value;

import java.util.ArrayList;
import java.util.List;

public class MemberFunction extends AbstractBuiltInFunction<MemberFunction> {
	public MemberFunction(String name, List<String> argumentNames, FunctionDefinition<MemberFunction> function, boolean isDeprecated) {
		super(name, addThis(argumentNames), function, isDeprecated);
	}

	public MemberFunction(String name, List<String> argumentNames, FunctionDefinition<MemberFunction> function) {
		super(name, addThis(argumentNames), function, false);
	}

	public MemberFunction(String name, String argument, FunctionDefinition<MemberFunction> function) {
		this(name, List.of(argument), function, false);
	}

	public MemberFunction(String name, FunctionDefinition<MemberFunction> function) {
		this(name, List.of(), function, false);
	}

	private static List<String> addThis(List<String> stringList) {
		if (stringList.isEmpty() || !stringList.get(0).equals("this")) {
			stringList = new ArrayList<>(stringList);
			stringList.add(0, "this");
		}
		return stringList;
	}

	@Override
	public Value<?> execute(Context context, List<Value<?>> arguments) throws CodeError {
		this.checkDeprecated(context);
		this.checkAndPopulateArguments(context, arguments, this.argumentNames);
		return this.function.execute(context, this);
	}

	@Override
	public <T extends Value<?>> T getParameterValueOfType(Context context, Class<T> clazz, int index, String additionalInfo) throws CodeError {
		if (index > 0) {
			return super.getParameterValueOfType(context, clazz, index, additionalInfo);
		}
		Value<?> value = this.getParameterValue(context, index);
		if (!clazz.isInstance(value)) {
			throw this.throwInvalidParameterError("Only %s can call the method %s()%s".formatted(
					clazz.getSimpleName(), this.value, additionalInfo == null ? "" : ("\n" + additionalInfo)
			), context);
		}
		return clazz.cast(value);
	}

	@Override
	public <T extends Value<?>> T getParameterValueOfType(Context context, Class<T> clazz, int index) throws CodeError {
		return this.getParameterValueOfType(context, clazz, index, null);
	}

	@Override
	public Value<?> copy() {
		return new MemberFunction(this.value, this.argumentNames, this.function, this.isDeprecated).setPos(this.startPos, this.endPos);
	}
}
