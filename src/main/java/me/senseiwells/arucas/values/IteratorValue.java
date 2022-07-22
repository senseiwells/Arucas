package me.senseiwells.arucas.values;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.ValueTypes;
import me.senseiwells.arucas.utils.impl.IArucasIterable;

import java.util.Iterator;
import java.util.function.Supplier;

public class IteratorValue extends GenericValue<Supplier<Iterator<Value>>> implements IArucasIterable {
	public IteratorValue(Supplier<Iterator<Value>> value) {
		super(value);
	}

	@Override
	public IteratorValue copy(Context context) throws CodeError {
		return this;
	}

	@Override
	public boolean isIterable() {
		return true;
	}

	@Override
	public IArucasIterable asIterable(Context context, ISyntax syntaxPosition) {
		return this;
	}

	@Override
	public String getAsString(Context context) throws CodeError {
		return "<iterator>";
	}

	@Override
	public int getHashCode(Context context) throws CodeError {
		return System.identityHashCode(this);
	}

	@Override
	public boolean isEquals(Context context, Value other) throws CodeError {
		return this == other;
	}

	@Override
	public String getTypeName() {
		return ValueTypes.ITERATOR;
	}

	@Override
	public Iterator<Value> iterator() {
		return this.value.get();
	}
}
