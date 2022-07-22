package me.senseiwells.arucas.utils.impl;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.StringUtils;
import me.senseiwells.arucas.utils.ValuePair;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.ValueIdentifier;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This map implementation is just identity theft of HashMap.
 * This map is synchronized and Thread-Safe, it also makes use
 * of ValueIdentifier's isEquals, getHashCode, and getAsString
 * methods for Arucas support.
 * This map also cannot contain null values.
 */
public class ArucasMap implements IArucasCollection, ValueIdentifier {
	private static final Object TOTAL_LOCK = new Object();

	private static final int INITIAL_CAPACITY = 16;
	private static final int MAX_CAPACITY = 1 << 30;
	private static final int TREEIFY_THRESHOLD = 8;
	private static final int UNTREEIFY_THRESHOLD = 6;
	private static final int MIN_TREEIFY_CAPACITY = 64;
	private static final float DEFAULT_LOAD_FACTOR = 0.75F;

	protected final Object LOCK = new Object();
	private Node[] table;
	private int size;
	private int threshold;
	private final float loadFactor;

	public ArucasMap() {
		this.loadFactor = DEFAULT_LOAD_FACTOR;
	}

	@SuppressWarnings("unused")
	public ArucasMap(Context context, ArucasMap map) throws CodeError {
		this();
		this.putMapEntries(context, map, false);
	}

	void putMapEntries(Context context, ArucasMap otherMap, boolean evict) throws CodeError {
		synchronized (this.LOCK) {
			int size = otherMap.size;
			if (size > 0) {
				if (this.table == null) {
					float factorThreshold = (size / this.loadFactor) + 1.0F;
					int threshold = Math.min((int) factorThreshold, MAX_CAPACITY);
					if (threshold > this.threshold) {
						this.threshold = tableSizeFor(threshold);
					}
				}
				else {
					while (size > this.threshold && this.table.length < MAX_CAPACITY) {
						this.resize();
					}
				}
				this.deadlockSafe(otherMap, map -> {
					for (ValuePair valuePair : map.pairSet()) {
						Value key = valuePair.getKey();
						Value value = valuePair.getValue();
						this.putVal(context, hash(context, key), key, value, false, evict);
					}
				});
			}
		}
	}

	@Override
	public int size() {
		return this.size;
	}

	@Override
	public String getAsStringSafe() {
		return "<map>";
	}

	public boolean isEmpty() {
		return this.size == 0;
	}

	public Value get(Context context, Value key) throws CodeError {
		Node node = this.getNode(context, key);
		return node == null ? null : node.value;
	}

	Node getNode(Context context, Value key) throws CodeError {
		// This map doesn't allow null keys
		Objects.requireNonNull(key);
		synchronized (this.LOCK) {
			Node[] table = this.table;
			int size;
			if (table != null && (size = table.length) > 0) {
				int hash = hash(context, key);
				Node first = table[size - 1 & hash];
				if (first == null) {
					return null;
				}
				Value k;
				if (first.hash == hash && ((k = first.key) == key || key.isEquals(context, k))) {
					return first;
				}
				Node e = first.next;
				if (e != null) {
					if (first instanceof TreeNode treeNode) {
						return treeNode.getTreeNode(context, hash, key);
					}
					do {
						if (e.hash == hash && ((k = e.key) == key || key.isEquals(context, k))) {
							return e;
						}
					}
					while ((e = e.next) != null);
				}
			}
			return null;
		}
	}

	public boolean containsKey(Context context, Value key) throws CodeError {
		return this.getNode(context, key) != null;
	}

	public Value getKey(Context context, Value key) throws CodeError {
		Node node = this.getNode(context, key);
		return node == null ? null : node.key;
	}

	public Value put(Context context, Value key, Value value) throws CodeError {
		return this.putVal(context, hash(context, key), key, value, false, true);
	}

	public Value putIfAbsent(Context context, Value key, Value value) throws CodeError {
		return this.putVal(context, hash(context, key), key, value, true, true);
	}

