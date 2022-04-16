package me.senseiwells.arucas.extensions.util;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.utils.impl.ArucasSet;
import me.senseiwells.arucas.utils.impl.IArucasCollection;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.BuiltInFunction;
import me.senseiwells.arucas.values.functions.FunctionValue;
import me.senseiwells.arucas.values.functions.MemberFunction;

import java.util.ArrayList;

public class CollectorValue extends Value<ArucasList> {
	public CollectorValue(ArucasList value) {
		super(value);
	}

	@Override
	public String getAsString(Context context) throws CodeError {
		return "Collector{value=" + this.value.getAsString(context) + "}";
	}

	@Override
	public int getHashCode(Context context) throws CodeError {
		return this.value.getHashCode(context);
	}

	@Override
	public boolean isEquals(Context context, Value<?> other) throws CodeError {
		return this.value.isEquals(context, other);
	}

	@Override
	public String getTypeName() {
		return "Collector";
	}

	@Override
	public Value<ArucasList> copy(Context context) throws CodeError {
		return this;
	}

	/**
	 * Collector class for Arucas. <br>
	 * Import the class with <code>import Collector from util.Collection;</code> <br>
	 * This class is similar to Java streams, allowing for easy modifications of collections. <br>
	 * Fully Documented.
	 * @author senseiwells
	 */
	public static class ArucasCollectorClass extends ArucasClassExtension {
		public ArucasCollectorClass() {
			super("Collector");
		}

		@Override
		public Class<? extends BaseValue> getValueClass() {
			return CollectorValue.class;
		}

		@Override
		public ArucasFunctionMap<BuiltInFunction> getDefinedStaticMethods() {
			return ArucasFunctionMap.of(
				new BuiltInFunction("of", "collection", this::of),
				new BuiltInFunction.Arbitrary("of", this::ofArbitrary),
				new BuiltInFunction("isCollection", "value", this::isCollection)
			);
		}

		/**
		 * Name: <code>Collector.of(collection)</code> <br>
		 * Description: This creates a collector for a collection <br>
		 * Parameter - Collection: the collection of values you want to evaluate <br>
		 * Returns - Collector: the collector <br>
		 * Throws - Error: <code>"'...' is not a collection"</code> if the parameter isn't a collection <br>
		 * Example: <code>Collector.of([1, 2, 3]);</code>
		 */
		private Value<?> of(Context context, BuiltInFunction function) throws CodeError {
			Value<?> value = function.getParameterValue(context, 0);
			if (value.value instanceof IArucasCollection collection) {
				ArucasList list = new ArucasList();
				list.addAll(collection.asCollection());
				return new CollectorValue(list);
			}
			throw new RuntimeError("'%s' is not a collection".formatted(value.getAsString(context)), function.syntaxPosition, context);
		}

		/**
		 * Name: <code>Collector.of(value...)</code> <br>
		 * Description: This creates a collector for a collection <br>
		 * Parameter - Value: the values you want to evaluate <br>
		 * Returns - Collector: the collector <br>
		 * Example: <code>Collector.of(1, 2, 3);</code>
		 */
		private Value<?> ofArbitrary(Context context, BuiltInFunction function) throws CodeError {
			ListValue arguments = function.getFirstParameter(context, ListValue.class);
			return new CollectorValue(arguments.value);
		}

		/**
		 * Name: <code>Collector.isCollection(value)</code> <br>
		 * Description: This checks if the value is a collection <br>
		 * Parameter - Value: the value you want to check <br>
		 * Returns - Boolean: <code>true</code> if the value is a collection <br>
		 * Example: <code>Collector.isCollection([]);</code>
		 */
		private Value<?> isCollection(Context context, BuiltInFunction function) {
			Value<?> value = function.getParameterValue(context, 0);
			return BooleanValue.of(value.value instanceof IArucasCollection);
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				new MemberFunction("filter", "predicate", this::filter),
				new MemberFunction("anyMatch", "predicate", this::anyMatch),
				new MemberFunction("allMatch", "predicate", this::allMatch),
				new MemberFunction("noneMatch", "predicate", this::noneMatch),
				new MemberFunction("map", "mapper", this::map),
				new MemberFunction("forEach", "function", this::forEach),
				new MemberFunction("flatten", this::flatten),
				new MemberFunction("toSet", this::toSet),
				new MemberFunction("toList", this::toList)
			);
		}

