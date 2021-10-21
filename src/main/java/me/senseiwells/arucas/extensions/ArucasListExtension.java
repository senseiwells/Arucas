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
	private final Object LIST_ACCESS_LOCK = new Object();
	
	@Override
	public String getName() {
		return "ListExtension";
	}
	
	@Override
	public Set<BuiltInFunction> getDefinedFunctions() {
		return Set.of(
			new ListFunction("getIndex", "index", this::getListIndex),
			new ListFunction("removeIndex", "index", this::removeListIndex),
			new ListFunction("append", "value", this::appendList),
			new ListFunction("concat", "otherList", this::concatList)
		);
	}
	
	private Value<?> concatList(Context context, BuiltInFunction function) throws CodeError {
		synchronized (LIST_ACCESS_LOCK) {
			ListValue list1 = function.getParameterValueOfType(context, ListValue.class, 0);
			ListValue list2 = function.getParameterValueOfType(context, ListValue.class, 1);
			list1.value.addAll(list2.value);
			return list1;
		}
	}
	
	private Value<?> appendList(Context context, BuiltInFunction function) throws CodeError {
		synchronized (LIST_ACCESS_LOCK) {
			ListValue listValue = function.getParameterValueOfType(context, ListValue.class, 0);
			Value<?> value = function.getParameterValue(context, 1);
			listValue.value.add(value);
			return listValue;
		}
	}
	
	private Value<?> removeListIndex(Context context, BuiltInFunction function) throws CodeError {
		synchronized (LIST_ACCESS_LOCK) {
			ListValue listValue = function.getParameterValueOfType(context, ListValue.class, 0);
			NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 1);
			int index = numberValue.value.intValue();
			if (index >= listValue.value.size() || index < 0)
				throw function.throwInvalidParameterError("Parameter 2 is out of bounds");
			
			return listValue.value.remove(index);
		}
	}
	
	private Value<?> getListIndex(Context context, BuiltInFunction function) throws CodeError {
		synchronized (LIST_ACCESS_LOCK) {
			ListValue listValue = function.getParameterValueOfType(context, ListValue.class, 0);
			NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 1);
			int index = numberValue.value.intValue();
			if (index >= listValue.value.size() || index < 0)
				throw function.throwInvalidParameterError("Parameter 2 is out of bounds");
			
			return listValue.value.get(index);
		}
	}
	
	public static class ListFunction extends BuiltInFunction {
		public ListFunction(String name, String argument, FunctionDefinition function) {
			super(name, List.of("list", argument), function);
		}
	}
}
