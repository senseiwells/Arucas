package me.senseiwells.arucas.extensions;

import me.senseiwells.arucas.api.IArucasExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.values.ListValue;
import me.senseiwells.arucas.values.NumberValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.functions.BuiltInFunction;
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
			new ListFunction("getIndex", "index", function -> modifyListIndex(function, false)),
			new ListFunction("removeIndex", "index", function -> modifyListIndex(function, true)),
			
			new ListFunction("append", "value", function -> {
				ListValue listValue = function.getValueForType(ListValue.class, 0, null);
				Value<?> value = function.getValueFromTable(function.argumentNames.get(1));
				listValue.value.add(value);
				return listValue;
			}),
			
			new ListFunction("concat", "otherList", function -> {
				ListValue list1 = function.getValueForType(ListValue.class, 0, null);
				ListValue list2 = function.getValueForType(ListValue.class, 1, null);
				list1.value.addAll(list2.value);
				return list1;
			})
		);
	}
	
	private static Value<?> modifyListIndex(BuiltInFunction function, boolean delete) throws CodeError {
		ListValue listValue = function.getValueForType(ListValue.class, 0, null);
		NumberValue numberValue = function.getValueForType(NumberValue.class, 1, null);
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
