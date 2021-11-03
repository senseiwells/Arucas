package me.senseiwells.arucas.extensions;

import me.senseiwells.arucas.api.IArucasExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.ListValue;
import me.senseiwells.arucas.values.NumberValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.functions.MemberFunction;

import java.util.Set;

public class ArucasListMembers implements IArucasExtension {
	private final Object LIST_LOCK = new Object();

	@Override
	public Set<MemberFunction> getDefinedFunctions() {
		return this.listFunctions;
	}

	@Override
	public String getName() {
		return "ListMemberFunctions";
	}

	private final Set<MemberFunction> listFunctions = Set.of(
		new MemberFunction("getIndex", "index", this::getListIndex),
		new MemberFunction("removeIndex", "index", this::removeListIndex),
		new MemberFunction("append", "value", this::appendList),
		new MemberFunction("concat", "otherList", this::concatList)
	);

	private Value<?> appendList(Context context, MemberFunction function) throws CodeError {
		synchronized (LIST_LOCK) {
			ListValue listValue = function.getParameterValueOfType(context, ListValue.class, 0);
			Value<?> value = function.getParameterValue(context, 1);
			listValue.value.add(value);
			return listValue;
		}
	}

	private Value<?> removeListIndex(Context context, MemberFunction function) throws CodeError {
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

	private Value<?> getListIndex(Context context, MemberFunction function) throws CodeError {
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

	private Value<?> concatList(Context context, MemberFunction function) throws CodeError {
		synchronized (LIST_LOCK) {
			ListValue list1 = function.getParameterValueOfType(context, ListValue.class, 0);
			ListValue list2 = function.getParameterValueOfType(context, ListValue.class, 1);
			list1.value.addAll(list2.value);
			return list1;
		}
	}
}
