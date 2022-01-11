package me.senseiwells.arucas.values;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Context;

/**
 * This class is intended to replace the Java
 * {@link #toString()},
 * {@link #hashCode()},
 * {@link #equals(Object)},
 * with methods that pass in context,
 * for Arucas Values.
 * When implementing this you should make
 * these methods final and deprecated
 */
public interface ValueIdentifier {

	/**
	 * This method should return the string
	 * representation of an object
	 */
	String getAsString(Context context) throws CodeError;

	/**
	 * This method should return the
	 * hashcode of an object
	 */
	int getHashCode(Context context) throws CodeError;

	/**
	 * This method should return
	 * whether two objects are equal
	 */
	boolean isEquals(Context context, Value<?> other) throws CodeError;
}
