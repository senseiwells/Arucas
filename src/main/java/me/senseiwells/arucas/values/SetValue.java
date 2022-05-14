package me.senseiwells.arucas.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasSet;
import me.senseiwells.arucas.utils.impl.IArucasCollection;
import me.senseiwells.arucas.values.functions.BuiltInFunction;
import me.senseiwells.arucas.values.functions.MemberFunction;

import static me.senseiwells.arucas.utils.ValueTypes.*;

public class SetValue extends Value<ArucasSet> {

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
	public boolean isEquals(Context context, Value<?> other) throws CodeError {
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
				new BuiltInFunction("unordered", this::unordered),
				new BuiltInFunction.Arbitrary("of", this::of)
			);
		}

		@FunctionDoc(
			isStatic = true,
			name = "unordered",
			desc = "This creates an unordered set",
			returns = {SET, "the unordered set"},
			example = "Set.unordered();"
		)
		private Value<?> unordered(Context context, BuiltInFunction function) {
			return new SetValue(new ArucasSet());
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
		private Value<?> of(Context context, BuiltInFunction function) throws CodeError {
			ListValue arguments = function.getFirstParameter(context, ListValue.class);
			ArucasSet set = new ArucasSet();
			set.addAll(context, arguments.value);
			return new SetValue(set);
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				new MemberFunction("get", "object", this::get),
				new MemberFunction("remove", "index", this::remove),
				new MemberFunction("add", "value", this::add),
				new MemberFunction("addAll", "otherList", this::addAll),
				new MemberFunction("contains", "value", this::contains),
				new MemberFunction("containsAll", "otherList", this::containsAll),
				new MemberFunction("isEmpty", this::isEmpty),
				new MemberFunction("clear", this::clear),
				new MemberFunction("toString", this::toString)
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
		private Value<?> get(Context context, MemberFunction function) throws CodeError {
			SetValue thisValue = function.getThis(context, SetValue.class);
			Value<?> value = function.getParameterValue(context, 1);
			return thisValue.value.get(context, value);
		}

		@FunctionDoc(
			name = "remove",
			desc = "This allows you to remove a value from the set",
			params = {ANY, "value", "the value you want to remove from the set"},
			returns = {BOOLEAN, "whether the value was removed from the set"},
			example = "Set.of('object').remove('object');"
		)
		private Value<?> remove(Context context, MemberFunction function) throws CodeError {
			SetValue thisValue = function.getThis(context, SetValue.class);
			Value<?> value = function.getParameterValue(context, 1);
			return BooleanValue.of(thisValue.value.remove(context, value));
		}

		@FunctionDoc(
			name = "add",
			desc = "This allows you to add a value to the set",
			params = {ANY, "value", "the value you want to add to the set"},
			returns = {BOOLEAN, "whether the value was successfully added to the set"},
			example = "Set.of().add('object');"
		)
		private Value<?> add(Context context, MemberFunction function) throws CodeError {
			SetValue thisValue = function.getThis(context, SetValue.class);
			Value<?> value = function.getParameterValue(context, 1);
			return BooleanValue.of(thisValue.value.add(context, value));
		}

		@FunctionDoc(
			name = "addAll",
			desc = "This allows you to add all the values in a collection into the set",
			params = {COLLECTION, "collection", "the collection of values you want to add"},
			returns = {SET, "the modified set"},
			throwMsgs = "... is not a collection",
			example = "Set.of().addAll(Set.of('object', 81, 96, 'case'));"
		)
		private Value<?> addAll(Context context, MemberFunction function) throws CodeError {
			SetValue thisValue = function.getThis(context, SetValue.class);
			Value<?> value = function.getParameterValue(context, 1);
			if (value.value instanceof IArucasCollection collection) {
				thisValue.value.addAll(context, collection.asCollection());
				return thisValue;
			}
			throw new RuntimeError("'%s' is not a collection".formatted(value.getAsString(context)), function.syntaxPosition, context);
		}

		@FunctionDoc(
			name = "contains",
			desc = "This allows you to check whether a value is in the set",
			params = {ANY, "value", "the value that you want to check in the set"},
			returns = {BOOLEAN, "whether the value is in the set"},
			example = "Set.of('object').contains('object');"
		)
		private BooleanValue contains(Context context, MemberFunction function) throws CodeError {
			SetValue thisValue = function.getThis(context, SetValue.class);
			Value<?> value = function.getParameterValue(context, 1);
			return BooleanValue.of(thisValue.value.contains(context, value));
		}

		@FunctionDoc(
			name = "containsAll",
			desc = "This allows you to check whether a collection of values are all in the set",
			params = {COLLECTION, "collection", "the collection of values you want to check in the set"},
			returns = {BOOLEAN, "whether all the values are in the set"},
			throwMsgs = "... is not a collection",
			example = "Set.of('object').containsAll(Set.of('object', 81, 96, 'case'));"
		)
		private BooleanValue containsAll(Context context, MemberFunction function) throws CodeError {
			SetValue thisValue = function.getThis(context, SetValue.class);
			Value<?> value = function.getParameterValue(context, 1);
			if (value.value instanceof IArucasCollection collection) {
				return BooleanValue.of(thisValue.value.containsAll(context, collection.asCollection()));
			}
			throw new RuntimeError("'%s' is not a collection".formatted(value.getAsString(context)), function.syntaxPosition, context);
		}

		@FunctionDoc(
			name = "isEmpty",
			desc = "This allows you to check whether the set has no values",
			returns = {BOOLEAN, "whether the set is empty"},
			example = "Set.of().isEmpty();"
		)
		private BooleanValue isEmpty(Context context, MemberFunction function) throws CodeError {
			SetValue thisValue = function.getThis(context, SetValue.class);
			return BooleanValue.of(thisValue.value.isEmpty());
		}

		@FunctionDoc(
			name = "clear",
			desc = "This removes all values from inside the set",
			example = "Set.of('object').clear();"
		)
		private Value<?> clear(Context context, MemberFunction function) throws CodeError {
			SetValue thisValue = function.getThis(context, SetValue.class);
			thisValue.value.clear();
			return NullValue.NULL;
		}

		@FunctionDoc(
			name = "toString",
			desc = "This converts the set to a string and evaluating any collections inside it",
			returns = {STRING, "the string representation of the set"},
			example = "Set.of('object').toString();"
		)
		private Value<?> toString(Context context, MemberFunction function) throws CodeError {
			SetValue thisValue = function.getThis(context, SetValue.class);
			return StringValue.of(thisValue.value.getAsStringUnsafe(context, function.syntaxPosition));
		}
	}
}
