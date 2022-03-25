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
				new MemberFunction("clear", this::clear)
			);
		}

		/**
		 * Description: this allows you to get a value from in the set <p>
		 * Parameters - Value: the value you want to get from the set <p>
		 * Returns - Value/Null: the value you wanted to get, null if it wasn't in the set
		 */
		private synchronized Value<?> get(Context context, MemberFunction function) throws CodeError {
			SetValue thisValue = function.getThis(context, SetValue.class);
			Value<?> value = function.getParameterValue(context, 1);
			return thisValue.value.get(context, value);
		}

		/**
		 * Description: this allows you to remove a value from the set <p>
		 * Parameters - Value: the value you want to remove from the set <p>
		 * Returns - Boolean: whether the value was removed from the set
		 */
		private synchronized Value<?> remove(Context context, MemberFunction function) throws CodeError {
			SetValue thisValue = function.getThis(context, SetValue.class);
			Value<?> value = function.getParameterValue(context, 1);
			return BooleanValue.of(thisValue.value.remove(context, value));
		}

		/**
		 * Description: this allows you to add a value to the set <p>
		 * Parameters - Value: the value you want to add to the set <p>
		 * Returns - Boolean: whether the value was successfully added to the set
		 */
		private synchronized Value<?> add(Context context, MemberFunction function) throws CodeError {
			SetValue thisValue = function.getThis(context, SetValue.class);
			Value<?> value = function.getParameterValue(context, 1);
			return BooleanValue.of(thisValue.value.add(context, value));
		}

		/**
		 * Description: this allows you to add all the values in a collection into the set <p>
		 * Parameters - List/Set: the collection of values you want to add <p>
		 * Returns - Set: the modified set <p>
		 * Throws: <code>"'...' is not a colletion"</code>
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
		 * Description: this allows you to check whether a value is in the set <p>
		 * Parameters - Value: the value that you want to check in the set <p>
		 * Returns - Boolean: whether the value is in the set
		 */
		private synchronized BooleanValue contains(Context context, MemberFunction function) throws CodeError {
			SetValue thisValue = function.getThis(context, SetValue.class);
			Value<?> value = function.getParameterValue(context, 1);
			return BooleanValue.of(thisValue.value.contains(context, value));
		}

		/**
		 * Description: this allows you to check whether a collection of values are all in the set <p>
		 * Parameters - List/Set: the collection of values you want to check in the set <p>
		 * Returns - Boolean: whether all the values are in the set <p>
		 * Throws: <code>"'...' is not a collection"</code>
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
		 * Description: this allows you to check whether the set has no values <p>
		 * Returns - Boolean: whether the set is empty
		 */
		private synchronized BooleanValue isEmpty(Context context, MemberFunction function) throws CodeError {
			SetValue thisValue = function.getThis(context, SetValue.class);
			return BooleanValue.of(thisValue.value.isEmpty());
		}

		/**
		 * Description: this removes all values from inside the set
		 */
		private synchronized Value<?> clear(Context context, MemberFunction function) throws CodeError {
			SetValue thisValue = function.getThis(context, SetValue.class);
			thisValue.value.clear();
			return NullValue.NULL;
		}
	}
}
