package me.senseiwells.arucas.utils;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.values.Value;

import java.util.*;

/**
 * This class cannot contain null values
 */
public class ArucasValueMapCustom {
	static final int HASH_BITS = 0x7fffffff;
	static final int INITIAL_SIZE = 64;
	
	private final Node[] table;
	private final int mask;
	private int size;
	
	public ArucasValueMapCustom() {
		this.mask = ArucasValueMapCustom.INITIAL_SIZE;
		this.table = new Node[this.mask];
		this.size = 0;
	}
	
	private synchronized int hash(int h) {
		return (h ^ (h >>> 16)) & ArucasValueMapCustom.HASH_BITS;
	}
	
	/**
	 * Adds an element to the map
	 */
	public synchronized Value<?> put(Context context, Value<?> key, Value<?> value) throws CodeError {
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
				return curr.replace(value);
			}
			
			last = curr;
			curr = curr.next;
		}
		
		this.size ++;
		last.next = new Node(key, value);
		return null;
	}
	
	/**
	 * Removes an element from the map
	 */
	public synchronized Value<?> remove(Context context, Value<?> key) throws CodeError {
		int hash = hash(key.getHashCode(context)) & (this.mask - 1);
		Node curr = this.table[hash];
		Node last = curr;
		
		while (curr != null) {
			if (key.isEquals(context, curr.key)) {
				last.next = curr.next;
				this.size --;
				return curr.value;
			}
			
			last = curr;
			curr = curr.next;
		}
		
		return null;
	}
	
	public synchronized boolean containsKey(Context context, Value<?> key) throws CodeError {
		int hash = hash(key.getHashCode(context)) & (this.mask - 1);
		Node curr = this.table[hash];
		
		while (curr != null) {
			if (key.isEquals(context, curr.key)) {
				return true;
			}
			
			curr = curr.next;
		}
		
		return false;
	}
	
	public synchronized void clear() {
		this.size = 0;
		for (int i = 0, len = this.table.length; i < len; i++) {
			this.table[i] = null;
		}
	}
	
	public synchronized int size() {
		return this.size;
	}
	
	public synchronized Set<Value<?>> keySet() {
		List<Value<?>> list = new ArrayList<>(this.table.length);
		
		for (int i = 0, len = this.table.length; i < len; i++) {
			Node node = this.table[i];
			
			while (node != null) {
				list.add(node.key);
				node = node.next;
			}
		}
		
		return new KeySet(list);
	}
	
	public synchronized String toString(Context context) throws CodeError {
		StringBuilder sb = new StringBuilder();
		sb.append('{');
		
		final int size = this.size;
		for (int i = 0, l = 0; l < size; i++) {
			Node node = this.table[i];
			
			while (node != null) {
				sb.append(node.key.getStringValue(context)).append(": ").append(node.value.getStringValue(context));
				
				if (++l >= this.size) {
					break;
				}
				
				sb.append(", ");
				node = node.next;
			}
		}
		
		return sb.append('}').toString();
	}
	
	static class Node {
		Value<?> key;
		Value<?> value;
		Node next;
		
		Node(Value<?> key, Value<?> value) {
			this.key = key;
			this.value = value;
		}
		
		Value<?> replace(Value<?> value) {
			Value<?> old = this.value;
			this.value = value;
			return old;
		}
	}
	
	static class KeySet implements Set<Value<?>> {
		final List<Value<?>> array;
		final int length;
		
		KeySet(List<Value<?>> array) {
			this.array = array;
			this.length = array.size();
		}
		
		@Override public int size() { return this.length; }
		@Override public boolean isEmpty() { return this.length == 0; }
		@Override public boolean contains(Object o) { return this.array.contains(o); }
		@Override public Iterator<Value<?>> iterator() { return this.array.iterator(); }
		@Override public Object[] toArray() { return this.array.toArray(); }
		@Override public <T> T[] toArray(T[] a) { return this.array.toArray(a); }
		@Override public boolean containsAll(Collection<?> c) { return this.array.containsAll(c); }
		
		// Unsupported
		@Override public boolean add(Value<?> value) { throw new UnsupportedOperationException(); }
		@Override public boolean remove(Object o) { throw new UnsupportedOperationException(); }
		@Override public boolean addAll(Collection<? extends Value<?>> c) { throw new UnsupportedOperationException(); }
		@Override public boolean retainAll(Collection<?> c) { throw new UnsupportedOperationException(); }
		@Override public boolean removeAll(Collection<?> c) { throw new UnsupportedOperationException(); }
		@Override public void clear() { throw new UnsupportedOperationException(); }
	}
}