	Value putVal(Context context, int hash, Value key, Value value, boolean onlyIfAbsent, boolean evict) throws CodeError {
		// This map doesn't allow any null values
		Objects.requireNonNull(key);
		Objects.requireNonNull(value);
		synchronized (this.LOCK) {
			Node[] tab = this.table;
			Node p;
			int n, i;
			if (tab == null || (n = tab.length) == 0) {
				n = (tab = this.resize()).length;
			}
			if ((p = tab[i = (n - 1) & hash]) == null) {
				tab[i] = this.newNode(hash, key, value, null);
			}
			else {
				Node e;
				Value k;
				if (p.hash == hash && ((k = p.key) == key || key.isEquals(context, k))) {
					e = p;
				}
				else if (p instanceof TreeNode pt) {
					e = pt.putTreeVal(context, this, tab, hash, key, value);
				}
				else {
					for (int binCount = 0; ; ++binCount) {
						if ((e = p.next) == null) {
							p.next = this.newNode(hash, key, value, null);
							if (binCount >= TREEIFY_THRESHOLD - 1) {
								this.treeifyBin(tab, hash);
							}
							break;
						}
						if (e.hash == hash && ((k = e.key) == key || key.isEquals(context, k))) {
							break;
						}
						p = e;
					}
				}
				if (e != null) {
					Value oldValue = e.value;
					if (!onlyIfAbsent || oldValue == null) {
						e.value = value;
					}
					this.afterNodeAccess(e);
					return oldValue;
				}
			}
			if (this.size++ > this.threshold) {
				this.resize();
			}
			this.afterNodeInsertion(context, evict);
			return null;
		}
	}

	Node[] resize() {
		synchronized (this.LOCK) {
			Node[] oldTab = this.table;
			int oldCap = (oldTab == null) ? 0 : oldTab.length;
			int oldThr = this.threshold;
			int newCap, newThr = 0;
			if (oldCap > 0) {
				if (oldCap >= MAX_CAPACITY) {
					this.threshold = Integer.MAX_VALUE;
					return oldTab;
				}
				if ((newCap = oldCap << 1) < MAX_CAPACITY &&
					oldCap >= INITIAL_CAPACITY) {
					newThr = oldThr << 1;
				}
			}
			else if (oldThr > 0) {
				newCap = oldThr;
			}
			else {
				newCap = INITIAL_CAPACITY;
				newThr = (int) (DEFAULT_LOAD_FACTOR * INITIAL_CAPACITY);
			}
			if (newThr == 0) {
				float ft = (float) newCap * this.loadFactor;
				newThr = newCap < MAX_CAPACITY && ft < (float) MAX_CAPACITY ? (int) ft : Integer.MAX_VALUE;
			}
			this.threshold = newThr;
			Node[] newTab = new Node[newCap];
			this.table = newTab;
			if (oldTab != null) {
				for (int j = 0; j < oldCap; ++j) {
					Node e;
					if ((e = oldTab[j]) != null) {
						oldTab[j] = null;
						if (e.next == null) {
							newTab[e.hash & (newCap - 1)] = e;
						}
						else if (e instanceof TreeNode et) {
							et.split(this, newTab, j, oldCap);
						}
						else {
							Node loHead = null, loTail = null;
							Node hiHead = null, hiTail = null;
							Node next;
							do {
								next = e.next;
								if ((e.hash & oldCap) == 0) {
									if (loTail == null) {
										loHead = e;
									}
									else {
										loTail.next = e;
									}
									loTail = e;
								}
								else {
									if (hiTail == null) {
										hiHead = e;
									}
									else {
										hiTail.next = e;
									}
									hiTail = e;
								}
							}
							while ((e = next) != null);
							if (loTail != null) {
								loTail.next = null;
								newTab[j] = loHead;
							}
							if (hiTail != null) {
								hiTail.next = null;
								newTab[j + oldCap] = hiHead;
							}
						}
					}
				}
			}
			return newTab;
		}
	}

