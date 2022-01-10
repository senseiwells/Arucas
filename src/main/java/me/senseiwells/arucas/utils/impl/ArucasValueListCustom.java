package me.senseiwells.arucas.utils.impl;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.StringUtils;
import me.senseiwells.arucas.values.Value;

import java.util.*;

/**
 * Custom list implementation
 * Most of the code was taken from ArrayList
 */

// TODO: rename to ArucasValueList and delete the old one
public class ArucasValueListCustom implements Iterable<Value<?>> {
	private static final Value<?>[] DEFAULT_DATA = {};
	private static final int DEFAULT_CAPACITY = 10;

	private Value<?>[] valueData;

	private int size;

	public ArucasValueListCustom() {
		this.valueData = DEFAULT_DATA;
	}

	public ArucasValueListCustom(int initialCapacity) {
		if (initialCapacity > 0) {
			this.valueData = new Value[initialCapacity];
		}
		else if (initialCapacity == 0) {
			this.valueData = DEFAULT_DATA;
		}
		else {
			throw new IllegalArgumentException("Illegal Capacity: "+ initialCapacity);
		}
	}

	public ArucasValueListCustom(ArucasValueListCustom valueList) {
		Value<?>[] valueArray = valueList.toArray();
		this.size = valueArray.length;
		this.valueData = this.size == 0 ? DEFAULT_DATA : valueArray;
	}

	public synchronized int size() {
		return this.size;
	}

	public synchronized boolean isEmpty() {
		return this.size == 0;
	}

	public synchronized Value<?> get(int index) {
		this.checkExistingIndex(index);
		return this.valueData[index];
	}

	public synchronized boolean contains(Context context, Value<?> value) throws CodeError {
		return this.indexOf(context, value) >= 0;
	}

	public synchronized boolean containsAll(Context context, ArucasValueListCustom valueList) throws CodeError {
		for (Value<?> value : valueList) {
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

	public synchronized boolean add(Value<?> value) {
		this.add(value, this.valueData, this.size);
		return true;
	}

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

	public synchronized void addAll(ArucasValueListCustom valueList) {
		this.addAll(valueList.toArray());
	}

	public synchronized void addAll(Collection<? extends Value<?>> values) {
		this.addAll(values.toArray(Value[]::new));
	}

	private synchronized void addAll(Value<?>[] valueArray) {
		int newSize = valueArray.length;
		if (newSize == 0) {
			return;
		}
		Value<?>[] valueData = this.valueData;
		final int size = this.size;
		if (newSize > valueData.length - size) {
			valueData = this.grow(size + newSize);
		}
		System.arraycopy(valueArray, 0, valueData, size, newSize);
		this.size = size + newSize;
	}

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

	@SuppressWarnings("unused")
	public synchronized boolean removeAll(Collection<?> collection) {
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

	public synchronized void clear() {
		final Value<?>[] valueData = this.valueData;
		for (int to = this.size, i = this.size = 0; i < to; i++) {
			valueData[i] = null;
		}
	}

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

	public synchronized int getHashCode(Context context) throws CodeError {
		final Value<?>[] valueData = this.valueData;
		int hashCode = 1;
		for (int i = 0; i < this.size; i++) {
			Value<?> value = valueData[i];
			hashCode = 31 * hashCode + (value == null ? 0 : value.getHashCode(context));
		}
		return hashCode;
	}

	public synchronized boolean isEquals(Context context, ArucasValueListCustom other) throws CodeError {
		if (this == other || this.size != other.size) {
			return false;
		}
		for (int i = 0; i < this.size; i++) {
			if (!this.valueData[i].isEquals(context, other.valueData[i])) {
				return false;
			}
		}
		return true;
	}

	public synchronized String getStringValue(Context context) throws CodeError {
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
				return this.cursor != ArucasValueListCustom.this.size;
			}

			@Override
			public synchronized Value<?> next() {
				final int i = this.cursor;
				if (i >= ArucasValueListCustom.this.size) {
					throw new NoSuchElementException();
				}
				Value<?>[] valueData = ArucasValueListCustom.this.valueData;
				this.cursor = i + 1;
				return valueData[i];
			}
		};
	}


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
