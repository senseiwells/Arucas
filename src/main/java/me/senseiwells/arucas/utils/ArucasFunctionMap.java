package me.senseiwells.arucas.utils;

import me.senseiwells.arucas.values.functions.FunctionValue;
import me.senseiwells.arucas.values.functions.MemberFunction;

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
	 * Returns true if the function did not exist
	 */
	public boolean add(T value) {
		// If the value is a member value it still has the `this`. We need to get the true argument count
		int parameters = value.getParameterCount();
		
		// Get or calculate the map that this function belongs to
		Map<Integer, T> map = this.map.computeIfAbsent(value.getName(), (name) -> new HashMap<>());
		
		if (map.containsKey(parameters)) {
			return false;
		}
		
		map.put(parameters, value);
		return true;
	}
	
	/**
	 * Returns true if this map is empty
	 */
	public boolean isEmpty() {
		return this.map.isEmpty();
	}

	/**
	 * Returns true if this map contains the function
	 */
	public boolean has(String name) {
		return this.map.containsKey(name);
	}
	
	/**
	 * Returns true if this map contains the function with the specified amount of parameters
	 */
	public boolean has(String name, int parameters) {
		Map<Integer, T> map = this.map.get(name);
		return map != null && map.containsKey(parameters);
	}
	
	/**
	 * Returns the function value only if no overloads exist
	 */
	public T get(String name) {
		Map<Integer, T> map = this.map.get(name);
		return map != null && map.size() == 1 ? map.values().stream().findFirst().orElse(null):null;
	}
	
	/**
	 * Returns the function value with the specified amount of parameters
	 */
	public T get(String name, int parameters) {
		Map<Integer, T> map = this.map.get(name);
		return map != null ? map.get(parameters):null;
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
}
