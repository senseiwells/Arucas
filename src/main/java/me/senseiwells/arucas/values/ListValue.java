package me.senseiwells.arucas.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Arguments;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.utils.impl.IArucasCollection;
import me.senseiwells.arucas.values.functions.MemberFunction;

import static me.senseiwells.arucas.utils.ValueTypes.*;

public class ListValue extends GenericValue<ArucasList> {
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
	public boolean isEquals(Context context, Value other) throws CodeError {
		return this.value.isEquals(context, other);
	}

	@Override
	public String getTypeName() {
		return LIST;
	}

	@ClassDoc(
		name = LIST,
		desc = "This class cannot be constructed since it has a literal, `[]`"
	)
	public static class ArucasListClass extends ArucasClassExtension {
		public ArucasListClass() {
			super(LIST);
		}

		@Override
		public Class<ListValue> getValueClass() {
			return ListValue.class;
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				MemberFunction.of("getIndex", 1, this::getListIndex, "Use '<List>.get(index)'"),
				MemberFunction.of("get", 1, this::getListIndex),
				MemberFunction.of("removeIndex", 1, this::removeListIndex, "Use '<List>.remove(index)'"),
				MemberFunction.of("remove", 1, this::removeListIndex),
				MemberFunction.of("append", 1, this::appendList),
				MemberFunction.of("insert", 2, this::insertList),
				MemberFunction.of("addAll", 1, this::addAll),
				MemberFunction.of("concat",1, this::concatList, "Use '<List>.addAll(collection)'"),
				MemberFunction.of("contains", 1, this::listContains),
				MemberFunction.of("containsAll", 1, this::containsAll),
				MemberFunction.of("isEmpty", this::isEmpty),
				MemberFunction.of("clear", this::clear),
				MemberFunction.of("indexOf", 1, this::indexOf),
				MemberFunction.of("toString", this::toString)
			);
		}

		@FunctionDoc(
			name = "get",
			desc = "This allows you to get the value at a specific index",
			params = {NUMBER, "index", "the index of the value you want to get"},
			returns = {ANY, "the value at the index"},
			throwMsgs = "Index is out of bounds",
			example = "`['object', 81, 96, 'case'].get(1);`"
		)
		private Value getListIndex(Arguments arguments) throws CodeError {
			ListValue thisValue = arguments.getNext(ListValue.class);
			int index = arguments.getNextGeneric(NumberValue.class).intValue();
			if (index >= thisValue.value.size() || index < 0) {
				throw arguments.getError("Index is out of bounds");
			}
			return thisValue.value.get(index);
		}

		@FunctionDoc(
			name = "remove",
			desc = "This allows you to remove the value at a specific index",
			params = {NUMBER, "index", "the index of the value you want to remove"},
			returns = {ANY, "the value that was removed"},
			throwMsgs = "Index is out of bounds",
			example = "`['object', 81, 96, 'case'].remove(1);`"
		)
		private Value removeListIndex(Arguments arguments) throws CodeError {
			ListValue thisValue = arguments.getNext(ListValue.class);
			int index = arguments.getNextGeneric(NumberValue.class).intValue();
			if (index >= thisValue.value.size() || index < 0) {
				throw arguments.getError("Index is out of bounds");
			}
			return thisValue.value.remove(index);
		}

		@FunctionDoc(
			name = "append",
			desc = "This allows you to append a value to the end of the list",
			params = {ANY, "value", "the value you want to append"},
			returns = {LIST, "the list"},
			example = "`['object', 81, 96, 'case'].append('foo');`"
		)
		private Value appendList(Arguments arguments) throws CodeError {
			ListValue thisValue = arguments.getNext(ListValue.class);
			Value value = arguments.getNext();
			thisValue.value.add(value);
			return thisValue;
		}

		@FunctionDoc(
			name = "insert",
			desc = "This allows you to insert a value at a specific index",
			params = {
				ANY, "value", "the value you want to insert",
				NUMBER, "index", "the index you want to insert the value at"
			},
			returns = {LIST, "the list"},
			throwMsgs = "Index is out of bounds",
			example = "`['object', 81, 96, 'case'].insert('foo', 1);`"
		)
		private Value insertList(Arguments arguments) throws CodeError {
			ListValue thisValue = arguments.getNext(ListValue.class);
			Value value = arguments.getNext();
			int index = arguments.getNextGeneric(NumberValue.class).intValue();
			int len = thisValue.value.size();
			if (index > len || index < 0) {
				throw arguments.getError("Index is out of bounds");
			}
			thisValue.value.add(index, value);
			return thisValue;
		}

