package me.senseiwells.arucas.utils.impl;

import me.senseiwells.arucas.values.Value;

import java.util.Collection;

public interface IArucasCollection {
	Collection<? extends Value<?>> asCollection();
	int size();
}
