package me.senseiwells.arucas.extensions;

import me.senseiwells.arucas.api.IArucasValueExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.MemberFunction;

import java.util.Set;

public class ArucasBuiltInMembers implements IArucasValueExtension {
	@Override
	public Set<MemberFunction> getDefinedFunctions() {
		return this.functions;
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public Class<Value> getValueType() {
		return Value.class;
	}
	
	@Override
	public String getName() {
		return "BuiltInMemberFunctions";
	}
	
	private final Set<MemberFunction> functions = Set.of(
		new MemberFunction("instanceOf", "class", this::instanceOf),
		new MemberFunction("getValueType", this::getValueType),
		new MemberFunction("copy", (context, function) -> function.getParameterValue(context, 0).newCopy()),
		new MemberFunction("toString", (context, function) -> new StringValue(function.getParameterValue(context, 0).toString()))
	);
	
	private Value<?> instanceOf(Context context, MemberFunction function) throws CodeError {
		StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 1);
		Value<?> value = function.getParameterValue(context, 0);
		
		if (stringValue.value.isEmpty()) {
			return BooleanValue.FALSE;
		}
		
		Class<?> clazz = value.getClass();
		while (clazz != null && clazz != Object.class) {
			if (clazz.getSimpleName().replaceFirst("Value$", "").equals(stringValue.value)) {
				return BooleanValue.TRUE;
			}
			
			clazz = clazz.getSuperclass();
		}
		
		return BooleanValue.FALSE;
	}
	
	private Value<?> getValueType(Context context, MemberFunction function) {
		Value<?> value = function.getParameterValue(context, 0);
		String valueType = value.getClass().getSimpleName().replaceFirst("Value$", "");
		return new StringValue(valueType);
	}
}