	void treeifyBin(Node[] tab, int hash) {
		synchronized (this.LOCK) {
			int n, index;
			Node e;
			if (tab == null || (n = tab.length) < MIN_TREEIFY_CAPACITY) {
				this.resize();
			}
			else if ((e = tab[index = (n - 1) & hash]) != null) {
				TreeNode hd = null, tl = null;
				do {
					TreeNode p = this.replacementTreeNode(e, null);
					if (tl == null) {
						hd = p;
					}
					else {
						p.previous = tl;
						tl.next = p;
					}
					tl = p;
				}
				while ((e = e.next) != null);
				if ((tab[index] = hd) != null) {
					hd.treeify(tab);
				}
			}
		}
	}

	public void putAll(Context context, ArucasMap map) throws CodeError {
		this.putMapEntries(context, map, true);
	}

	public Value remove(Context context, Value key) throws CodeError {
		Node e = this.removeNode(context, hash(context, key), key, null, false, true);
		return e == null ? null : e.value;
	}

	public boolean remove(Context context, Value key, Value value) throws CodeError {
		return this.removeNode(context, hash(context, key), key, value, true, true) != null;
	}

	@SuppressWarnings("SameParameterValue")
	Node removeNode(Context context, int hash, Value key, Value value, boolean matchValue, boolean movable) throws CodeError {
		Objects.requireNonNull(key);
		synchronized (this.LOCK) {
			Node[] tab;
			Node p;
			int n, index;
			if ((tab = this.table) != null && (n = tab.length) > 0 &&
				(p = tab[index = (n - 1) & hash]) != null) {
				Node node = null, e;
				Value k;
				Value v;
				if (p.hash == hash && ((k = p.key) == key || key.isEquals(context, k))) {
					node = p;
				}
				else if ((e = p.next) != null) {
					if (p instanceof TreeNode pt) {
						node = pt.getTreeNode(context, hash, key);
					}
					else {
						do {
							if (e.hash == hash && ((k = e.key) == key || key.isEquals(context, k))) {
								node = e;
								break;
							}
							p = e;
						}
						while ((e = e.next) != null);
					}
				}
				if (node != null && (!matchValue || (v = node.value) == value ||
					(value != null && value.isEquals(context, v)))) {
					if (node instanceof TreeNode treeNode) {
						treeNode.removeTreeNode(this, tab, movable);
					}
					else if (node == p) {
						tab[index] = node.next;
					}
					else {
						p.next = node.next;
					}
					this.size--;
					this.afterNodeRemoval(node);
					return node;
				}
			}
			return null;
		}
	}

	public void clear() {
		synchronized (this.LOCK) {
			Node[] table = this.table;
			if (table != null && this.size > 0) {
				this.size = 0;
				Arrays.fill(table, null);
			}
		}
	}

	@SuppressWarnings("unused")
	public boolean containsValue(Context context, Value value) throws CodeError {
		if (value == null) {
			return false;
		}
		synchronized (this.LOCK) {
			Node[] table = this.table;
			Value v;
			if (table != null && this.size > 0) {
				for (Node e : table) {
					for (; e != null; e = e.next) {
						if ((v = e.value) == value || value.isEquals(context, v)) {
							return true;
						}
					}
				}
			}
			return false;
		}
	}

	@Override
	public Collection<Value> asCollection() {
		return this.keys();
	}

	@Override
	public String getAsStringUnsafe(Context context, ISyntax position) throws CodeError {
		synchronized (this.LOCK) {
			StringBuilder sb = new StringBuilder();
			sb.append("{");

			for (ValuePair pair : this.pairSet()) {
				String key = pair.getKey().isCollection() ?
					pair.getKey().asCollection(context, position).getAsStringUnsafe(context, position) : pair.getKey().getAsString(context);
				sb.append(key).append(": ");

				String value = pair.getValue().isCollection() ?
					pair.getValue().asCollection(context, position).getAsStringUnsafe(context, position) : pair.getValue().getAsString(context);
				sb.append(value).append(", ");
			}

			if (!this.pairSet().isEmpty()) {
				sb.delete(sb.length() - 2, sb.length());
			}

			return sb.append("}").toString();
		}
	}