		@FunctionDoc(
			name = "addAll",
			desc = "This allows you to add all the values in a collection to the list",
			params = {COLLECTION, "collection", "the collection you want to add"},
			returns = {LIST, "the list"},
			throwMsgs = "... is not a collection",
			example = "`['object', 81, 96, 'case'].addAll(['foo', 'bar']);`"
		)
		private Value addAll(Arguments arguments) throws CodeError {
			ListValue thisValue = arguments.getNext(ListValue.class);
			Value value = arguments.getNext();
			if (value.getValue() instanceof IArucasCollection collection) {
				thisValue.value.addAll(collection.asCollection());
				return thisValue;
			}
			throw arguments.getError("'%s' is not a collection", value);
		}

		@FunctionDoc(
			deprecated = "You should use `<List>.addAll(collection)` instead",
			name = "concat",
			desc = "This allows you to concatenate two lists",
			params = {LIST, "otherList", "the list you want to concatenate with"},
			returns = {LIST, "the concatenated list"},
			example = "`['object', 81, 96, 'case'].concat(['foo', 'bar']);`"
		)
		private Value concatList(Arguments arguments) throws CodeError {
			ListValue thisValue = arguments.getNext(ListValue.class);
			ListValue otherList = arguments.getNext(ListValue.class);
			thisValue.value.addAll(otherList.value);
			return thisValue;
		}

		@FunctionDoc(
			name = "contains",
			desc = "This allows you to check if the list contains a value",
			params = {ANY, "value", "the value you want to check for"},
			returns = {BOOLEAN, "true if the list contains the value, false otherwise"},
			example = "`['object', 81, 96, 'case'].contains('foo');`"
		)
		private BooleanValue listContains(Arguments arguments) throws CodeError {
			ListValue thisValue = arguments.getNext(ListValue.class);
			Value value = arguments.getNext();
			return BooleanValue.of(thisValue.value.contains(arguments.getContext(), value));
		}

		@FunctionDoc(
			name = "containsAll",
			desc = "This allows you to check if the list contains all the values in a collection",
			params = {COLLECTION, "collection", "the collection you want to check for"},
			returns = {BOOLEAN, "true if the list contains all the values in the collection, false otherwise"},
			throwMsgs = "... is not a collection",
			example = "`['object', 81, 96, 'case'].containsAll(['foo', 'bar']);`"
		)
		private BooleanValue containsAll(Arguments arguments) throws CodeError {
			ListValue thisValue = arguments.getNext(ListValue.class);
			Value value = arguments.getNext();
			if (value.getValue() instanceof IArucasCollection collection) {
				return BooleanValue.of(thisValue.value.containsAll(arguments.getContext(), collection.asCollection()));
			}
			throw arguments.getError("'%s' is not a collection", value);
		}

		@FunctionDoc(
			name = "isEmpty",
			desc = "This allows you to check if the list is empty",
			returns = {BOOLEAN, "true if the list is empty, false otherwise"},
			example = "`['object', 81, 96, 'case'].isEmpty();`"
		)
		private BooleanValue isEmpty(Arguments arguments) throws CodeError {
			ListValue thisValue = arguments.getNext(ListValue.class);
			return BooleanValue.of(thisValue.value.isEmpty());
		}

		@FunctionDoc(
			name = "clear",
			desc = "This allows you to clear all the values the list",
			example = "`['object', 81, 96, 'case'].clear();`"
		)
		private Value clear(Arguments arguments) throws CodeError {
			ListValue thisValue = arguments.getNext(ListValue.class);
			thisValue.value.clear();
			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "indexOf",
			desc = "This allows you to get the index of a value in the list",
			params = {ANY, "value", "the value you want to check for"},
			returns = {NUMBER, "the index of the value, -1 if the value is not in the list"},
			example = "`['object', 81, 96, 'case'].indexOf('case');`"
		)
		private Value indexOf(Arguments arguments) throws CodeError {
			ListValue thisValue = arguments.getNext(ListValue.class);
			Value value = arguments.getNext();
			return NumberValue.of(thisValue.value.indexOf(arguments.getContext(), value));
		}

		@FunctionDoc(
			name = "toString",
			desc = "This converts the list to a string and evaluating any collections inside it",
			returns = {STRING, "the string representation of the set"},
			example = "`['object', 81, 96, 'case'].toString();`"
		)
		private Value toString(Arguments arguments) throws CodeError {
			ListValue thisValue = arguments.getNext(ListValue.class);
			return StringValue.of(thisValue.value.getAsStringUnsafe(arguments.getContext(), arguments.getPosition()));
		}
	}
}
