package me.senseiwells.arucas.utils;

import me.senseiwells.arucas.values.NumberValue;
import me.senseiwells.arucas.values.Value;

import java.util.Iterator;

public class RangeIterator implements Iterator<Value> {
	private final double end;
	private final double step;
	private double current;

	public RangeIterator(double start, double end, double step) {
		this.end = end;
		this.step = step;
		this.current = start;
	}

	@Override
	public boolean hasNext() {
		return this.current < this.end;
	}

	@Override
	public Value next() {
		Value value = NumberValue.of(this.current);
		this.current += this.step;
		return value;
	}
}
