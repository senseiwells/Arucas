package me.senseiwells.arucas.extensions;

import me.senseiwells.arucas.api.IArucasExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.ListValue;
import me.senseiwells.arucas.values.NumberValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.functions.FunctionDefinition;

import java.util.List;
import java.util.Set;

public class ArucasListExtension implements IArucasExtension {
	
	@Override
	public String getName() {
		return "ListExtension";
	}
	
	@Override
	public Set<BuiltInFunction> getDefinedFunctions() {
		return Set.of(
			new ListFunction("getIndex", "index", (context, function) -> modifyListIndex(context, function, false)),
			new ListFunction("removeIndex", "index", (context, function) -> modifyListIndex(context, function, true)),
			
			new ListFunction("append", "value", (context, function) -> {
				ListValue listValue = function.getParameterValueOfType(context, ListValue.class, 0);
				Value<?> value = function.getParameterValue(context, 1);
				listValue.value.add(value);
				return listValue;
			}),
			
			new ListFunction("concat", "otherList", (context, function) -> {
				ListValue list1 = function.getParameterValueOfType(context, ListValue.class, 0);
				ListValue list2 = function.getParameterValueOfType(context, ListValue.class, 1);
				list1.value.addAll(list2.value);
				return list1;
			})
		);
	}
	
	private static Value<?> modifyListIndex(Context context, BuiltInFunction function, boolean delete) throws CodeError {
		ListValue listValue = function.getParameterValueOfType(context, ListValue.class, 0);
		NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 1);
		int index = numberValue.value.intValue();
		if (index >= listValue.value.size() || index < 0)
			throw function.throwInvalidParameterError("Parameter 2 is out of bounds");
		return delete ? listValue.value.remove(index) : listValue.value.get(index);
	}
	
	public static class ListFunction extends BuiltInFunction {
		public ListFunction(String name, String argument, FunctionDefinition function) {
			super(name, List.of("list", argument), function);
		}
	}
}
