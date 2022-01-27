package me.senseiwells.arucas.utils.impl;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.StringUtils;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.ValueIdentifier;

import java.util.*;

/**
 * Custom list implementation.
 * Most of the code was taken from ArrayList,
 * this implements {@link me.senseiwells.arucas.values.ValueIdentifier}
 * as it is easier to implement these
 * methods natively.
 */
public class ArucasList implements List<Value<?>>, ValueIdentifier {
	private static final Object DEADLOCKED_HANDLER = new Object();
	private static final Value<?>[] DEFAULT_DATA = {};
	private static final int DEFAULT_CAPACITY = 10;
	
	private Value<?>[] valueData;
	private int size;

	public ArucasList() {
		this.valueData = DEFAULT_DATA;
	}

	public ArucasList(ArucasList valueList) {
		Value<?>[] valueArray = valueList.toArray();
		this.size = valueArray.length;
		this.valueData = this.size == 0 ? DEFAULT_DATA : valueArray;
	}

	@Override
	public synchronized int size() {
		return this.size;
	}

	@Override
	public synchronized boolean isEmpty() {
		return this.size == 0;
	}

	@Override
	public synchronized Value<?> get(int index) {
		this.checkExistingIndex(index);
		return this.valueData[index];
	}

	public synchronized boolean contains(Context context, Value<?> value) throws CodeError {
		return this.indexOf(context, value) >= 0;
	}

	public synchronized boolean containsAll(Context context, ArucasList valueList) throws CodeError {
		if (this.size < valueList.size) {
			return false;
		}
		// TODO: This allocates another iterator.. Try make it faster
		// This calls .toArray() because it may cause deadlocks
		for (Value<?> value : valueList.toArray()) {
			if (!this.contains(context, value)) {
				return false;
			}
		}
		return true;
	}

