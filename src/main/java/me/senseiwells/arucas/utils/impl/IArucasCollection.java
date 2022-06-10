package me.senseiwells.arucas.utils.impl;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.Value;

import java.util.Collection;

public interface IArucasCollection {
	String COLLECTION = "<collection>";

	Collection<? extends Value> asCollection();

	default int size() {
		return this.asCollection().size();
	}

	default String getAsStringSafe() {
		return COLLECTION;
	}

	default String getAsStringUnsafe(Context context, ISyntax position) throws CodeError {
		try {
			StringBuilder builder = new StringBuilder();

			Collection<? extends Value> values = this.asCollection();

			for (Value value : values) {
				String valueAsString = value.isCollection() ?
					value.asCollection(context, position).getAsStringUnsafe(context, position) : value.getAsString(context);
				builder.append(valueAsString).append(", ");
			}

			if (!values.isEmpty()) {
				builder.delete(builder.length() - 2, builder.length());
			}

			return builder.toString();
		}
		catch (StackOverflowError e) {
			throw new RuntimeError("StackOverflow: String evaluation went too deep", position, context);
		}
	}
}
