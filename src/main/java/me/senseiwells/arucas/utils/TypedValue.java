package me.senseiwells.arucas.utils;

import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.classes.AbstractClassDefinition;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class TypedValue {
	public final List<AbstractClassDefinition> definitions;
	public Value value;

	public TypedValue(List<AbstractClassDefinition> definition, Value value) {
		this.definitions = definition;
		this.value = value;
	}

	public static String typesAsString(Collection<AbstractClassDefinition> collection) {
		StringBuilder builder = new StringBuilder();
		Iterator<AbstractClassDefinition> iterator = collection.iterator();
		while (iterator.hasNext()) {
			AbstractClassDefinition definition = iterator.next();
			builder.append(definition.getName());

			if (iterator.hasNext()) {
				builder.append(" | ");
			}
		}
		return builder.toString();
	}
}
