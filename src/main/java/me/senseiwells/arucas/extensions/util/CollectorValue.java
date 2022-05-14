package me.senseiwells.arucas.extensions.util;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
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

import static me.senseiwells.arucas.utils.ValueTypes.*;

public class CollectorValue extends GenericValue<ArucasList> {
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
	public boolean isEquals(Context context, Value other) throws CodeError {
		return this.value.isEquals(context, other);
	}

	@Override
	public String getTypeName() {
		return COLLECTOR;
	}

	@Override
	public GenericValue<ArucasList> copy(Context context) throws CodeError {
		return this;
	}
	
	@ClassDoc(
		name = COLLECTOR,
		desc = "This class is similar to Java streams, allowing for easy modifications of collections.",
		importPath = "util.Collection"
	)
	public static class ArucasCollectorClass extends ArucasClassExtension {
		public ArucasCollectorClass() {
			super(COLLECTOR);
		}

		@Override
		public Class<? extends Value> getValueClass() {
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

		@FunctionDoc(
			isStatic = true,
			name = "of",
			desc = "This creates a collector for a collection",
			params = {COLLECTION, "collection", "the collection of values you want to evaluate"},
			returns = {COLLECTOR, "the collector"},
			throwMsgs = "... is not a collection",
			example = "Collector.of([1, 2, 3]);"
		)
		private Value of(Context context, BuiltInFunction function) throws CodeError {
			Value value = function.getParameterValue(context, 0);
			if (value.getValue() instanceof IArucasCollection collection) {
				ArucasList list = new ArucasList();
				list.addAll(collection.asCollection());
				return new CollectorValue(list);
			}
			throw new RuntimeError("'%s' is not a collection".formatted(value.getAsString(context)), function.syntaxPosition, context);
		}

		@FunctionDoc(
			isVarArgs = true,
			isStatic = true,
			name = "of",
			desc = "This creates a collector for a collection",
			params = {ANY, "value...", "the values you want to evaluate"},
			returns = {COLLECTOR, "the collector"},
			example = "Collector.of(1, 2, '3');"
		)
		private Value ofArbitrary(Context context, BuiltInFunction function) throws CodeError {
			ListValue arguments = function.getFirstParameter(context, ListValue.class);
			return new CollectorValue(arguments.value);
		}

		@FunctionDoc(
			isStatic = true,
			name = "isCollection",
			desc = "This checks if the value is a collection",
			params = {ANY, "value", "the value you want to check"},
			returns = {BOOLEAN, "true if the value is a collection"},
			example = "Collector.isCollection([1, 2, 3]);"
		)
		private Value isCollection(Context context, BuiltInFunction function) {
			Value value = function.getParameterValue(context, 0);
			return BooleanValue.of(value.getValue() instanceof IArucasCollection);
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

		@FunctionDoc(
			name = "filter",
			desc = "This filters the collection using the predicate",
			params = {FUNCTION, "predicate", "a function that takes a value and returns Boolean, true if it should be kept, false if not"},
			returns = {COLLECTOR, "the filtered collection"},
			throwMsgs = "Predicate must return Boolean",
			example = """
			Collector.of([1, 2, 3]).filter(fun(value) {
			    return value < 3;
			});
			"""
		)
		private Value filter(Context context, MemberFunction function) throws CodeError {
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

		@FunctionDoc(
			name = "anyMatch",
			desc = "This checks if any of the values in the collection match the predicate",
			params = {FUNCTION, "predicate", "a function that takes a value and returns Boolean, true if it matches, false if not"},
			returns = {BOOLEAN, "true if any of the values match the predicate, false if not"},
			throwMsgs = "Predicate must return Boolean",
			example = """
			Collector.of([1, 2, 3]).anyMatch(fun(value) {
			    return value < 3;
			});
			"""
		)
		private Value anyMatch(Context context, MemberFunction function) throws CodeError {
			CollectorValue thisValue = function.getThis(context, CollectorValue.class);
			FunctionValue predicate = function.getParameterValueOfType(context, FunctionValue.class, 1);

			for (int i = 0; i < thisValue.value.size(); i++) {
				if (this.callPredicate(context, predicate, thisValue.value.get(i))) {
					return BooleanValue.TRUE;
				}
			}

			return BooleanValue.FALSE;
		}

		@FunctionDoc(
			name = "allMatch",
			desc = "This checks if all the values in the collection match the predicate",
			params = {FUNCTION, "predicate", "a function that takes a value and returns Boolean, true if it matches, false if not"},
			returns = {BOOLEAN, "true if all the values match the predicate, false if not"},
			throwMsgs = "Predicate must return Boolean",
			example = """
			Collector.of([1, 2, 3]).anyMatch(fun(value) {
			    return value < 5;
			});
			"""
		)
		private Value allMatch(Context context, MemberFunction function) throws CodeError {
			CollectorValue thisValue = function.getThis(context, CollectorValue.class);
			FunctionValue predicate = function.getParameterValueOfType(context, FunctionValue.class, 1);

			for (int i = 0; i < thisValue.value.size(); i++) {
				if (!this.callPredicate(context, predicate, thisValue.value.get(i))) {
					return BooleanValue.FALSE;
				}
			}

			return BooleanValue.TRUE;
		}

		@FunctionDoc(
			name = "noneMatch",
			desc = "This checks if none of the values in the collection match the predicate",
			params = {FUNCTION, "predicate", "a function that takes a value and returns Boolean, true if it matches, false if not"},
			returns = {BOOLEAN, "true if none of the values match the predicate, false if not"},
			throwMsgs = "Predicate must return Boolean",
			example = """
			Collector.of([1, 2, 3]).noneMatch(fun(value) {
			    return value < 5;
			});
			"""
		)
		private Value noneMatch(Context context, MemberFunction function) throws CodeError {
			CollectorValue thisValue = function.getThis(context, CollectorValue.class);
			FunctionValue predicate = function.getParameterValueOfType(context, FunctionValue.class, 1);

			for (int i = 0; i < thisValue.value.size(); i++) {
				if (this.callPredicate(context, predicate, thisValue.value.get(i))) {
					return BooleanValue.FALSE;
				}
			}

			return BooleanValue.TRUE;
		}

		@FunctionDoc(
			name = "map",
			desc = "This maps the values in Collector to a new value",
			params = {FUNCTION, "mapper", "a function that takes a value and returns a new value"},
			returns = {COLLECTOR, "a new Collector with the mapped values"},
			example = """
			Collector.of([1, 2, 3]).map(fun(value) {
				return value * 2;
			});
			"""
		)
		private Value map(Context context, MemberFunction function) throws CodeError {
			CollectorValue thisValue = function.getThis(context, CollectorValue.class);
			FunctionValue mapper = function.getParameterValueOfType(context, FunctionValue.class, 1);

			for (int i = 0; i < thisValue.value.size(); i++) {
				Value value = thisValue.value.get(i);
				ArrayList<Value> parameters = new ArrayList<>();
				parameters.add(value);
				thisValue.value.set(i, mapper.call(context, parameters));
			}

			return thisValue;
		}

		@FunctionDoc(
			name = "forEach",
			desc = "This iterates over all the values in the Collector and calls the passed in function with each value",
			params = {FUNCTION, "function", "a function that takes a value and returns nothing"},
			returns = {COLLECTOR, "the Collector"},
			example = """
			Collector.of([1, 2, 3]).forEach(fun(value) {
				print(value);
			});
			"""
		)
		private Value forEach(Context context, MemberFunction function) throws CodeError {
			CollectorValue thisValue = function.getThis(context, CollectorValue.class);
			FunctionValue forEachFunction = function.getParameterValueOfType(context, FunctionValue.class, 1);

			for (int i = 0; i < thisValue.value.size(); i++) {
				Value value = thisValue.value.get(i);
				ArrayList<Value> parameters = new ArrayList<>();
				parameters.add(value);
				forEachFunction.call(context, parameters);
			}

			return thisValue;
		}

		@FunctionDoc(
			name = "flatten",
			desc = {
				"If there are values in the collector that are collections they will be expanded, ",
				"collections inside collections are not flattened, you would have to call this method again"
			},
			returns = {COLLECTOR, "a new Collector with the expanded values"},
			example = "Collector.of([1, 2, [3, 4]]).flatten();"
		)
		private Value flatten(Context context, MemberFunction function) throws CodeError {
			CollectorValue thisValue = function.getThis(context, CollectorValue.class);

			for (int i = 0; i < thisValue.value.size(); i++) {
				Value value = thisValue.value.get(i);
				if (value.getValue() instanceof IArucasCollection collection) {
					thisValue.value.addAll(i + 1, collection.asCollection());
					thisValue.value.remove(i);
					i += collection.size() - 1;
				}
			}

			return thisValue;
		}

		@FunctionDoc(
			name = "toSet",
			desc = "This puts all the values in the collector into a set and returns it",
			returns = {SET, "a set with all the values in the collector"},
			example = "Collector.of([1, 2, 3]).toSet();"
		)
		private Value toSet(Context context, MemberFunction function) throws CodeError {
			CollectorValue thisValue = function.getThis(context, CollectorValue.class);
			ArucasSet set = new ArucasSet();
			set.addAll(context, thisValue.value);
			return new SetValue(set);
		}

		@FunctionDoc(
			name = "toList",
			desc = "This puts all the values in the collector into a list and returns it",
			returns = {LIST, "a list with all the values in the collector"},
			example = "Collector.of([1, 2, 3]).toList();"
		)
		private Value toList(Context context, MemberFunction function) throws CodeError {
			CollectorValue thisValue = function.getThis(context, CollectorValue.class);
			ArucasList list = new ArucasList(thisValue.value);
			return new ListValue(list);
		}

		private boolean callPredicate(Context context, FunctionValue predicate, Value value) throws CodeError {
			ArrayList<Value> parameters = new ArrayList<>();
			parameters.add(value);
			Value returnValue = predicate.call(context, parameters);
			if (!(returnValue instanceof BooleanValue booleanValue)) {
				throw new RuntimeError("Predicate must return Boolean", predicate.syntaxPosition, context);
			}
			return booleanValue.value;
		}
	}
}
