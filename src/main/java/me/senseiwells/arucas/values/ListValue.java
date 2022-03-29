package me.senseiwells.arucas.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.utils.impl.IArucasCollection;
import me.senseiwells.arucas.values.functions.MemberFunction;

import java.util.List;

public class ListValue extends Value<ArucasList> {
	public ListValue(ArucasList value) {
		super(value);
	}

	@Override
	public ListValue copy(Context context) {
		return new ListValue(this.value);
	}

	@Override
	public ListValue newCopy(Context context) {
		return new ListValue(new ArucasList(this.value));
	}

	@Override
	public int getHashCode(Context context) throws CodeError {
		return this.value.getHashCode(context);
	}

	@Override
	public String getAsString(Context context) throws CodeError {
		return this.value.getAsString(context);
	}

	@Override
	public boolean isEquals(Context context, Value<?> other) throws CodeError {
		return this.value.isEquals(context, other);
	}

	@Override
	public String getTypeName() {
		return "List";
	}

	/**
	 * List class for Arucas. <br>
	 * Fully Documented.
	 * @author senseiwells
	 */
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
				new MemberFunction("addAll", "collection", this::addAll),
				new MemberFunction("concat", "otherList", this::concatList),
				new MemberFunction("contains", "value", this::listContains),
				new MemberFunction("containsAll", "otherList", this::containsAll),
				new MemberFunction("isEmpty", this::isEmpty),
				new MemberFunction("clear", this::clear),
				new MemberFunction("indexOf", "value", this::indexOf),
				new MemberFunction("toString", this::toString)
			);
		}

		/**
		 * Name: <code>&lt;List>.get(index)</code> <br>
		 * Description: this allows you to get the value at a specific index <br>
		 * Parameter - index: the index of the value you want to get <br>
		 * Returns - Value: the value at the index <br>
		 * Throws - Error: <code>"Index is out of bounds"</code> if the index is out of bounds <br>
		 * Example: <code>["object", 81, 96, "case"].get(1);</code>
		 */
		private synchronized Value<?> getListIndex(Context context, MemberFunction function) throws CodeError {
			ListValue thisValue = function.getThis(context, ListValue.class);
			NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 1);
			int index = numberValue.value.intValue();
			if (index >= thisValue.value.size() || index < 0) {
				throw function.throwInvalidParameterError("Index is out of bounds", context);
			}
			return thisValue.value.get(index);
		}

		/**
		 * Name: <code>&lt;List>.remove(index)</code> <br>
		 * Description: this allows you to remove the value at a specific index <br>
		 * Parameter - index: the index of the value you want to remove <br>
		 * Returns - Value: the value that was removed <br>
		 * Throws - Error: <code>"Index is out of bounds"</code> if the index is out of bounds <br>
		 * Example: <code>["object", 81, 96, "case"].remove(1);</code>
		 */
		private synchronized Value<?> removeListIndex(Context context, MemberFunction function) throws CodeError {
			ListValue thisValue = function.getThis(context, ListValue.class);
			NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 1);
			int index = numberValue.value.intValue();
			if (index >= thisValue.value.size() || index < 0) {
				throw function.throwInvalidParameterError("Index is out of bounds", context);
			}
			return thisValue.value.remove(index);
		}

		/**
		 * Name: <code>&lt;List>.append(value)</code> <br>
		 * Description: this allows you to append a value to the end of the list <br>
		 * Parameter - value: the value you want to append <br>
		 * Returns - List: the list <br>
		 * Example: <code>["object", 81, 96, "case"].append("foo");</code>
		 */
		private synchronized Value<?> appendList(Context context, MemberFunction function) throws CodeError {
			ListValue thisValue = function.getThis(context, ListValue.class);
			Value<?> value = function.getParameterValue(context, 1);
			thisValue.value.add(value);
			return thisValue;
		}

		/**
		 * Name: <code>&lt;List>.insert(value, index)</code> <br>
		 * Description: this allows you to insert a value at a specific index <br>
		 * Parameter - value, index: the value you want to insert, the index you want to insert the value at <br>
		 * Returns - List: the list <br>
		 * Throws - Error: <code>"Index is out of bounds"</code> if the index is out of bounds <br>
		 * Example: <code>["object", 81, 96, "case"].insert("foo", 1);</code>
		 */
		private synchronized Value<?> insertList(Context context, MemberFunction function) throws CodeError {
			ListValue thisValue = function.getThis(context, ListValue.class);
			Value<?> value = function.getParameterValue(context, 1);
			int index = function.getParameterValueOfType(context, NumberValue.class, 2).value.intValue();
			int len = thisValue.value.size();
			if (index > len || index < 0) {
				throw new RuntimeError("Index is out of bounds", function.syntaxPosition, context);
			}
			thisValue.value.add(index, value);
			return thisValue;
		}

		/**
		 * Name: <code>&lt;List>.addAll(collection)</code> <br>
		 * Description: this allows you to add all the values in a collection to the list <br>
		 * Parameter - Collection: the collection you want to add <br>
		 * Returns - List: the list <br>
		 * Throws - Error: <code>"... is not a collection"</code> if the value is not a collection <br>
		 * Example: <code>["object", 81, 96, "case"].addAll(["foo", "bar"]);</code>
		 */
		private synchronized Value<?> addAll(Context context, MemberFunction function) throws CodeError {
			ListValue thisValue = function.getThis(context, ListValue.class);
			Value<?> value = function.getParameterValue(context, 1);
			if (value.value instanceof IArucasCollection collection) {
				thisValue.value.addAll(collection.asCollection());
				return thisValue;
			}
			throw new RuntimeError("'%s' is not a collection".formatted(value.getAsString(context)), function.syntaxPosition, context);
		}

		/**
		 * Name: <code>&lt;List>.concat(otherList)</code> <br>
		 * Description: this allows you to concatenate two lists <br>
		 * Parameter - List: the list you want to concatenate with <br>
		 * Returns - List: the concatenated list <br>
		 * Example: <code>["object", 81, 96, "case"].concat(["foo", "bar"]);</code>
		 */
		private synchronized Value<?> concatList(Context context, MemberFunction function) throws CodeError {
			ListValue thisValue = function.getThis(context, ListValue.class);
			ListValue list2 = function.getParameterValueOfType(context, ListValue.class, 1);
			thisValue.value.addAll(list2.value);
			return thisValue;
		}

		/**
		 * Name: <code>&lt;List>.contains(value)</code> <br>
		 * Description: this allows you to check if the list contains a value <br>
		 * Parameter - Value: the value you want to check for <br>
		 * Returns - Boolean: true if the list contains the value, false otherwise <br>
		 * Example: <code>["object", 81, 96, "case"].contains("foo");</code>
		 */
		private synchronized BooleanValue listContains(Context context, MemberFunction function) throws CodeError {
			ListValue thisValue = function.getThis(context, ListValue.class);
			Value<?> value = function.getParameterValue(context, 1);
			return BooleanValue.of(thisValue.value.contains(context, value));
		}

		/**
		 * Name: <code>&lt;List>.containsAll(collection)</code> <br>
		 * Description: this allows you to check if the list contains all the values in a collection <br>
		 * Parameter - Collection: the collection you want to check for <br>
		 * Returns - Boolean: true if the list contains all the values in the collection, false otherwise <br>
		 * Throws - Error: <code>"... is not a collection"</code> if the value is not a collection <br>
		 * Example: <code>["object", 81, 96, "case"].containsAll(["foo", "bar"]);</code>
		 */
		private synchronized BooleanValue containsAll(Context context, MemberFunction function) throws CodeError {
			ListValue thisValue = function.getThis(context, ListValue.class);
			Value<?> value = function.getParameterValue(context, 1);
			if (value.value instanceof IArucasCollection collection) {
				return BooleanValue.of(thisValue.value.containsAll(context, collection.asCollection()));
			}
			throw new RuntimeError("'%s' is not a collection".formatted(value.getAsString(context)), function.syntaxPosition, context);
		}

		/**
		 * Name: <code>&lt;List>.isEmpty()</code> <br>
		 * Description: this allows you to check if the list is empty <br>
		 * Returns - Boolean: true if the list is empty, false otherwise <br>
		 * Example: <code>["object", 81, 96, "case"].isEmpty();</code>
		 */
		private synchronized BooleanValue isEmpty(Context context, MemberFunction function) throws CodeError {
			ListValue thisValue = function.getThis(context, ListValue.class);
			return BooleanValue.of(thisValue.value.isEmpty());
		}

		/**
		 * Name: <code>&lt;List>.clear()</code> <br>
		 * Description: this allows you to clear all the values the list <br>
		 * Example: <code>["object", 81, 96, "case"].clear();</code>
		 */
		private synchronized Value<?> clear(Context context, MemberFunction function) throws CodeError {
			ListValue thisValue = function.getThis(context, ListValue.class);
			thisValue.value.clear();
			return NullValue.NULL;
		}

		/**
		 * Name: <code>&lt;List>.indexOf(value)</code> <br>
		 * Description: this allows you to get the index of a value in the list <br>
		 * Parameter - Value: the value you want to check for <br>
		 * Returns - Number: the index of the value, -1 if the value is not in the list <br>
		 * Example: <code>["object", 81, 96, "case"].indexOf("case");</code>
		 */
		private synchronized Value<?> indexOf(Context context, MemberFunction function) throws CodeError {
			ListValue thisValue = function.getThis(context, ListValue.class);
			Value<?> value = function.getParameterValue(context, 1);
			return NumberValue.of(thisValue.value.indexOf(context, value));
		}

		/**
		 * Name: <code>&lt;List>.toString(value)</code> <br>
		 * Description: This converts the set to a string and evaluating any collections inside it <br>
		 * Returns - String: the string representation of the set <br>
		 * Example: <code>["object", 81, 96, "case"].toString();</code>
		 */
		private synchronized Value<?> toString(Context context, MemberFunction function) throws CodeError {
			ListValue thisValue = function.getThis(context, ListValue.class);
			return StringValue.of(thisValue.value.getAsStringUnsafe(context, function.syntaxPosition));
		}
	}
}
