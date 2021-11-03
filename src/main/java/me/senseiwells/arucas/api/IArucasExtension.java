package me.senseiwells.arucas.api;

import me.senseiwells.arucas.values.functions.AbstractBuiltInFunction;

import java.util.Set;

public interface IArucasExtension {
	
	/**
	 * Returns a set of unique function objects.
	 */
	Set<? extends AbstractBuiltInFunction<?>> getDefinedFunctions();
	
	/**
	 * Returns the name of this extension.
	 */
	String getName();
}
