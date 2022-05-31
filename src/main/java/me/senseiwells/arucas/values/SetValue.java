package me.senseiwells.arucas.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Arguments;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasMap;
import me.senseiwells.arucas.utils.impl.ArucasSet;
import me.senseiwells.arucas.utils.impl.IArucasCollection;
import me.senseiwells.arucas.values.functions.BuiltInFunction;
import me.senseiwells.arucas.values.functions.MemberFunction;

import static me.senseiwells.arucas.utils.ValueTypes.*;

public class SetValue extends GenericValue<ArucasSet> {

	public SetValue(ArucasSet value) {
		super(value);
	}

	@Override
	public SetValue copy(Context context) {
		return new SetValue(this.value);
	}

	@Override
	public SetValue newCopy(Context context) throws CodeError {
		return new SetValue(new ArucasSet(context, this.value));
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
		return SET;
	}

	@ClassDoc(
		name = SET,
		desc = {
			"Sets are collections of unique values. Similar to maps, without the values.",
			"An instance of the class can be created by using `Set.of(values...)`"
		}
	)
	public static class ArucasSetClass extends ArucasClassExtension {
		public ArucasSetClass() {
			super(SET);
		}

		@Override
		public Class<SetValue> getValueClass() {
			return SetValue.class;
		}

		@Override
		public ArucasFunctionMap<BuiltInFunction> getDefinedStaticMethods() {
			return ArucasFunctionMap.of(
				BuiltInFunction.of("unordered", this::unordered),
				BuiltInFunction.arbitrary("of", this::of)
			);
		}

		@FunctionDoc(
			isStatic = true,
			name = "unordered",
			desc = "This creates an unordered set",
			returns = {SET, "the unordered set"},
			example = "Set.unordered();"
		)
		private Value unordered(Arguments arguments) {
			return new SetValue(new ArucasSet(new ArucasMap()));
		}

		@FunctionDoc(
			isVarArgs = true,
			isStatic = true,
			name = "of",
			desc = "This allows you to create a set with an arbitrary number of values",
			params = {ANY, "values...", "the values you want to add to the set"},
			returns = {SET, "the set you created"},
			example = "Set.of('object', 81, 96, 'case');"
		)
		private Value of(Arguments arguments) throws CodeError {
			ArucasSet set = new ArucasSet();
			set.addAll(arguments.getContext(), arguments.getAll());
			return new SetValue(set);
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				MemberFunction.of("get", 1, this::get),
				MemberFunction.of("remove", 1, this::remove),
				MemberFunction.of("add", 1, this::add),
				MemberFunction.of("addAll", 1, this::addAll),
				MemberFunction.of("contains", 1, this::contains),
				MemberFunction.of("containsAll", 1, this::containsAll),
				MemberFunction.of("isEmpty", this::isEmpty),
				MemberFunction.of("clear", this::clear),
				MemberFunction.of("toString", this::toString)
			);
		}

		@FunctionDoc(
			name = "get",
			desc = {
				"This allows you to get a value from in the set.",
				"The reason this might be useful is if you want to retrieve something",
				"from the set that will have the same hashcode but be in a different state",
				"as the value you are passing in"
			},
			params = {ANY, "value", "the value you want to get from the set"},
			returns = {ANY, "the value you wanted to get, null if it wasn't in the set"},
			example = "Set.of('object').get('object');"
		)
		private Value get(Arguments arguments) throws CodeError {
			SetValue thisValue = arguments.getNextSet();
			Value value = arguments.getNext();
			return thisValue.value.get(arguments.getContext(), value);
		}

		@FunctionDoc(
			name = "remove",
			desc = "This allows you to remove a value from the set",
			params = {ANY, "value", "the value you want to remove from the set"},
			returns = {BOOLEAN, "whether the value was removed from the set"},
			example = "Set.of('object').remove('object');"
		)
		private Value remove(Arguments arguments) throws CodeError {
			SetValue thisValue = arguments.getNextSet();
			Value value = arguments.getNext();
			return BooleanValue.of(thisValue.value.remove(arguments.getContext(), value));
		}

