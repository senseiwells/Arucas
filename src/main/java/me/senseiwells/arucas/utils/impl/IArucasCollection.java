package me.senseiwells.arucas.utils.impl;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.Value;

import java.util.Collection;
import java.util.Iterator;

public interface IArucasCollection extends IArucasIterable {
	String COLLECTION = "<collection>";

	Collection<Value> asCollection();

	default int size() {
		return this.asCollection().size();
	}

	default String getAsStringSafe() {
		return COLLECTION;
	}

	default String getAsStringUnsafe(Context context, ISyntax position) throws CodeError {
		try {
			StringBuilder builder = new StringBuilder();

			Iterator<Value> iterator = this.iterator();
			while (iterator.hasNext()) {
				Value value = iterator.next();
				String valueAsString = value.isCollection() ?
					value.asCollection(context, position).getAsStringUnsafe(context, position) : value.getAsString(context);
				builder.append(valueAsString);

				if (iterator.hasNext()) {
					builder.append(", ");
				}
			}

			return builder.toString();
		}
		catch (StackOverflowError e) {
			throw new RuntimeError("StackOverflow: String evaluation went too deep", position, context);
		}
	}

	@Override
	default Iterator<Value> iterator() {
		return this.asCollection().iterator();
	}
}
