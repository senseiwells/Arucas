package me.senseiwells.arucas.utils;

import me.senseiwells.arucas.values.functions.FunctionValue;

import java.util.*;

/**
 * A function map that provides a <code>O(1)</code> lookup time complexity for member functions.
 *
 * @param <T>
 */
public class ArucasFunctionMap<T extends FunctionValue> implements Iterable<T> {
	private final Map<String, Map<Integer, T>> map;
	
	public ArucasFunctionMap() {
		this.map = new HashMap<>();
	}
	
	/**
	 * Returns true if the function did not exist.
	 */
	public boolean add(T value) {
		// If the value is a member value it still has the `this`. We need to get the true argument count
		int parameters = value.getCount();
		
		// Get or calculate the map that this function belongs to
		Map<Integer, T> map = this.map.computeIfAbsent(value.getName(), (name) -> new HashMap<>());
		
		if (map.containsKey(parameters)) {
			map.put(parameters, value);
			return false;
		}
		
		map.put(parameters, value);
		return true;
	}
	
	/**
	 * Adds all functions in the specified map to this map.
	 */
	public void addAll(ArucasFunctionMap<? extends T> functions) {
		functions.forEach(this::add);
	}

	/**
	 * Adds all functions in the specified map to this map.
	 */
	public void addAll(Collection<? extends T> functions) {
		functions.forEach(this::add);
	}
	
	/**
	 * Returns true if this map is empty.
	 */
	public boolean isEmpty() {
		return this.map.isEmpty();
	}

	/**
	 * Returns true if this map contains the function.
	 */
	public boolean has(String name) {
		return this.map.containsKey(name);
	}
	
	/**
	 * Returns true if this map contains the function with the specified amount of parameters.
	 */
	public boolean has(String name, int parameters) {
		Map<Integer, T> map = this.map.get(name);
		return map != null && map.containsKey(parameters);
	}
	
	/**
	 * Returns the function value only if no overloads exist.
	 */
	public T get(String name) {
		Map<Integer, T> map = this.map.get(name);
		return map != null && map.size() == 1 ? map.values().stream().findFirst().orElse(null) : null;
	}
	
	/**
	 * Returns the function value with the specified amount of parameters.
	 */
	public T get(String name, int parameters) {
		// If parameters are less than equal 2 we return function without overload
		if (parameters <= -2) {
			// <=-2 --> delegate
			return this.get(name);
		}

		Map<Integer, T> map = this.map.get(name);
		if (map == null) {
			return null;
		}
		// This tries to return an arbitrary function if none are present
		T function = map.get(parameters);
		// -1 --> arbitrary parameter function
		return function != null ? function : map.get(-1);
	}
	
	/**
	 * Returns all unique function names inside this map
	 */
	public Set<String> names() {
		return this.map.keySet();
	}
	
	@Override
	public Iterator<T> iterator() {
		// Create a non thread safe iterator of the current map
		return this.map.values().stream().flatMap(i -> i.values().stream()).iterator();
	}
	
	@SafeVarargs
	public static <T extends FunctionValue> ArucasFunctionMap<T> of(T... functions) {
		ArucasFunctionMap<T> map = new ArucasFunctionMap<>();
		for (T value : functions) {
			map.add(value);
		}
		
		return map;
	}
	
	public static <T extends FunctionValue> ArucasFunctionMap<T> of(Collection<? extends T> functions) {
		ArucasFunctionMap<T> map = new ArucasFunctionMap<>();
		for (T value : functions) {
			map.add(value);
		}
		
		return map;
	}
}