	public ArucasList keys() {
		ArucasList keyList = new ArucasList();
		synchronized (this.LOCK) {
			Node[] table = this.table;
			if (this.size > 0 && table != null) {
				for (Node node : table) {
					for (; node != null; node = node.next) {
						keyList.add(node.key);
					}
				}
			}
			return keyList;
		}
	}

	public ArucasList values() {
		ArucasList valueList = new ArucasList();
		synchronized (this.LOCK) {
			Node[] table = this.table;
			if (this.size > 0 && table != null) {
				for (Node node : table) {
					for (; node != null; node = node.next) {
						valueList.add(node.value);
					}
				}
			}
			return valueList;
		}
	}

	public Set<ValuePair> pairSet() {
		Set<ValuePair> pairSet = new HashSet<>();
		synchronized (this.LOCK) {
			Node[] table = this.table;
			if (this.size > 0 && table != null) {
				for (Node node : table) {
					for (; node != null; node = node.next) {
						pairSet.add(new ValuePair(node.key, node.value));
					}
				}
			}
			return pairSet;
		}
	}

	private void deadlockSafe(ArucasMap otherMap, MapConsumer consumer) throws CodeError {
		/*
		 * If we always synchronize on DEADLOCK_HANDLE when locking on parameters
		 * we will never let two threads lock on each other without waiting for
		 * the other thread to release their lock on DEADLOCK_HANDLE.
		 * This prevents any deadlocks from happening.
		 * Anything that runs here will be slow since this method can only be
		 * executed once at a time, including if you are running multiple scripts.
		 */
		synchronized (TOTAL_LOCK) {
			synchronized (this.LOCK) {
				synchronized (otherMap.LOCK) {
					consumer.accept(otherMap);
				}
			}
		}
	}

	@SuppressWarnings("SameParameterValue")
	Node newNode(int hash, Value key, Value value, TreeNode next) {
		return new Node(hash, key, value, next);
	}

	@SuppressWarnings("SameParameterValue")
	Node replacementNode(Node node, Node next) {
		return new Node(node.hash, node.key, node.value, next);
	}

	TreeNode newTreeNode(int hash, Value key, Value value, Node next) {
		return new TreeNode(hash, key, value, next);
	}

	@SuppressWarnings("SameParameterValue")
	TreeNode replacementTreeNode(Node node, Node next) {
		return new TreeNode(node.hash, node.key, node.value, next);
	}

	// These are callbacks for OrderedMap...
	void afterNodeAccess(Node p) { }

	void afterNodeInsertion(Context context, boolean evict) throws CodeError { }

	void afterNodeRemoval(Node p) { }

	@Override
	public int getHashCode(Context context) throws CodeError {
		synchronized (this.LOCK) {
			int h = 0;

			if (this.table == null) {
				return 0;
			}

			for (Node value : this.table) {
				Node node = value;

				while (node != null) {
					h += node.getHashCode(context);
					node = node.next;
				}
			}

			return h;
		}
	}

	@Override
	public String getAsString(Context context) throws CodeError {
		synchronized (this.LOCK) {
			StringBuilder sb = new StringBuilder();
			sb.append('{');

			Set<ValuePair> pairSet = this.pairSet();
			for (ValuePair valuePair : pairSet) {
				sb.append(StringUtils.toPlainString(context, valuePair.getKey())).append(": ");
				sb.append(StringUtils.toPlainString(context, valuePair.getValue())).append(", ");
			}

			if (!pairSet.isEmpty()) {
				sb.delete(sb.length() - 2, sb.length());
			}

			return sb.append('}').toString();
		}
	}

