package me.senseiwells.arucas.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasSet;
import me.senseiwells.arucas.utils.impl.IArucasCollection;
import me.senseiwells.arucas.values.functions.BuiltInFunction;
import me.senseiwells.arucas.values.functions.MemberFunction;

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
		return "Set";
	}

	/**
	 * Set class for Arucas. <br>
	 * An instance of the class can be created by using <code>Set.of(values...)</code> <br>
	 * Fully Documented.
	 * @author senseiwells
	 */
	public static class ArucasSetClass extends ArucasClassExtension {
		public ArucasSetClass() {
			super("Set");
		}

		@Override
		public Class<SetValue> getValueClass() {
			return SetValue.class;
		}

		@Override
		public ArucasFunctionMap<BuiltInFunction> getDefinedStaticMethods() {
			return ArucasFunctionMap.of(
				new BuiltInFunction.Arbitrary("of", this::of)
			);
		}

		/**
		 * Name: <code>Set.of(values...)</code> <br>
		 * Description: this allows you to create a set with an arbitrary number of values <br>
		 * Parameters - Value...: the values you want to add to the set <br>
		 * Returns - Set: the set you created <br>
		 * Example: <code>Set.of("object", 81, 96, "case");</code>
		 */
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

		/**
		 * Name: <code>&lt;Set>.get(object)</code> <br>
		 * Description: This allows you to get a value from in the set <br>
		 * Parameter - Value: the value you want to get from the set <br>
		 * Returns - Value/Null: the value you wanted to get, null if it wasn't in the set <br>
		 * Example: <code>Set.of("object").get("object");</code>
		 */
		private synchronized Value<?> get(Context context, MemberFunction function) throws CodeError {
			SetValue thisValue = function.getThis(context, SetValue.class);
			Value<?> value = function.getParameterValue(context, 1);
			return thisValue.value.get(context, value);
		}

		/**
		 * Name: <code>&lt;Set>.remove(object)</code> <br>
		 * Description: This allows you to remove a value from the set <br>
		 * Parameter - Value: the value you want to remove from the set <br>
		 * Returns - Boolean: whether the value was removed from the set <br>
		 * Example: <code>Set.of("object").remove("object");</code>
		 */
		private synchronized Value<?> remove(Context context, MemberFunction function) throws CodeError {
			SetValue thisValue = function.getThis(context, SetValue.class);
			Value<?> value = function.getParameterValue(context, 1);
			return BooleanValue.of(thisValue.value.remove(context, value));
		}

		/**
		 * Name: <code>&lt;Set>.add(value)</code> <br>
		 * Description: This allows you to add a value to the set <br>
		 * Parameter - Value: the value you want to add to the set <br>
		 * Returns - Boolean: whether the value was successfully added to the set <br>
		 * Example: <code>Set.of().add("object");</code>
		 */
		private synchronized Value<?> add(Context context, MemberFunction function) throws CodeError {
			SetValue thisValue = function.getThis(context, SetValue.class);
			Value<?> value = function.getParameterValue(context, 1);
			return BooleanValue.of(thisValue.value.add(context, value));
		}

		/**
		 * Name: <code>&lt;Set>.addAll(otherCollection)</code> <br>
		 * Description: This allows you to add all the values in a collection into the set <br>
		 * Parameter - Collection: the collection of values you want to add <br>
		 * Returns - Set: the modified set <br>
		 * Throws - Error: <code>"'...' is not a collection"</code> if the parameter isn't a collection <br>
		 * Example: <code>Set.of().addAll(Set.of("object", 81, 96, "case"));</code>
		 */
		private synchronized Value<?> addAll(Context context, MemberFunction function) throws CodeError {
			SetValue thisValue = function.getThis(context, SetValue.class);
			Value<?> value = function.getParameterValue(context, 1);
			if (value.value instanceof IArucasCollection collection) {
				thisValue.value.addAll(context, collection.asCollection());
				return thisValue;
			}
			throw new RuntimeError("'%s' is not a collection".formatted(value.getAsString(context)), function.syntaxPosition, context);
		}

		/**
		 * Name: <code>&lt;Set>.contains(value)</code> <br>
		 * Description: This allows you to check whether a value is in the set <br>
		 * Parameter - Value: the value that you want to check in the set <br>
		 * Returns - Boolean: whether the value is in the set <br>
		 * Example: <code>Set.of("object").contains("object");</code>
		 */
		private synchronized BooleanValue contains(Context context, MemberFunction function) throws CodeError {
			SetValue thisValue = function.getThis(context, SetValue.class);
			Value<?> value = function.getParameterValue(context, 1);
			return BooleanValue.of(thisValue.value.contains(context, value));
		}

		/**
		 * Name: <code>&lt;Set>.containsAll(otherCollection)</code> <br>
		 * Description: This allows you to check whether a collection of values are all in the set <br>
		 * Parameter - Collection: the collection of values you want to check in the set <br>
		 * Returns - Boolean: whether all the values are in the set <br>
		 * Throws - Error: <code>"'...' is not a collection"</code> if the parameter isn't a collection <br>
		 * Example: <code>Set.of("object").containsAll(Set.of("object", 81, 96, "case"));</code>
		 */
		private synchronized BooleanValue containsAll(Context context, MemberFunction function) throws CodeError {
			SetValue thisValue = function.getThis(context, SetValue.class);
			Value<?> value = function.getParameterValue(context, 1);
			if (value.value instanceof IArucasCollection collection) {
				return BooleanValue.of(thisValue.value.containsAll(context, collection.asCollection()));
			}
			throw new RuntimeError("'%s' is not a collection".formatted(value.getAsString(context)), function.syntaxPosition, context);
		}

		/**
		 * Name: <code>&lt;Set>.isEmpty()</code> <br>
		 * Description: This allows you to check whether the set has no values <br>
		 * Returns - Boolean: whether the set is empty <br>
		 * Example: <code>Set.of().isEmpty();</code>
		 */
		private synchronized BooleanValue isEmpty(Context context, MemberFunction function) throws CodeError {
			SetValue thisValue = function.getThis(context, SetValue.class);
			return BooleanValue.of(thisValue.value.isEmpty());
		}

		/**
		 * Name: <code>&lt;Set>.clear()</code> <br>
		 * Description: This removes all values from inside the set <br>
		 * Example: <code>Set.of("object").clear();</code>
		 */
		private synchronized Value<?> clear(Context context, MemberFunction function) throws CodeError {
			SetValue thisValue = function.getThis(context, SetValue.class);
			thisValue.value.clear();
			return NullValue.NULL;
		}

		/**
		 * Name: <code>&lt;Set>.toString()</code> <br>
		 * Description: This converts the set to a string and evaluating any collections inside it <br>
		 * Returns - String: the string representation of the set <br>
		 * Example: <code>Set.of("object").toString();</code>
		 */
		private synchronized Value<?> toString(Context context, MemberFunction function) throws CodeError {
			SetValue thisValue = function.getThis(context, SetValue.class);
			return StringValue.of(thisValue.value.getAsStringUnsafe(context, function.syntaxPosition));
		}
	}
}
