package me.senseiwells.arucas.values;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Context;

/**
 * This class is intended to replace the Java
 * {@link #toString()},
 * {@link #hashCode()},
 * {@link #equals(Object)},
 * with methods that pass in context, for Arucas Values.
 * When implementing this you should make
 * these methods final and deprecated
 */
public interface ValueIdentifier {

	/**
	 * This method returns the string representation of this object
	 */
	String getAsString(Context context) throws CodeError;

	/**
	 * This method returns the hashcode of this object
	 */
	int getHashCode(Context context) throws CodeError;

	/**
	 * This method returns whether this object equals another object
	 */
	boolean isEquals(Context context, Value<?> other) throws CodeError;

	/**
	 * This method returns whether this object is not equal to another
	 */
	default boolean isNotEquals(Context context, Value<?> other) throws CodeError {
		return !this.isEquals(context, other);
	}
}