	@Override
	public boolean isEquals(Context context, Value other) throws CodeError {
		if (!(other.getValue() instanceof ArucasMap that)) {
			return false;
		}

		if (this == that) {
			return true;
		}

		AtomicBoolean returnBoolean = new AtomicBoolean(false);
		this.deadlockSafe(that, map -> {
			if (this.size != map.size) {
				return;
			}

			for (Node thatNode : map.table) {
				for (; thatNode != null; thatNode = thatNode.next) {
					// Check if keys are equal
					Node thisNode = this.getNode(context, thatNode.key);

					// Check if the nodes are equal
					if (thisNode == null || !thisNode.isEquals(context, thatNode)) {
						return;
					}
				}
			}

			returnBoolean.set(true);
		});
		return returnBoolean.get();
	}

	static int hash(Context context, Value key) throws CodeError {
		int hash = key.getHashCode(context);
		return hash ^ hash >>> 16;
	}

	static int tableSizeFor(int cap) {
		int n = -1 >>> Integer.numberOfLeadingZeros(cap - 1);
		return n < 0 ? 1 : (n >= MAX_CAPACITY) ? MAX_CAPACITY : n + 1;
	}

	static class Node implements ValueIdentifier {
		final int hash;
		final Value key;
		Value value;
		Node next;

		Node(int hash, Value key, Value value, Node next) {
			Objects.requireNonNull(key);
			Objects.requireNonNull(value);
			this.hash = hash;
			this.key = key;
			this.value = value;
			this.next = next;
		}

		@SuppressWarnings("unused")
		public Value getKey() {
			return this.key;
		}

		public Value getValue() {
			return this.value;
		}

		public boolean isEquals(Context context, Node otherNode) throws CodeError {
			if (this == otherNode) {
				return true;
			}
			return this.key.isEquals(context, otherNode.key) && this.value.isEquals(context, otherNode.value);
		}

		@Override
		public String getAsString(Context context) throws CodeError {
			return this.key.getAsString(context) + "=" + this.value.getAsString(context);
		}

		@Override
		public int getHashCode(Context context) throws CodeError {
			return this.key.getHashCode(context) ^ this.value.getHashCode(context);
		}

		@Deprecated
		@Override
		public boolean isEquals(Context context, Value other) {
			return false;
		}

		@Deprecated
		@Override
		public boolean equals(Object obj) {
			return super.equals(obj);
		}

		@Deprecated
		@Override
		public int hashCode() {
			return super.hashCode();
		}

		@Deprecated
		@Override
		public String toString() {
			return super.toString();
		}
	}

	static class TreeNode extends ArucasOrderedMap.Entry {
		TreeNode parent;
		TreeNode left;
		TreeNode right;
		TreeNode previous;
		boolean red;

		TreeNode(int hash, Value key, Value value, Node next) {
			super(hash, key, value, next);
		}

		TreeNode root() {
			TreeNode current = this, parent;
			while (true) {
				parent = current.parent;
				if (parent == null) {
					return current;
				}
				current = parent;
			}
		}

		static void moveRootToFront(Node[] table, TreeNode root) {
			int size;
			if (root != null && table != null && (size = table.length) > 0) {
				int index = (size - 1) & root.hash;
				TreeNode first = (TreeNode) table[index];
				if (root != first) {
					table[index] = root;
					TreeNode next = (TreeNode) root.next;
					TreeNode previous = root.previous;
					if (next != null) {
						next.previous = previous;
					}
					if (previous != null) {
						previous.next = next;
					}
					if (first != null) {
						first.previous = root;
					}
					root.next = first;
					root.previous = null;
				}
			}
		}

		TreeNode find(Context context, int hash, Value key) throws CodeError {
			TreeNode current = this;
			do {
				int currentHash;
				Value currentKey;
				TreeNode pl = current.left, pr = current.right, q;
				if ((currentHash = current.hash) > hash) {
					current = pl;
				}
				else if (currentHash < hash) {
					current = pr;
				}
				else if ((currentKey = current.key) == key || key.isEquals(context, currentKey)) {
					return current;
				}
				else if (pl == null) {
					current = pr;
				}
				else if (pr == null) {
					current = pl;
				}
				else if ((q = pr.find(context, hash, key)) != null) {
					return q;
				}
				else {
					current = pl;
				}
			}
			while (current != null);
			return null;
		}

