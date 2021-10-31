package me.senseiwells.arucas.values;

import me.senseiwells.arucas.extensions.BuiltInFunction;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.functions.MemberFunction;

import java.util.List;
import java.util.Set;

public class ListValue extends Value<List<Value<?>>> {
	private final Object LIST_LOCK = new Object();

	public ListValue(List<Value<?>> value) {
		super(value);
	}

	@Override
	public Value<List<Value<?>>> copy() {
		return new ListValue(this.value).setPos(this.startPos, this.endPos);
	}
	
	@Override
	public String toString() {
		final Value<?>[] array = this.value.toArray(Value<?>[]::new);
		if (array.length == 0) return "[]";
		
		StringBuilder sb = new StringBuilder();
		for (Value<?> element : array) {
			if (element instanceof ListValue) {
				sb.append(", <list>");
			}
			else if (element instanceof StringValue) {
				sb.append(", \"%s\"".formatted(element.toString()));
			}
			else {
				sb.append(", ").append(element);
			}
		}
		sb.deleteCharAt(0);
		
		return "[%s]".formatted(sb.toString().trim());
	}

	@Override
	public Set<? extends MemberFunction> getMembers() {
		return Set.of(
			new MemberFunction("getIndex", "index", this::getListIndex),
			new MemberFunction("removeIndex", "index", this::removeListIndex),
			new MemberFunction("append", "value", this::appendList),
			new MemberFunction("concat", "otherList", this::concatList)
		);
	}

	private Value<?> concatList(Context context, BuiltInFunction function) throws CodeError {
		synchronized (LIST_LOCK) {
			ListValue list1 = function.getParameterValueOfType(context, ListValue.class, 0);
			ListValue list2 = function.getParameterValueOfType(context, ListValue.class, 1);
			list1.value.addAll(list2.value);
			return list1;
		}
	}

	private Value<?> appendList(Context context, BuiltInFunction function) throws CodeError {
		synchronized (LIST_LOCK) {
			ListValue listValue = function.getParameterValueOfType(context, ListValue.class, 0);
			Value<?> value = function.getParameterValue(context, 1);
			listValue.value.add(value);
			return listValue;
		}
	}

	private Value<?> removeListIndex(Context context, BuiltInFunction function) throws CodeError {
		synchronized (LIST_LOCK) {
			ListValue listValue = function.getParameterValueOfType(context, ListValue.class, 0);
			NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 1);
			int index = numberValue.value.intValue();
			if (index >= listValue.value.size() || index < 0) {
				throw function.throwInvalidParameterError("Parameter 2 is out of bounds", context);
			}

			return listValue.value.remove(index);
		}
	}

	private Value<?> getListIndex(Context context, BuiltInFunction function) throws CodeError {
		synchronized (LIST_LOCK) {
			ListValue listValue = function.getParameterValueOfType(context, ListValue.class, 0);
			NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 1);
			int index = numberValue.value.intValue();
			if (index >= listValue.value.size() || index < 0) {
				throw function.throwInvalidParameterError("Parameter 2 is out of bounds", context);
			}

			return listValue.value.get(index);
		}
	}
}
