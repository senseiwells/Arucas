package me.senseiwells.arucas.api;

import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.functions.MemberFunction;

import java.util.Set;

public interface IArucasValueExtension {
	
	/**
	 * Returns a set of unique function objects.
	 */
	Set<? extends MemberFunction> getDefinedFunctions();
	
	/**
	 * Returns the name of this extension.
	 */
	String getName();
	
	/**
	 * Returns the value type of this extension.
	 */
	Class<? extends Value<?>> getValueType();
}