		TreeNode getTreeNode(Context context, int hash, Value key) throws CodeError {
			return ((this.parent != null) ? this.root() : this).find(context, hash, key);
		}

		static int tieBreakOrder(Value a, Value b) {
			int d;
			if (a == null || b == null || (d = a.getClass().getName().compareTo(b.getClass().getName())) == 0) {
				d = System.identityHashCode(a) <= System.identityHashCode(b) ? -1 : 1;
			}
			return d;
		}

		void treeify(Node[] table) {
			TreeNode root = null;
			for (TreeNode x = this, next; x != null; x = next) {
				next = (TreeNode) x.next;
				x.left = x.right = null;
				if (root == null) {
					x.parent = null;
					x.red = false;
					root = x;
					continue;
				}
				int h = x.hash;
				TreeNode p = root;
				while (true) {
					int dir, ph = p.hash;
					Value pk = p.key;
					dir = ph > h ? -1 : ph < h ? 1 : tieBreakOrder(x.key, pk);
					TreeNode xp = p;
					p = dir <= 0 ? p.left : p.right;
					if (p == null) {
						x.parent = xp;
						if (dir <= 0) {
							xp.left = x;
						}
						else {
							xp.right = x;
						}
						root = balanceInsertion(root, x);
						break;
					}
				}
			}
			moveRootToFront(table, root);
		}

		Node untreeify(ArucasMap map) {
			Node returnNode = null, lastReplacement = null;
			for (Node current = this; current != null; current = current.next) {
				Node replacement = map.replacementNode(current, null);
				if (lastReplacement == null) {
					returnNode = replacement;
				}
				else {
					lastReplacement.next = replacement;
				}
				lastReplacement = replacement;
			}
			return returnNode;
		}

		TreeNode putTreeVal(Context context, ArucasMap map, Node[] table, int h, Value k, Value v) throws CodeError {
			boolean searched = false;
			TreeNode root = this.parent != null ? this.root() : this;
			TreeNode p = root;
			while (true) {
				int dir, ph = p.hash;
				Value pk;
				if (ph > h) {
					dir = -1;
				}
				else if (ph < h) {
					dir = 1;
				}
				else if ((pk = p.key) == k || (k.isEquals(context, pk))) {
					return p;
				}
				else {
					if (!searched) {
						TreeNode q, ch = p.left == null ? p.right : p.left;
						searched = true;
						if (ch != null && (q = ch.find(context, h, k)) != null) {
							return q;
						}
					}
					dir = tieBreakOrder(k, pk);
				}
				TreeNode xp = p;
				p = dir <= 0 ? p.left : p.right;
				if (p == null) {
					Node xpn = xp.next;
					TreeNode x = map.newTreeNode(h, k, v, xpn);
					if (dir <= 0) {
						xp.left = x;
					}
					else {
						xp.right = x;
					}
					if (xpn != null) {
						((TreeNode) xpn).previous = x;
					}
					moveRootToFront(table, balanceInsertion(root, x));
					return null;
				}
			}
		}

