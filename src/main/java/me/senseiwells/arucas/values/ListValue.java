package me.senseiwells.arucas.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.ArucasValueList;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.StringUtils;
import me.senseiwells.arucas.values.functions.MemberFunction;

import java.util.List;

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
	public int getHashCode(Context context) throws CodeError {
		int hashCode = 0;
		
		for (Value<?> value : this.value) {
			// Get each value in this list
			hashCode = 32 * hashCode + value.getHashCode(context);
		}
		
		return hashCode;
	}
	
	@Override
	public String getStringValue(Context context) throws CodeError {
		if (this.value.isEmpty()) {
			return "[]";
		}
		
		StringBuilder sb = new StringBuilder();
		
		// Non thread safe iteration could occur here so we need to allocate memory
		Value<?>[] a = this.value.toArray(Value[]::new);
		
		for (int i = 0, len = a.length; i < len; i++) {
			sb.append(StringUtils.toPlainString(context, a[i]));
			if (i < len - 1) {
				sb.append(", ");
			}
		}
		
		return "[" + sb.toString().trim() + "]";
	}
	
	@Override
	public boolean isEquals(Context context, Value<?> other) throws CodeError {
		if (!(other instanceof ListValue that)) return false;
		
		// Do a reference check
		if (this == other) return true;
		
		// Non thread safe iteration could occur here so we need to allocate memory
		Value<?>[] a = this.value.toArray(Value[]::new);
		Value<?>[] b = that.value.toArray(Value[]::new);
		if (a.length != b.length) return false;
		
		for (int i = 0, len = a.length; i < len; i++) {
			if (!a[i].isEquals(context, b[i])) {
				return false;
			}
		}
		
		return true;
	}
	
	public static class ArucasListClass extends ArucasClassExtension {
		public ArucasListClass() {
			super("List");
		}

		@Override
		public Class<ListValue> getValueClass() {
			return ListValue.class;
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
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
			);
		}

		private synchronized Value<?> getListIndex(Context context, MemberFunction function) throws CodeError {
			ListValue thisValue = function.getParameterValueOfType(context, ListValue.class, 0);
			NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 1);
			int index = numberValue.value.intValue();
			if (index >= thisValue.value.size() || index < 0) {
				throw function.throwInvalidParameterError("Index is out of bounds", context);
			}
			return thisValue.value.get(index);
		}

		private synchronized Value<?> removeListIndex(Context context, MemberFunction function) throws CodeError {
			ListValue thisValue = function.getParameterValueOfType(context, ListValue.class, 0);
			NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 1);
			int index = numberValue.value.intValue();
			if (index >= thisValue.value.size() || index < 0) {
				throw function.throwInvalidParameterError("Index is out of bounds", context);
			}
			return thisValue.value.remove(index);
		}

		private synchronized Value<?> appendList(Context context, MemberFunction function) throws CodeError {
			ListValue thisValue = function.getParameterValueOfType(context, ListValue.class, 0);
			Value<?> value = function.getParameterValue(context, 1);
			thisValue.value.add(value);
			return thisValue;
		}

		private synchronized Value<?> insertList(Context context, MemberFunction function) throws CodeError {
			ListValue thisValue = function.getParameterValueOfType(context, ListValue.class, 0);
			Value<?> value = function.getParameterValue(context, 1);
			int index = function.getParameterValueOfType(context, NumberValue.class, 2).value.intValue();
			int len = thisValue.value.size();
			if (index > len || index < 0) {
				throw new RuntimeError("Index is out of bounds", function.syntaxPosition, context);
			}
			thisValue.value.add(index, value);
			return thisValue;
		}

		private synchronized Value<?> concatList(Context context, MemberFunction function) throws CodeError {
			ListValue thisValue = function.getParameterValueOfType(context, ListValue.class, 0);
			ListValue list2 = function.getParameterValueOfType(context, ListValue.class, 1);
			thisValue.value.addAll(list2.value);
			return thisValue;
		}

		private synchronized BooleanValue listContains(Context context, MemberFunction function) throws CodeError {
			ListValue thisValue = function.getParameterValueOfType(context, ListValue.class, 0);
			Value<?> value = function.getParameterValue(context, 1);
			return BooleanValue.of(thisValue.value.contains(value));
		}

		private synchronized BooleanValue containsAll(Context context, MemberFunction function) throws CodeError {
			ListValue thisValue = function.getParameterValueOfType(context, ListValue.class, 0);
			ListValue otherList = function.getParameterValueOfType(context, ListValue.class, 1);
			return BooleanValue.of(thisValue.value.containsAll(otherList.value));
		}

		private synchronized BooleanValue isEmpty(Context context, MemberFunction function) throws CodeError {
			ListValue thisValue = function.getParameterValueOfType(context, ListValue.class, 0);
			return BooleanValue.of(thisValue.value.isEmpty());
		}
	}
}
