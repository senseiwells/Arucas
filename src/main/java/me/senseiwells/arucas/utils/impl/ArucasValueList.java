package me.senseiwells.arucas.utils.impl;

import me.senseiwells.arucas.values.Value;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Custom implementation of a value array list.
 * This list is thread safe and will not throw any ConcurrentModificationExceptions.
 *
 * Impl: This class has been tested and does not throw any exceptions.
 */
@Deprecated
public class ArucasValueList extends CopyOnWriteArrayList<Value<?>> {
	public ArucasValueList() {
	
	}
	
	public ArucasValueList(ArucasValueList list) {
		super(list);
	}
}