		void removeTreeNode(ArucasMap map, Node[] table, boolean movable) {
			int size;
			if (table == null || (size = table.length) == 0) {
				return;
			}
			int index = (size - 1) & this.hash;
			TreeNode first = (TreeNode) table[index], root = first, rootLeft;
			TreeNode successor = (TreeNode) this.next, previous = this.previous;
			if (previous == null) {
				table[index] = first = successor;
			}
			else {
				previous.next = successor;
			}
			if (successor != null) {
				successor.previous = previous;
			}
			if (first == null) {
				return;
			}
			if (root.parent != null) {
				root = root.root();
			}
			if (movable && (root.right == null || (rootLeft = root.left) == null || rootLeft.left == null)) {
				table[index] = first.untreeify(map);
				return;
			}
			TreeNode current = this, currentLeft = this.left, currentRight = this.right, replacement;
			if (currentLeft != null && currentRight != null) {
				TreeNode next = currentRight, nextLeft;
				while ((nextLeft = next.left) != null) {
					next = nextLeft;
				}
				boolean nextRed = next.red;
				next.red = current.red;
				current.red = nextRed;
				TreeNode nextRight = next.right, currentParent = current.parent;
				if (next == currentRight) {
					current.parent = next;
					next.right = current;
				}
				else {
					TreeNode nextParent = next.parent;
					if ((current.parent = nextParent) != null) {
						if (next == nextParent.left) {
							nextParent.left = current;
						}
						else {
							nextParent.right = current;
						}
					}
					next.right = currentRight;
					currentRight.parent = next;
				}
				current.left = null;
				if ((current.right = nextRight) != null) {
					nextRight.parent = current;
				}
				next.left = currentLeft;
				currentLeft.parent = next;
				if ((next.parent = currentParent) == null) {
					root = next;
				}
				else if (current == currentParent.left) {
					currentParent.left = next;
				}
				else {
					currentParent.right = next;
				}
				replacement = nextRight == null ? current : nextRight;
			}
			else {
				replacement = currentLeft != null ? currentLeft : Objects.requireNonNullElse(currentRight, current);
			}
			if (replacement != current) {
				TreeNode pp = replacement.parent = current.parent;
				if (pp == null) {
					(root = replacement).red = false;
				}
				else if (current == pp.left) {
					pp.left = replacement;
				}
				else {
					pp.right = replacement;
				}
				current.left = current.right = current.parent = null;
			}
			TreeNode rootNode = current.red ? root : balanceDeletion(root, replacement);
			if (replacement == current) {
				TreeNode pp = current.parent;
				current.parent = null;
				if (pp != null) {
					if (current == pp.left) {
						pp.left = null;
					}
					else if (current == pp.right) {
						pp.right = null;
					}
				}
			}
			if (movable) {
				moveRootToFront(table, rootNode);
			}
		}

		void split(ArucasMap map, Node[] table, int index, int bit) {
			TreeNode treeNode = this;
			TreeNode loHead = null, loTail = null;
			TreeNode hiHead = null, hiTail = null;
			int lc = 0, hc = 0;
			for (TreeNode e = treeNode, next; e != null; e = next) {
				next = (TreeNode) e.next;
				e.next = null;
				if ((e.hash & bit) == 0) {
					if ((e.previous = loTail) == null) {
						loHead = e;
					}
					else {
						loTail.next = e;
					}
					loTail = e;
					lc++;
				}
				else {
					if ((e.previous = hiTail) == null) {
						hiHead = e;
					}
					else {
						hiTail.next = e;
					}
					hiTail = e;
					hc++;
				}
			}

			if (loHead != null) {
				if (lc <= UNTREEIFY_THRESHOLD) {
					table[index] = loHead.untreeify(map);
				}
				else {
					table[index] = loHead;
					if (hiHead != null) {
						loHead.treeify(table);
					}
				}
			}
			if (hiHead != null) {
				if (hc <= UNTREEIFY_THRESHOLD) {
					table[index + bit] = hiHead.untreeify(map);
				}
				else {
					table[index + bit] = hiHead;
					if (loHead != null) {
						hiHead.treeify(table);
					}
				}
			}
		}

		static TreeNode rotateLeft(TreeNode root, TreeNode p) {
			TreeNode r, pp, rl;
			if (p != null && (r = p.right) != null) {
				if ((rl = p.right = r.left) != null) {
					rl.parent = p;
				}
				if ((pp = r.parent = p.parent) == null) {
					(root = r).red = false;
				}
				else if (pp.left == p) {
					pp.left = r;
				}
				else {
					pp.right = r;
				}
				r.left = p;
				p.parent = r;
			}
			return root;
		}

		static TreeNode rotateRight(TreeNode root, TreeNode p) {
			TreeNode l, pp, lr;
			if (p != null && (l = p.left) != null) {
				if ((lr = p.left = l.right) != null) {
					lr.parent = p;
				}
				if ((pp = l.parent = p.parent) == null) {
					(root = l).red = false;
				}
				else if (pp.right == p) {
					pp.right = l;
				}
				else {
					pp.left = l;
				}
				l.right = p;
				p.parent = l;
			}
			return root;
		}

