package me.senseiwells.arucas.utils.impl;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.ValuePair;
import me.senseiwells.arucas.values.Value;

import java.util.LinkedHashSet;
import java.util.Set;

// LinkedHashMap
public class ArucasOrderedMap extends ArucasMap {
	private Entry head;
	private Entry tail;
	private final boolean accessOrder;

	public ArucasOrderedMap() {
		super();
		this.accessOrder = false;
	}

	public ArucasOrderedMap(Context context, ArucasMap arucasMap) throws CodeError {
		this();
		this.putMapEntries(context, arucasMap, false);
	}

	private void linkNodeLast(Entry entry) {
		synchronized (this.LOCK) {
			Entry last = this.tail;
			this.tail = entry;
			if (last == null) {
				this.head = entry;
				return;
			}
			entry.before = last;
			last.after = entry;
		}
	}

	private void transferLinks(Entry source, Entry destination) {
		synchronized (this.LOCK) {
			Entry b = destination.before = source.before;
			Entry a = destination.after = source.after;
			if (b == null) {
				this.head = destination;
			}
			else {
				b.after = destination;
			}
			if (a == null) {
				this.tail = destination;
			}
			else {
				a.before = destination;
			}
		}
	}

	@Override
	Node newNode(int hash, Value<?> key, Value<?> value, TreeNode next) {
		Entry entry = new Entry(hash, key, value, next);
		this.linkNodeLast(entry);
		return entry;
	}

	@Override
	Node replacementNode(Node node, Node next) {
		Entry q = (Entry) node;
		Entry t = new Entry(q.hash, q.key, q.value, next);
		this.transferLinks(q, t);
		return t;
	}

	@Override
	TreeNode newTreeNode(int hash, Value<?> key, Value<?> value, Node next) {
		TreeNode p = new TreeNode(hash, key, value, next);
		this.linkNodeLast(p);
		return p;
	}

	@Override
	TreeNode replacementTreeNode(Node node, Node next) {
		Entry q = (Entry) node;
		TreeNode t = new TreeNode(q.hash, q.key, q.value, next);
		this.transferLinks(q, t);
		return t;
	}


	@Override
	void afterNodeAccess(Node e) {
		synchronized (this.LOCK) {
			Entry last;
			if (this.accessOrder && (last = this.tail) != e) {
				Entry p =
					(Entry) e, b = p.before, a = p.after;
				p.after = null;
				if (b == null) {
					this.head = a;
				}
				else {
					b.after = a;
				}
				if (a != null) {
					a.before = b;
				}
				else {
					last = b;
				}
				if (last == null) {
					this.head = p;
				}
				else {
					p.before = last;
					last.after = p;
				}
				this.tail = p;
			}
		}
	}

	@Override
	public boolean containsValue(Context context, Value<?> value) throws CodeError {
		if (value == null) {
			return false;
		}
		synchronized (this.LOCK) {
			for (Entry e = this.head; e != null; e = e.after) {
				Value<?> v = e.value;
				if (v == value || value.isEquals(context, v)) {
					return true;
				}
			}
			return false;
		}
	}

	@Override
	public Value<?> get(Context context, Value<?> key) throws CodeError {
		synchronized (this.LOCK) {
			Node e = this.getNode(context, key);
			if (e == null) {
				return null;
			}
			if (this.accessOrder) {
				this.afterNodeAccess(e);
			}
			return e.value;
		}
	}

	@Override
	public void clear() {
		synchronized (this.LOCK) {
			super.clear();
			this.head = this.tail = null;
		}
	}

	@SuppressWarnings("unused")
	protected boolean removeEldestEntry(Entry eldest) {
		return false;
	}

	@Override
	public ArucasList keys() {
		ArucasList keyList = new ArucasList();
		synchronized (this.LOCK) {
			for (Entry e = this.head; e != null; e = e.after) {
				keyList.add(e.key);
			}
			return keyList;
		}
	}

	@Override
	public ArucasList values() {
		ArucasList valueList = new ArucasList();
		synchronized (this.LOCK) {
			for (Entry e = this.head; e != null; e = e.after) {
				valueList.add(e.value);
			}
			return valueList;
		}
	}

	@Override
	public Set<ValuePair> pairSet() {
		Set<ValuePair> pairSet = new LinkedHashSet<>();
		synchronized (this.LOCK) {
			for (Entry e = this.head; e != null; e = e.after) {
				pairSet.add(new ValuePair(e.key, e.value));
			}
			return pairSet;
		}
	}

	@Override
	void afterNodeInsertion(Context context, boolean evict) throws CodeError {
		Entry first;
		if (evict && (first = this.head) != null && this.removeEldestEntry(first)) {
			Value<?> key = first.key;
			this.removeNode(context, hash(context, key), key, null, false, true);
		}
	}

	@Override
	void afterNodeRemoval(Node e) {
		Entry p =
			(Entry) e, b = p.before, a = p.after;
		p.before = p.after = null;
		if (b == null) {
			this.head = a;
		}
		else {
			b.after = a;
		}
		if (a == null) {
			this.tail = b;
		}
		else {
			a.before = b;
		}
	}

	static class Entry extends Node {
		Entry before, after;

		Entry(int hash, Value<?> key, Value<?> value, Node next) {
			super(hash, key, value, next);
		}
	}
}
