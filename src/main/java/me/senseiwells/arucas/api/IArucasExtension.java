package me.senseiwells.arucas.api;

import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.values.functions.BuiltInFunction;

public interface IArucasExtension {
	
	/**
	 * Returns a set of unique function objects.
	 */
	ArucasFunctionMap<? extends BuiltInFunction> getDefinedFunctions();
	
	/**
	 * Returns the name of this extension.
	 */
	String getName();
}
