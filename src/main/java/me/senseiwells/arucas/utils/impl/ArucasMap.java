package me.senseiwells.arucas.utils.impl;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.StringUtils;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.ValueIdentifier;

import java.util.*;

/**
 * This class cannot contain null values.
 */
public class ArucasMap implements ValueIdentifier {
	// This field is used to prevent deadlocks.
	static final Object DEADLOCK_HANDLE = new Object();
	
	static final int HASH_BITS = 0x7fffffff;
	// Using an INITIAL SIZE of 2048 makes this map implementation just 10 times slower than the
	// Java implementation. Because Java dynamically expands their maps their implementation will
	// become faster regardless.
	static final int INITIAL_SIZE = 2048;
	
	private final Node[] table;
	private final int mask;
	private int size;
	
	public ArucasMap() {
		this.mask = ArucasMap.INITIAL_SIZE;
		this.table = new Node[this.mask];
		this.size = 0;
	}
	
	public ArucasMap(Context context, ArucasMap map) throws CodeError {
		this();
		this.putAll(context, map);
	}
	
	/**
	 * Adds an element to the map.
	 */
	public synchronized Value<?> put(Context context, Value<?> key, Value<?> value) throws CodeError {
		return this.putNode(context, key, value, false);
	}
	
	/**
	 * Adds an element to the map if it doesn't exist in the map.
	 */
	public synchronized Value<?> putIfAbsent(Context context, Value<?> key, Value<?> value) throws CodeError {
		return this.putNode(context, key, value, true);
	}
	
	/**
	 * Gets the specified key inside this map.
	 */
	public synchronized Value<?> get(Context context, Value<?> key) throws CodeError {
		Node node = this.getNode(context,key);
		return node == null ? null : node.value;
	}
	
	/**
	 * Removes an element from the map.
	 */
	public synchronized Value<?> remove(Context context, Value<?> key) throws CodeError {
		Node node = this.removeNode(context, key);
		return node == null ? null : node.value;
	}
	
	/**
	 * Returns if the specified key exists inside this map.
	 */
	public synchronized boolean containsKey(Context context, Value<?> key) throws CodeError {
		return this.getNode(context, key) != null;
	}
	
	/**
	 * Add all elements from one map to this map.
	 */
	public void putAll(Context context, ArucasMap map) throws CodeError {
		synchronized (DEADLOCK_HANDLE) {
			synchronized (this) {
				synchronized (map) {
					for (int i = 0, len = map.table.length; i < len; i++) {
						Node node = map.table[i];
						
						while (node != null) {
							this.put(context, node.key, node.value);
							node = node.next;
						}
					}
				}
			}
		}
	}
	
	/**
	 * Clear this map.
	 */
	public synchronized void clear() {
		this.size = 0;
		for (int i = 0, len = this.table.length; i < len; i++) {
			this.table[i] = null;
		}
	}
	
	/**
	 * Returns if this map is empty
	 */
	public boolean isEmpty() {
		return this.size == 0;
	}
	
	/**
	 * Returns the size of this map
	 */
	public int size() {
		return this.size;
	}
	
	/**
	 * Returns all keys inside this map
	 */
	public synchronized Set<Value<?>> keySet(Context context) throws CodeError {
		final Value<?>[] array = new Value<?>[this.size];
		int j = 0;
		
		for (int i = 0, len = this.table.length; i < len; i++) {
			Node node = this.table[i];
			
			while (node != null) {
				array[j++] = node.key;
				node = node.next;
			}
		}
		
		return new KeySet(array.length == j ? array : Arrays.copyOf(array, j));
	}
	
	/**
	 * Returns a collection of all values inside this map
	 */
	public synchronized Collection<? extends Value<?>> values(Context context) throws CodeError {
		final Value<?>[] array = new Value<?>[this.size];
		int j = 0;
		
		for (int i = 0, len = this.table.length; i < len; i++) {
			Node node = this.table[i];
			
			while (node != null) {
				array[j++] = node.value;
				node = node.next;
			}
		}
		
		return List.of(array.length == j ? array : Arrays.copyOf(array, j));
	}
	
	/**
	 * Returns a set of all entries inside this map
	 */
	public synchronized Set<Node> entrySet(Context context) throws CodeError {
		final Node[] array = new Node[this.size];
		
		for (int i = 0, j = 0, len = this.table.length; i < len; i++) {
			Node node = this.table[i];
			
			while (node != null) {
				array[j++] = node;
				node = node.next;
			}
		}
		
		return new EntrySet(array);
	}
	
	private synchronized Value<?> putNode(Context context, Value<?> key, Value<?> value, boolean putIfAbsent) throws CodeError {
		int hash = hash(key.getHashCode(context)) & (this.mask - 1);
		Node curr = this.table[hash];
		
		if (curr == null) {
			this.size ++;
			this.table[hash] = new Node(key, value);
			return null;
		}
		
		Node last = curr;
		while (curr != null) {
			if (key.isEquals(context, curr.key)) {
				return putIfAbsent ? curr.value : curr.setValue(value);
			}
			
			last = curr;
			curr = curr.next;
		}
		
		this.size ++;
		last.next = new Node(key, value);
		return null;
	}
	
