package me.senseiwells.arucas.api;

import me.senseiwells.arucas.extensions.BuiltInFunction;

import java.util.Set;

public interface IArucasExtension {
	
	/**
	 * Returns a set of unique function objects.
	 */
	Set<BuiltInFunction> getDefinedFunctions();
	
	/**
	 * Returns the name of this extension.
	 */
	String getName();
}