	public synchronized int indexOf(Context context, Value<?> value) throws CodeError {
		Value<?>[] valueData = this.valueData;
		for (int i = 0; i < this.size; i++) {
			if (value.isEquals(context, valueData[i])) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public synchronized boolean add(Value<?> value) {
		this.add(value, this.valueData, this.size);
		return true;
	}

	@Override
	public synchronized void add(int index, Value<?> value) {
		this.checkAddIndex(index);
		final int size = this.size;
		Value<?>[] valueData = this.valueData;
		if (size == valueData.length) {
			valueData = this.grow();
		}
		System.arraycopy(valueData, index, valueData, index + 1, size - index);
		valueData[index] = value;
		this.size = size + 1;
	}

	private synchronized void add(Value<?> value, Value<?>[] valueData, int size) {
		if (size == valueData.length) {
			valueData = grow();
		}
		valueData[size] = value;
		this.size = size + 1;
	}

	@Override
	public synchronized boolean addAll(Collection<? extends Value<?>> values) {
		if (values instanceof ArucasList arucasList) {
			return this.addAll(arucasList.toArray());
		}
		return this.addAll(values.toArray(Value[]::new));
	}

	private synchronized boolean addAll(Value<?>[] valueArray) {
		int newSize = valueArray.length;
		if (newSize == 0) {
			return false;
		}
		Value<?>[] valueData = this.valueData;
		final int size = this.size;
		if (newSize > valueData.length - size) {
			valueData = this.grow(size + newSize);
		}
		System.arraycopy(valueArray, 0, valueData, size, newSize);
		this.size = size + newSize;
		return true;
	}

	@Override
	public synchronized Value<?> remove(int index) {
		this.checkAddIndex(index);
		final Value<?>[] valueData = this.valueData;
		Value<?> oldValue = valueData[index];
		this.remove(valueData, index);
		return oldValue;
	}

	public synchronized boolean remove(Context context, Value<?> value) throws CodeError {
		final Value<?>[] valueData = this.valueData;
		for (int i = 0; i < this.size; i++) {
			if (value.isEquals(context, valueData[i])) {
				this.remove(valueData, i);
				return true;
			}
		}
		return false;
	}

	private synchronized void remove(Value<?>[] valueData, int index) {
		final int newSize = this.size - 1;
		if (newSize > index) {
			System.arraycopy(valueData, index + 1, valueData, index, newSize - index);
		}
		valueData[this.size = newSize] = null;
	}

	@Override
	public boolean removeAll(Collection<?> collection) {
		return this.batchRemove(collection, this.size);
	}

	private boolean batchRemove(Collection<?> collection, final int to) {
		final Value<?>[] valueData = this.valueData;
		int i = 0;
		for (;; i++) {
			if (i == to) {
				return false;
			}
			if (collection.contains(valueData[i])) {
				break;
			}
		}
		int j = i++;
		for (Value<?> value; i < to; i++) {
			if (!collection.contains(value = valueData[i])) {
				valueData[j++] = value;
			}
		}
		this.shiftTailOverGap(valueData, j, to);
		return true;
	}

	@Override
	public synchronized void clear() {
		final Value<?>[] valueData = this.valueData;
		for (int to = this.size, i = this.size = 0; i < to; i++) {
			valueData[i] = null;
		}
	}

	@Override
	public Value<?>[] toArray() {
		return Arrays.copyOf(this.valueData, this.size);
	}

	private synchronized Value<?>[] grow() {
		return this.grow(this.size + 1);
	}

	private synchronized Value<?>[] grow(int minCapacity) {
		final int oldCapacity = this.valueData.length;
		if (oldCapacity > 0 || this.valueData != DEFAULT_DATA) {
			int newCapacity = newLength(oldCapacity, minCapacity - oldCapacity, oldCapacity >> 1);
			return this.valueData = Arrays.copyOf(this.valueData, newCapacity);
		}
		return this.valueData = new Value<?>[Math.max(DEFAULT_CAPACITY, minCapacity)];
	}

	private synchronized void shiftTailOverGap(Value<?>[] valueData, int low, int high) {
		System.arraycopy(valueData, high, valueData, low, this.size - high);
		for (int to = this.size, i = (this.size -= high - low); i < to; i++) {
			valueData[i] = null;
		}
	}

	private synchronized void checkAddIndex(int index) {
		if (index < 0 || index > this.size) {
			throw new IndexOutOfBoundsException();
		}
	}

	private synchronized void checkExistingIndex(int index)  {
		if (index < 0 || index >= this.size) {
			throw new IndexOutOfBoundsException();
		}
	}

	@Override
	public synchronized int getHashCode(Context context) throws CodeError {
		final Value<?>[] valueData = this.valueData;
		int hashCode = 1;
		for (int i = 0; i < this.size; i++) {
			Value<?> value = valueData[i];
			hashCode = 31 * hashCode + (value == null ? 0 : value.getHashCode(context));
		}
		return hashCode;
	}

	@Override
	public boolean isEquals(Context context, Value<?> other) throws CodeError {
		if (!(other.value instanceof ArucasList that)) {
			return false;
		}
		
		if (this == that) {
			return true;
		}
		
		synchronized (DEADLOCKED_HANDLER) {
			synchronized (this) {
				synchronized (that) {
					if (this.size != that.size) {
						return false;
					}

					for (int i = 0; i < this.size; i++) {
						if (!this.valueData[i].isEquals(context, that.valueData[i])) {
							return false;
						}
					}
				}
			}
		}

		return true;
	}

	@Override
	public synchronized String getAsString(Context context) throws CodeError {
		if (this.isEmpty()) {
			return "[]";
		}

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("[");

		int i = 0;
		while (i < this.size) {
			stringBuilder.append(StringUtils.toPlainString(context, this.valueData[i]));
			i++;
			if (i < this.size) {
				stringBuilder.append(", ");
			}
		}

		stringBuilder.append("]");
		return stringBuilder.toString();
	}

	@Override
	public synchronized Iterator<Value<?>> iterator() {
		return new Iterator<>() {
			int cursor = 0;

			@Override
			public synchronized boolean hasNext() {
				return this.cursor != ArucasList.this.size;
			}

			@Override
			public synchronized Value<?> next() {
				final int i = this.cursor;
				if (i >= ArucasList.this.size) {
					throw new NoSuchElementException();
				}
				Value<?>[] valueData = ArucasList.this.valueData;
				this.cursor = i + 1;
				return valueData[i];
			}
		};
	}

	/**
	 * These methods should not be used instead
	 * {@link #getAsString(Context)},
	 * {@link #getHashCode(Context)},
	 * {@link #isEquals(Context, Value)},
	 * should be used.
	 */

	@Deprecated
	@Override
	public final int hashCode() {
		return super.hashCode();
	}

	@Deprecated
	@Override
	public final boolean equals(Object obj) {
		return this == obj;
	}

	@Deprecated
	@Override
	public final String toString() {
		return super.toString();
	}

	/**
	 * These methods are unsupported.
	 */

	@Override public boolean addAll(int index, Collection<? extends Value<?>> c) { throw new UnsupportedOperationException(); }
	@Override public boolean contains(Object o) { throw new UnsupportedOperationException(); }
	@Override public boolean remove(Object o) { throw new UnsupportedOperationException(); }
	@Override public boolean containsAll(Collection<?> c) { throw new UnsupportedOperationException(); }
	@Override public boolean retainAll(Collection<?> c) { throw new UnsupportedOperationException(); }
	@Override public <T> T[] toArray(T[] a) { throw new UnsupportedOperationException(); }
	@Override public int indexOf(Object o) { throw new UnsupportedOperationException(); }
	@Override public int lastIndexOf(Object o) { throw new UnsupportedOperationException(); }
	@Override public ListIterator<Value<?>> listIterator() { throw new UnsupportedOperationException(); }
	@Override public ListIterator<Value<?>> listIterator(int index) { throw new UnsupportedOperationException(); }
	@Override public List<Value<?>> subList(int fromIndex, int toIndex) { throw new UnsupportedOperationException(); }
	@Override public Value<?> set(int index, Value<?> element) { throw new UnsupportedOperationException(); }


	public static final int MAX_ARRAY_LENGTH = Integer.MAX_VALUE - 8;

	// Taken from ArraysSupport
	public static int newLength(int oldLength, int minGrowth, int prefGrowth) {
		int newLength = Math.max(minGrowth, prefGrowth) + oldLength;
		if (newLength - MAX_ARRAY_LENGTH <= 0) {
			return newLength;
		}
		return hugeLength(oldLength, minGrowth);
	}

	// Taken from ArraysSupport
	private static int hugeLength(int oldLength, int minGrowth) {
		int minLength = oldLength + minGrowth;
		if (minLength < 0) { // overflow
			throw new OutOfMemoryError("Required array length too large");
		}
		if (minLength <= MAX_ARRAY_LENGTH) {
			return MAX_ARRAY_LENGTH;
		}
		return Integer.MAX_VALUE;
	}
}