		/**
		 * Name: <code>&lt;Collector>.filter(predicate)</code> <br>
		 * Description: This filters the collection using the predicate <br>
		 * Parameter - Function: a function that takes a value and returns Boolean, true if it should be kept, false if not <br>
		 * Returns - Collector: the filtered collection <br>
		 * Throws - Error: <code>"Predicate must return Boolean"</code> if the predicate doesn't return a Boolean <br>
		 * Example: <code>Collector.of([1, 2, 3]).filter(fun(value) { return value < 3; });</code>
		 */
		private Value<?> filter(Context context, MemberFunction function) throws CodeError {
			CollectorValue thisValue = function.getThis(context, CollectorValue.class);
			FunctionValue predicate = function.getParameterValueOfType(context, FunctionValue.class, 1);

			for (int i = 0; i < thisValue.value.size(); i++) {
				if (!this.callPredicate(context, predicate, thisValue.value.get(i))) {
					thisValue.value.remove(i);
					i--;
				}
			}

			return thisValue;
		}

		/**
		 * Name: <code>&lt;Collector>.anyMatch(predicate)</code> <br>
		 * Description: This checks if any of the values in the collection match the predicate <br>
		 * Parameter - Function: a function that takes a value and returns Boolean, true if it matches, false if not <br>
		 * Returns - Boolean: true if any of the values match the predicate, false if not <br>
		 * Throws - Error: <code>"Predicate must return Boolean"</code> if the predicate doesn't return a Boolean <br>
		 * Example: <code>Collector.of([1, 2, 3]).anyMatch(fun(value) { return value < 3; });</code>
		 */
		private Value<?> anyMatch(Context context, MemberFunction function) throws CodeError {
			CollectorValue thisValue = function.getThis(context, CollectorValue.class);
			FunctionValue predicate = function.getParameterValueOfType(context, FunctionValue.class, 1);

			for (int i = 0; i < thisValue.value.size(); i++) {
				if (this.callPredicate(context, predicate, thisValue.value.get(i))) {
					return BooleanValue.TRUE;
				}
			}

			return BooleanValue.FALSE;
		}

		/**
		 * Name: <code>&lt;Collector>.allMatch(predicate)</code> <br>
		 * Description: This checks if all the values in the collection match the predicate <br>
		 * Parameter - Function: a function that takes a value and returns Boolean, true if it matches, false if not <br>
		 * Returns - Boolean: true if all the values match the predicate, false if not <br>
		 * Throws - Error: <code>"Predicate must return Boolean"</code> if the predicate doesn't return a Boolean <br>
		 * Example: <code>Collector.of([1, 2, 3]).allMatch(fun(value) { return value < 5; });</code>
		 */
		private Value<?> allMatch(Context context, MemberFunction function) throws CodeError {
			CollectorValue thisValue = function.getThis(context, CollectorValue.class);
			FunctionValue predicate = function.getParameterValueOfType(context, FunctionValue.class, 1);

			for (int i = 0; i < thisValue.value.size(); i++) {
				if (!this.callPredicate(context, predicate, thisValue.value.get(i))) {
					return BooleanValue.FALSE;
				}
			}

			return BooleanValue.TRUE;
		}

		/**
		 * Name: <code>&lt;Collector>.noneMatch(predicate)</code> <br>
		 * Description: This checks if none of the values in the collection match the predicate <br>
		 * Parameter - Function: a function that takes a value and returns Boolean, true if it matches, false if not <br>
		 * Returns - Boolean: true if none of the values match the predicate, false if not <br>
		 * Throws - Error: <code>"Predicate must return Boolean"</code> if the predicate doesn't return a Boolean <br>
		 * Example: <code>Collector.of([1, 2, 3]).noneMatch(fun(value) { return value < 5; });</code>
		 */
		private Value<?> noneMatch(Context context, MemberFunction function) throws CodeError {
			CollectorValue thisValue = function.getThis(context, CollectorValue.class);
			FunctionValue predicate = function.getParameterValueOfType(context, FunctionValue.class, 1);

			for (int i = 0; i < thisValue.value.size(); i++) {
				if (this.callPredicate(context, predicate, thisValue.value.get(i))) {
					return BooleanValue.FALSE;
				}
			}

			return BooleanValue.TRUE;
		}

