package me.senseiwells.arucas.extensions;

import me.senseiwells.arucas.api.IArucasValueExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.BooleanValue;
import me.senseiwells.arucas.values.ListValue;
import me.senseiwells.arucas.values.NumberValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.functions.MemberFunction;

import java.util.List;
import java.util.Set;

public class ArucasListMembers implements IArucasValueExtension {
	private final Object LIST_LOCK = new Object();

	@Override
	public Set<MemberFunction> getDefinedFunctions() {
		return this.listFunctions;
	}
	
	@Override
	public Class<ListValue> getValueType() {
		return ListValue.class;
	}
	
	@Override
	public String getName() {
		return "ListMemberFunctions";
	}

	private final Set<MemberFunction> listFunctions = Set.of(
		new MemberFunction("getIndex", "index", this::getListIndex),
		new MemberFunction("get", "index", this::getListIndex),
		new MemberFunction("removeIndex", "index", this::removeListIndex),
		new MemberFunction("remove", "index", this::removeListIndex),
		new MemberFunction("append", "value", this::appendList),
		new MemberFunction("insert", List.of("value", "index"), this::insertList),
		new MemberFunction("concat", "otherList", this::concatList),
		new MemberFunction("contains", "value", this::listContains),
		new MemberFunction("containsAll", "otherList", this::containsAll),
		new MemberFunction("isEmpty", this::isEmpty)
	);

	private Value<?> getListIndex(Context context, MemberFunction function) throws CodeError {
		synchronized (LIST_LOCK) {
			ListValue listValue = function.getParameterValueOfType(context, ListValue.class, 0);
			NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 1);
			int index = numberValue.value.intValue();
			if (index >= listValue.value.size() || index < 0) {
				throw function.throwInvalidParameterError("Index is out of bounds", context);
			}
			return listValue.value.get(index);
		}
	}

	private Value<?> removeListIndex(Context context, MemberFunction function) throws CodeError {
		synchronized (LIST_LOCK) {
			ListValue listValue = function.getParameterValueOfType(context, ListValue.class, 0);
			NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 1);
			int index = numberValue.value.intValue();
			if (index >= listValue.value.size() || index < 0) {
				throw function.throwInvalidParameterError("Index is out of bounds", context);
			}
			return listValue.value.remove(index);
		}
	}

	private Value<?> appendList(Context context, MemberFunction function) throws CodeError {
		synchronized (LIST_LOCK) {
			ListValue listValue = function.getParameterValueOfType(context, ListValue.class, 0);
			Value<?> value = function.getParameterValue(context, 1);
			listValue.value.add(value);
			return listValue;
		}
	}

	private Value<?> insertList(Context context, MemberFunction function) throws CodeError {
		synchronized (LIST_LOCK) {
			ListValue listValue = function.getParameterValueOfType(context, ListValue.class, 0);
			Value<?> value = function.getParameterValue(context, 1);
			int index = function.getParameterValueOfType(context, NumberValue.class, 2).value.intValue();
			int len = listValue.value.size();
			if (index > len || index < 0) {
				throw new RuntimeError("Index is out of bounds", function.syntaxPosition, context);
			}
			listValue.value.add(index, value);
			return listValue;
		}
	}

	private Value<?> concatList(Context context, MemberFunction function) throws CodeError {
		synchronized (LIST_LOCK) {
			ListValue list1 = function.getParameterValueOfType(context, ListValue.class, 0);
			ListValue list2 = function.getParameterValueOfType(context, ListValue.class, 1);
			list1.value.addAll(list2.value);
			return list1;
		}
	}

	private BooleanValue listContains(Context context, MemberFunction function) throws CodeError {
		synchronized (LIST_LOCK) {
			ListValue listValue = function.getParameterValueOfType(context, ListValue.class, 0);
			Value<?> value = function.getParameterValue(context, 1);
			return BooleanValue.of(listValue.value.contains(value));
		}
	}

	private BooleanValue containsAll(Context context, MemberFunction function) throws CodeError {
		synchronized (LIST_LOCK) {
			ListValue listValue = function.getParameterValueOfType(context, ListValue.class, 0);
			ListValue otherList = function.getParameterValueOfType(context, ListValue.class, 1);
			return BooleanValue.of(listValue.value.containsAll(otherList.value));
		}
	}

	private BooleanValue isEmpty(Context context, MemberFunction function) throws CodeError {
		synchronized (LIST_LOCK) {
			ListValue listValue = function.getParameterValueOfType(context, ListValue.class, 0);
			return BooleanValue.of(listValue.value.isEmpty());
		}
	}
}
