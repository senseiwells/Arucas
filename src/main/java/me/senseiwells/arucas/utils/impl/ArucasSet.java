package me.senseiwells.arucas.utils.impl;


import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.StringUtils;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.ValueIdentifier;

import java.util.Collection;
import java.util.Iterator;

public class ArucasSet implements IArucasCollection, ValueIdentifier {
	// Empty object used as value.
	static final Value<?> EMPTY = NullValue.NULL;

	private final ArucasMap map;
	
	public ArucasSet() {
		this.map = new ArucasOrderedMap();
	}

	public ArucasSet(Context context, ArucasSet set) throws CodeError {
		this.map = new ArucasOrderedMap(context, set.map);
	}
	
	/**
	 * Add an element to the set
	 */
	public synchronized boolean add(Context context, Value<?> value) throws CodeError {
		return this.map.put(context, value, EMPTY) == null;
	}

	/**
	 * Get an element from the set. The reason
	 * we allow this is that a value may be
	 * equal to another value but be in a
	 * different state, if a set contains a
	 * value it may not be the 'exact' value.
	 */
	public synchronized Value<?> get(Context context, Value<?> value) throws CodeError {
		Value<?> returnValue = this.map.getKey(context, value);
		return returnValue == null ? NullValue.NULL : returnValue;
	}
	
	/**
	 * Remove an element from this set
	 */
	public synchronized boolean remove(Context context, Value<?> value) throws CodeError {
		return this.map.remove(context, value) != null;
	}
	
	/**
	 * Returns if this set contains the value
	 */
	public synchronized boolean contains(Context context, Value<?> value) throws CodeError {
		return this.map.containsKey(context, value);
	}

	/**
	 * Returns if this set contains all the values in a collection
	 */
	public synchronized boolean containsAll(Context context, Collection<? extends Value<?>> list) throws CodeError {
		for (Value<?> value : list) {
			if (!this.contains(context, value)) {
				return false;
			}
		}
		return true;
	}

	
	/**
	 * Add all elements in the collection to this set
	 */
	public synchronized void addAll(Context context, Collection<? extends Value<?>> list) throws CodeError {
		for (Value<?> value : list) {
			this.add(context, value);
		}
	}

	/**
	 * Clear this set
	 */
	public synchronized void clear() {
		this.map.clear();
	}
	
	/**
	 * Returns if this set is empty
	 */
	public boolean isEmpty() {
		return this.map.isEmpty();
	}
	
	/**
	 * Returns the size of this set
	 */
	public int size() {
		return this.map.size();
	}

	@Override
	public Collection<? extends Value<?>> asCollection() {
		return this.map.keys();
	}
	
	@Override
	public int getHashCode(Context context) throws CodeError {
		return this.map.getHashCode(context);
	}
	
	@Override
	public synchronized String getAsString(Context context) throws CodeError {
		StringBuilder sb = new StringBuilder();
		sb.append('<');
		
		Iterator<Value<?>> iter = this.map.keys().iterator();
		while (iter.hasNext()) {
			Value<?> value = iter.next();
			sb.append(StringUtils.toPlainString(context, value));

			if (iter.hasNext()) {
				sb.append(", ");
			}
		}
		
		return sb.append('>').toString();
	}
	
	@Override
	public boolean isEquals(Context context, Value<?> other) throws CodeError {
		return this.map.isEquals(context, other);
	}
	
	@Deprecated
	@Override
	public final int hashCode() {
		return super.hashCode();
	}
	
	@Deprecated
	@Override
	public final boolean equals(Object obj) {
		return super.equals(obj);
	}
	
	@Deprecated
	@Override
	public final String toString() {
		return super.toString();
	}
}