		/**
		 * Name: <code>&lt;Collector>.map(mapper)</code> <br>
		 * Description: This maps the values in Collector to a new value <br>
		 * Parameter - Function: a function that takes a value and returns a new value <br>
		 * Returns - Collector: a new Collector with the mapped values <br>
		 * Example: <code>Collector.of([1, 2, 3]).map(fun(value) { return value * 2; });</code>
		 */
		private Value<?> map(Context context, MemberFunction function) throws CodeError {
			CollectorValue thisValue = function.getThis(context, CollectorValue.class);
			FunctionValue mapper = function.getParameterValueOfType(context, FunctionValue.class, 1);

			for (int i = 0; i < thisValue.value.size(); i++) {
				Value<?> value = thisValue.value.get(i);
				ArrayList<Value<?>> parameters = new ArrayList<>();
				parameters.add(value);
				thisValue.value.set(i, mapper.call(context, parameters));
			}

			return thisValue;
		}

		/**
		 * Name: <code>&lt;Collector>.forEach(function)</code> <br>
		 * Description: This iterates over all the values in the Collector and calls the passed in function with each value <br>
		 * Parameter - Function: a function that takes a value and returns nothing <br>
		 * Returns - Collector: the Collector <br>
		 * Example: <code>Collector.of([1, 2, 3]).forEach(fun(value) { print(value); });</code>
		 */
		private Value<?> forEach(Context context, MemberFunction function) throws CodeError {
			CollectorValue thisValue = function.getThis(context, CollectorValue.class);
			FunctionValue forEachFunction = function.getParameterValueOfType(context, FunctionValue.class, 1);

			for (int i = 0; i < thisValue.value.size(); i++) {
				Value<?> value = thisValue.value.get(i);
				ArrayList<Value<?>> parameters = new ArrayList<>();
				parameters.add(value);
				forEachFunction.call(context, parameters);
			}

			return thisValue;
		}

		/**
		 * Name: <code>&lt;Collector>.flatten()</code> <br>
		 * Description: If there are values in the collector that are collections they will be expanded, collections inside collections
		 * are not flattened, you would have to call this method again <br>
		 * Returns - Collector: a new Collector with the expanded values <br>
		 * Example: <code>Collector.of([1, 2, [3, 4]]).flatten();</code>
		 */
		private Value<?> flatten(Context context, MemberFunction function) throws CodeError {
			CollectorValue thisValue = function.getThis(context, CollectorValue.class);

			for (int i = 0; i < thisValue.value.size(); i++) {
				Value<?> value = thisValue.value.get(i);
				if (value.value instanceof IArucasCollection collection) {
					thisValue.value.addAll(i + 1, collection.asCollection());
					thisValue.value.remove(i);
					i += collection.size() - 1;
				}
			}

			return thisValue;
		}

		/**
		 * Name: <code>&lt;Collector>.toSet()</code> <br>
		 * Description: This puts all the values in the collector into a set and returns it <br>
		 * Returns - Set: a set with all the values in the collector <br>
		 * Example: <code>Collector.of([1, 2, 3]).toSet();</code>
		 */
		private Value<?> toSet(Context context, MemberFunction function) throws CodeError {
			CollectorValue thisValue = function.getThis(context, CollectorValue.class);
			ArucasSet set = new ArucasSet();
			set.addAll(context, thisValue.value);
			return new SetValue(set);
		}

		/**
		 * Name: <code>&lt;Collector>.toList()</code> <br>
		 * Description: This puts all the values in the collector into a list and returns it <br>
		 * Returns - List: a list with all the values in the collector <br>
		 * Example: <code>Collector.of([1, 2, 3]).toList();</code>
		 */
		private Value<?> toList(Context context, MemberFunction function) throws CodeError {
			CollectorValue thisValue = function.getThis(context, CollectorValue.class);
			ArucasList list = new ArucasList(thisValue.value);
			return new ListValue(list);
		}

		private boolean callPredicate(Context context, FunctionValue predicate, Value<?> value) throws CodeError {
			ArrayList<Value<?>> parameters = new ArrayList<>();
			parameters.add(value);
			Value<?> returnValue = predicate.call(context, parameters);
			if (!(returnValue instanceof BooleanValue booleanValue)) {
				throw new RuntimeError("Predicate must return Boolean", predicate.syntaxPosition, context);
			}
			return booleanValue.value;
		}
	}
}