		@FunctionDoc(
			name = "add",
			desc = "This allows you to add a value to the set",
			params = {ANY, "value", "the value you want to add to the set"},
			returns = {BOOLEAN, "whether the value was successfully added to the set"},
			example = "Set.of().add('object');"
		)
		private Value add(Arguments arguments) throws CodeError {
			SetValue thisValue = arguments.getNextSet();
			Value value = arguments.getNext();
			return BooleanValue.of(thisValue.value.add(arguments.getContext(), value));
		}

		@FunctionDoc(
			name = "addAll",
			desc = "This allows you to add all the values in a collection into the set",
			params = {COLLECTION, "collection", "the collection of values you want to add"},
			returns = {SET, "the modified set"},
			throwMsgs = "... is not a collection",
			example = "Set.of().addAll(Set.of('object', 81, 96, 'case'));"
		)
		private Value addAll(Arguments arguments) throws CodeError {
			SetValue thisValue = arguments.getNextSet();
			Value value = arguments.getNext();
			if (value.getValue() instanceof IArucasCollection collection) {
				thisValue.value.addAll(arguments.getContext(), collection.asCollection());
				return thisValue;
			}
			throw arguments.getError("'%s' is not a collection", value);
		}

		@FunctionDoc(
			name = "contains",
			desc = "This allows you to check whether a value is in the set",
			params = {ANY, "value", "the value that you want to check in the set"},
			returns = {BOOLEAN, "whether the value is in the set"},
			example = "Set.of('object').contains('object');"
		)
		private BooleanValue contains(Arguments arguments) throws CodeError {
			SetValue thisValue = arguments.getNextSet();
			Value value = arguments.getNext();
			return BooleanValue.of(thisValue.value.contains(arguments.getContext(), value));
		}

		@FunctionDoc(
			name = "containsAll",
			desc = "This allows you to check whether a collection of values are all in the set",
			params = {COLLECTION, "collection", "the collection of values you want to check in the set"},
			returns = {BOOLEAN, "whether all the values are in the set"},
			throwMsgs = "... is not a collection",
			example = "Set.of('object').containsAll(Set.of('object', 81, 96, 'case'));"
		)
		private BooleanValue containsAll(Arguments arguments) throws CodeError {
			SetValue thisValue = arguments.getNextSet();
			Value value = arguments.getNext();
			if (value.getValue() instanceof IArucasCollection collection) {
				return BooleanValue.of(thisValue.value.containsAll(arguments.getContext(), collection.asCollection()));
			}
			throw arguments.getError("'%s' is not a collection", value);
		}

		@FunctionDoc(
			name = "isEmpty",
			desc = "This allows you to check whether the set has no values",
			returns = {BOOLEAN, "whether the set is empty"},
			example = "Set.of().isEmpty();"
		)
		private BooleanValue isEmpty(Arguments arguments) throws CodeError {
			SetValue thisValue = arguments.getNextSet();
			return BooleanValue.of(thisValue.value.isEmpty());
		}

		@FunctionDoc(
			name = "clear",
			desc = "This removes all values from inside the set",
			example = "Set.of('object').clear();"
		)
		private Value clear(Arguments arguments) throws CodeError {
			SetValue thisValue = arguments.getNextSet();
			thisValue.value.clear();
			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "toString",
			desc = "This converts the set to a string and evaluating any collections inside it",
			returns = {STRING, "the string representation of the set"},
			example = "Set.of('object').toString();"
		)
		private Value toString(Arguments arguments) throws CodeError {
			SetValue thisValue = arguments.getNextSet();
			return StringValue.of(thisValue.value.getAsStringUnsafe(arguments.getContext(), arguments.getPosition()));
		}
	}
}
