package me.senseiwells.arucas.utils;

import me.senseiwells.arucas.values.Value;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Custom implementation of a value hash map.
 * This map is thread safe and will not throw any ConcurrentModificationExceptions.
 *
 * Impl: This class has been tested and does not throw any exceptions.
 */
public class ArucasValueMap extends ConcurrentHashMap<Value<?>, Value<?>> {
	public ArucasValueMap() {
	
	}
	
	public ArucasValueMap(ArucasValueMap map) {
		super(map);
	}
}