		static TreeNode balanceInsertion(TreeNode root, TreeNode x) {
			x.red = true;
			TreeNode xp, xpp, xppl, xppr;
			while (true) {
				xp = x.parent;
				if (xp == null) {
					x.red = false;
					return x;
				}
				if (!xp.red || (xpp = xp.parent) == null) {
					return root;
				}
				if (xp == (xppl = xpp.left)) {
					if ((xppr = xpp.right) != null && xppr.red) {
						xppr.red = false;
						xp.red = false;
						xpp.red = true;
						x = xpp;
					}
					else {
						if (x == xp.right) {
							root = rotateLeft(root, x = xp);
							xpp = (xp = x.parent) == null ? null : xp.parent;
						}
						if (xp != null) {
							xp.red = false;
							if (xpp != null) {
								xpp.red = true;
								root = rotateRight(root, xpp);
							}
						}
					}
				}
				else {
					if (xppl != null && xppl.red) {
						xppl.red = false;
						xp.red = false;
						xpp.red = true;
						x = xpp;
					}
					else {
						if (x == xp.left) {
							root = rotateRight(root, x = xp);
							xpp = (xp = x.parent) == null ? null : xp.parent;
						}
						if (xp != null) {
							xp.red = false;
							if (xpp != null) {
								xpp.red = true;
								root = rotateLeft(root, xpp);
							}
						}
					}
				}
			}
		}

		static TreeNode balanceDeletion(TreeNode root, TreeNode x) {
			for (TreeNode xp, xpl, xpr; ; ) {
				if (x == null || x == root) {
					return root;
				}
				if ((xp = x.parent) == null) {
					x.red = false;
					return x;
				}
				if (x.red) {
					x.red = false;
					return root;
				}
				if ((xpl = xp.left) == x) {
					if ((xpr = xp.right) != null && xpr.red) {
						xpr.red = false;
						xp.red = true;
						root = rotateLeft(root, xp);
						xpr = (xp = x.parent) == null ? null : xp.right;
					}
					if (xpr == null) {
						x = xp;
					}
					else {
						TreeNode sl = xpr.left, sr = xpr.right;
						if ((sr == null || !sr.red) && (sl == null || !sl.red)) {
							xpr.red = true;
							x = xp;
						}
						else {
							if (sr == null || !sr.red) {
								sl.red = false;
								xpr.red = true;
								root = rotateRight(root, xpr);
								xpr = (xp = x.parent) == null ?
									null : xp.right;
							}
							if (xpr != null) {
								xpr.red = xp.red;
								if ((sr = xpr.right) != null) {
									sr.red = false;
								}
							}
							if (xp != null) {
								xp.red = false;
								root = rotateLeft(root, xp);
							}
							x = root;
						}
					}
				}
				else {
					if (xpl != null && xpl.red) {
						xpl.red = false;
						xp.red = true;
						root = rotateRight(root, xp);
						xpl = (xp = x.parent) == null ? null : xp.left;
					}
					if (xpl == null) {
						x = xp;
					}
					else {
						TreeNode sl = xpl.left, sr = xpl.right;
						if ((sl == null || !sl.red) && (sr == null || !sr.red)) {
							xpl.red = true;
							x = xp;
						}
						else {
							if (sl == null || !sl.red) {
								sr.red = false;
								xpl.red = true;
								root = rotateLeft(root, xpl);
								xpl = (xp = x.parent) == null ? null : xp.left;
							}
							if (xpl != null) {
								xpl.red = xp.red;
								if ((sl = xpl.left) != null) {
									sl.red = false;
								}
							}
							if (xp != null) {
								xp.red = false;
								root = rotateRight(root, xp);
							}
							x = root;
						}
					}
				}
			}
		}
	}

	@FunctionalInterface
	interface MapConsumer {
		void accept(ArucasMap map) throws CodeError;
	}
}