	private synchronized Node removeNode(Context context, Value<?> key) throws CodeError {
		int hash = this.hash(key.getHashCode(context)) & (this.mask - 1);
		Node curr = this.table[hash];
		Node last = null;
		
		while (curr != null) {
			if (key.isEquals(context, curr.key)) {
				if (last == null) {
					// Make sure we remove this element from the map
					this.table[hash] = curr.next;
				}
				else {
					last.next = curr.next;
				}
				
				this.size --;
				return curr;
			}
			
			last = curr;
			curr = curr.next;
		}
		
		return null;
	}
	
	private synchronized Node getNode(Context context, Value<?> key) throws CodeError {
		int hash = hash(key.getHashCode(context)) & (this.mask - 1);
		Node curr = this.table[hash];
		
		while (curr != null) {
			if (key.isEquals(context, curr.key)) {
				return curr;
			}
			
			curr = curr.next;
		}
		
		return null;
	}

	private synchronized int hash(int h) {
		return (h ^ (h >>> 16)) & ArucasMap.HASH_BITS;
	}
	
	@Override
	public int getHashCode(Context context) throws CodeError {
		int h = 0;
		
		for (int i = 0, len = this.table.length; i < len; i++) {
			Node node = this.table[i];
			
			while (node != null) {
				h += node.getHashCode(context);
				node = node.next;
			}
		}
		
		return h;
	}
	
	@Override
	public synchronized String getAsString(Context context) throws CodeError {
		StringBuilder sb = new StringBuilder();
		sb.append('{');
		
		final int size = this.size;
		for (int i = 0, l = 0; l < size; i++) {
			Node node = this.table[i];
			
			while (node != null) {
				sb.append(StringUtils.toPlainString(context, node.key)).append(": ")
				  .append(StringUtils.toPlainString(context, node.value));
				
				if (++l >= this.size) {
					break;
				}
				
				sb.append(", ");
				node = node.next;
			}
		}
		
		return sb.append('}').toString();
	}
	
	@Override
	public boolean isEquals(Context context, Value<?> other) throws CodeError {
		if (!(other.value instanceof ArucasMap that)) {
			return false;
		}
		
		if (this == that) {
			return true;
		}
		
		// If we always synchronize on DEADLOCK_HANDLE when locking on parameters
		// we will never let two threads lock on each other without waiting for
		// the other thread to release their lock on DEADLOCK_HANDLE.
		// This prevents any deadlocks from happening.
		synchronized (DEADLOCK_HANDLE) {
			synchronized (this) {
				synchronized (that) {
					if (this.size != that.size) {
						return false;
					}
					
					for (int i = 0, len = that.table.length; i < len; i++) {
						Node thatNode = that.table[i];
						
						while (thatNode != null) {
							// Check if keys are equal
							Node thisNode = this.getNode(context, thatNode.key);
							
							// Check if the nodes are equal
							if (thisNode == null || !thisNode.isEquals(context, thatNode)) {
								return false;
							}
							
							thatNode = thatNode.next;
						}
					}
				}
			}
		}
		
		return true;
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
	
	public static class Node {
		private final Value<?> key;
		private Value<?> value;
		private Node next;
		
		Node(Value<?> key, Value<?> value) {
			this.key = key;
			this.value = value;
		}
		
		public int getHashCode(Context context) throws CodeError {
			return this.key.getHashCode(context) ^ this.value.getHashCode(context);
		}
		
		public boolean isEquals(Context context, Node node) throws CodeError {
			return this == node
				|| (this.key.isEquals(context, node.key)
				&& this.value.isEquals(context, node.value));
		}
		
		public String getAsString(Context context) throws CodeError {
			return this.key.getAsString(context) + " == " + this.value.getAsString(context);
		}
		
		public Value<?> getKey() {
			return this.key;
		}
		
		public Value<?> getValue() {
			return this.value;
		}
		
		public Value<?> setValue(Value<?> value) {
			Value<?> old = this.value;
			this.value = value;
			return old;
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
	
	static class KeySet extends MapSet<Value<?>> {
		KeySet(Value<?>[] array) {
			super(array);
		}
	}
	
	public static class EntrySet extends MapSet<Node> {
		EntrySet(Node[] array) {
			super(array);
		}
	}
	
	private static class MapSet<T> implements Set<T> {
		final T[] array;
		final int length;
		
		protected MapSet(T[] array) {
			this.array = array;
			this.length = array.length;
		}
		
		@Override
		public int size() {
			return this.length;
		}
		
		@Override
		public boolean isEmpty() {
			return this.length == 0;
		}
		
		@Override
		public Object[] toArray() {
			return this.array;
		}


		@SuppressWarnings("unchecked")
		@Override
		public <E> E[] toArray(E[] a) {
			return (E[]) Arrays.copyOf(this.array, this.size(), a.getClass());
		}
		
		@Override
		public Iterator<T> iterator() {
			return new Iterator<>() {
				private int index;

				@Override
				public synchronized boolean hasNext() {
					return this.index < MapSet.this.length;
				}

				@Override
				public synchronized T next() {
					if (this.index >= MapSet.this.length) {
						return null;
					}

					return MapSet.this.array[this.index++];
				}
			};
		}
		
		@Override
		public boolean add(T value) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public boolean remove(Object o) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}
		
		@Deprecated
		@Override
		public boolean addAll(Collection<? extends T> c) {
			throw new UnsupportedOperationException();
		}
		
		@Deprecated
		@Override
		public boolean retainAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}
		
		@Deprecated
		@Override
		public boolean removeAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}
		
		@Deprecated
		@Override
		public boolean contains(Object o) {
			throw new UnsupportedOperationException();
		}
		
		@Deprecated
		@Override
		public boolean containsAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}
	}
}
