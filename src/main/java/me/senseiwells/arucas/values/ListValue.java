package me.senseiwells.arucas.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.ArucasValueList;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.StringUtils;
import me.senseiwells.arucas.values.functions.MemberFunction;

import java.util.List;
import java.util.Set;

public class ListValue extends Value<ArucasValueList> {

	public ListValue(ArucasValueList value) {
		super(value);
	}

	@Override
	public ListValue copy() {
		return new ListValue(this.value);
	}

	@Override
	public ListValue newCopy() {
		return new ListValue(new ArucasValueList(this.value));
	}

	@Override
	public String getStringValue(Context context) throws CodeError {
		ArucasValueList list = this.value;
		if (list.isEmpty()) return "[]";
		
		StringBuilder sb = new StringBuilder();
		for (Value<?> element : list) {
			sb.append(", ").append(StringUtils.toPlainString(context, element));
		}
		
		/*
		 * Because of thread safety the list might have been reset before this point
		 * and is empty meaning that 'sb' will be empty. If this was the case an
		 * StringIndexOutOfBoundsException would have been thrown.
		 *
		 * To prevent this exception we check if the StringBuilder has any characters
		 * inside it.
		 */
		if (sb.length() > 0) {
			sb.deleteCharAt(0);
		}
		
		return "[%s]".formatted(sb.toString().trim());
	}

	@Override
	protected Set<MemberFunction> getDefinedFunctions() {
		Set<MemberFunction> memberFunctions = super.getDefinedFunctions();
		memberFunctions.addAll(Set.of(
			new MemberFunction("getIndex", "index", this::getListIndex, "Use '<List>.get(index)'"),
			new MemberFunction("get", "index", this::getListIndex),
			new MemberFunction("removeIndex", "index", this::removeListIndex, "Use '<List>.remove(index)'"),
			new MemberFunction("remove", "index", this::removeListIndex),
			new MemberFunction("append", "value", this::appendList),
			new MemberFunction("insert", List.of("value", "index"), this::insertList),
			new MemberFunction("concat", "otherList", this::concatList),
			new MemberFunction("contains", "value", this::listContains),
			new MemberFunction("containsAll", "otherList", this::containsAll),
			new MemberFunction("isEmpty", this::isEmpty)
		));
		return memberFunctions;
	}

	private synchronized Value<?> getListIndex(Context context, MemberFunction function) throws CodeError {
		NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 0);
		int index = numberValue.value.intValue();
		if (index >= this.value.size() || index < 0) {
			throw function.throwInvalidParameterError("Index is out of bounds", context);
		}
		return this.value.get(index);
	}

	private synchronized Value<?> removeListIndex(Context context, MemberFunction function) throws CodeError {
		NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 0);
		int index = numberValue.value.intValue();
		if (index >= this.value.size() || index < 0) {
			throw function.throwInvalidParameterError("Index is out of bounds", context);
		}
		return this.value.remove(index);
	}

	private synchronized Value<?> appendList(Context context, MemberFunction function) {
		Value<?> value = function.getParameterValue(context, 0);
		this.value.add(value);
		return this;
	}

	private synchronized Value<?> insertList(Context context, MemberFunction function) throws CodeError {
		Value<?> value = function.getParameterValue(context, 0);
		int index = function.getParameterValueOfType(context, NumberValue.class, 1).value.intValue();
		int len = this.value.size();
		if (index > len || index < 0) {
			throw new RuntimeError("Index is out of bounds", function.syntaxPosition, context);
		}
		this.value.add(index, value);
		return this;
	}

	private synchronized Value<?> concatList(Context context, MemberFunction function) throws CodeError {
		ListValue list2 = function.getParameterValueOfType(context, ListValue.class, 0);
		this.value.addAll(list2.value);
		return this;
	}

	private synchronized BooleanValue listContains(Context context, MemberFunction function) {
		Value<?> value = function.getParameterValue(context, 0);
		return BooleanValue.of(this.value.contains(value));
	}

	private synchronized BooleanValue containsAll(Context context, MemberFunction function) throws CodeError {
		ListValue otherList = function.getParameterValueOfType(context, ListValue.class, 0);
		return BooleanValue.of(this.value.containsAll(otherList.value));
	}

	private synchronized BooleanValue isEmpty(Context context, MemberFunction function) {
		return BooleanValue.of(this.value.isEmpty());
	}

	public static class ArucasListClass extends ArucasClassExtension {
		public ArucasListClass() {
			super("List");
		}
	}
}
